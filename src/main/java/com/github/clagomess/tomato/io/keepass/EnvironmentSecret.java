package com.github.clagomess.tomato.io.keepass;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.util.CacheManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.linguafranca.pwdb.base.AbstractEntry;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.jackson.JacksonDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class EnvironmentSecret {
    private static final String FILE_NAME = "environment-%s.kdbx";
    private static final String MSG_BLANK_PASSWORD = "Password is blank";

    @Getter(AccessLevel.PROTECTED)
    private final File databaseFile;

    @Setter
    private Supplier<String> getPassword = () -> {
        throw new RuntimeException("Needs override 'getPassword'");
    };

    @Setter
    private Supplier<String> getNewPassword = () -> {
        throw new RuntimeException("Needs override 'getNewPassword'");
    };

    public EnvironmentSecret(
            File workspacePath,
            String environmentId
    ) {
        this.databaseFile = new File(
                workspacePath,
                String.format(FILE_NAME, environmentId)
        );
    }

    protected static final CacheManager<File, KdbxCreds> credentialCache = new CacheManager<>();
    protected KdbxCreds getCredential() {
        return credentialCache.get(databaseFile, () -> {
            String password = getPassword.get();

            if(StringUtils.isBlank(password)){
                throw new IllegalArgumentException(MSG_BLANK_PASSWORD);
            }

            return new KdbxCreds(password.getBytes());
        });
    }

    protected KdbxCreds getNewCredential() {
        credentialCache.evict(databaseFile);
        String password = getNewPassword.get();

        if(StringUtils.isBlank(password)){
            throw new IllegalArgumentException(MSG_BLANK_PASSWORD);
        }

        return new KdbxCreds(password.getBytes());
    }

    protected JacksonDatabase getDatabase() throws IOException {
        if(!databaseFile.isFile()) return new JacksonDatabase();

        try (FileInputStream fis = new FileInputStream(databaseFile)) {
            return JacksonDatabase.load(
                    getCredential(),
                    fis
            );
        } catch (IllegalStateException e) {
            credentialCache.evict(databaseFile);
            throw new IllegalStateException("Invalid password or corrupted keystore", e);
        } catch (IOException e) {
            credentialCache.evict(databaseFile);
            throw e;
        }
    }

    protected void saveDatabase(
            JacksonDatabase database,
            KdbxCreds credential
    ) throws IOException {
        if(databaseFile.isFile()) {
            var backupFile = new File(
                    databaseFile.getParentFile(),
                    databaseFile.getName() + ".bkp"
            );

            if(backupFile.isFile() && !backupFile.delete()){
                throw new IOException("Failed to delete backup file");
            }

            Files.copy(databaseFile.toPath(), backupFile.toPath());
        }

        try(FileOutputStream fos = new FileOutputStream(databaseFile)) {
            database.save(credential, fos);
        }
    }

    protected void removeEntry(
            JacksonDatabase database,
            UUID entryId
    ) {
        database.getRootGroup()
                .findEntries(item -> Objects.equals(
                        item.getUsername(),
                        entryId.toString()
                ), false)
                .forEach(entry ->
                    database.getRootGroup().removeEntry(entry)
                );
    }

    public List<Entry> saveEntries(
            @NotNull List<Entry> entries
    ) throws IOException {
        if(entries.isEmpty()) return entries;

        var database = getDatabase();

        for(var item : entries){
            if(item.getEntryId() == null){
                item.setEntryId(UUID.randomUUID());
            }

            var entry = database.newEntry();
            entry.setTitle(item.getKey());
            entry.setUsername(item.getEntryId().toString());
            entry.setPassword(item.getValue());

            removeEntry(database, item.getEntryId());
            database.getRootGroup().addEntry(entry);
        }

        KdbxCreds credential = databaseFile.isFile() ?
                getCredential() :
                getNewCredential();

        saveDatabase(database, credential);

        return entries;
    }

    public Optional<String> loadSecret(
            @NotNull UUID entryId
    ) throws IOException {
        var database = getDatabase();

        return database.getRootGroup()
                .findEntries(item -> Objects.equals(
                        item.getUsername(),
                        entryId.toString()
                ), false).stream()
                .findFirst()
                .map(AbstractEntry::getPassword);
    }

    public void changeDatabasePassword() throws IOException {
        var database = getDatabase();
        saveDatabase(database, getNewCredential());
    }

    @Getter
    @AllArgsConstructor
    public static class Entry {
        @Setter
        private UUID entryId;
        private final String key;
        private final String value;

        public Entry(EnvironmentItemDto item) {
            this.entryId = item.getSecretId();
            this.key = item.getKey();
            this.value = item.getValue();
        }
    }
}
