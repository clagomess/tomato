package com.github.clagomess.tomato.io.keepass;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.linguafranca.pwdb.base.AbstractEntry;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.jackson.JacksonDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class EnvironmentSecret {
    private static final String FILE_NAME = "environment-%s.kdbx";

    @Getter(AccessLevel.PROTECTED)
    private final File databaseFile;

    @Setter
    private Supplier<String> getPassword = () -> null;

    @Setter
    private Supplier<String> getNewPassword = () -> null;

    public EnvironmentSecret(
            File workspacePath,
            String environmentId
    ) {
        this.databaseFile = new File(
                workspacePath,
                String.format(FILE_NAME, environmentId)
        );
    }

    protected KdbxCreds getCredential(String password) {
        if(StringUtils.isBlank(password)){
            throw new IllegalArgumentException("The password is blank");
        }

        return new KdbxCreds(password.getBytes());
    }

    protected JacksonDatabase getDatabase() throws IOException {
        if (databaseFile.isFile()) {
            try(FileInputStream fis = new FileInputStream(databaseFile)) {
                return JacksonDatabase.load(
                        getCredential(getPassword.get()),
                        fis
                );
            }
        }else{
            return new JacksonDatabase();
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

    public UUID saveEntry(
            @Nullable UUID entryId,
            @NotNull String key,
            @NotNull String value
    ) throws IOException {
        var database = getDatabase();

        if(entryId == null) entryId = UUID.randomUUID();

        var entry = database.newEntry();
        entry.setTitle(key);
        entry.setUsername(entryId.toString());
        entry.setPassword(value);

        removeEntry(database, entryId);
        database.getRootGroup().addEntry(entry);

        KdbxCreds credential = getCredential(databaseFile.isFile() ?
                getPassword.get() :
                getNewPassword.get()
        );

        saveDatabase(database, credential);

        return entryId;
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
        saveDatabase(database, getCredential(getNewPassword.get()));
    }
}
