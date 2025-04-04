package com.github.clagomess.tomato.io.keepass;

import com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemDto;
import com.github.clagomess.tomato.io.repository.RepositoryStubs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.jackson.JacksonDatabase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.github.clagomess.tomato.dto.data.keyvalue.EnvironmentItemTypeEnum.SECRET;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class EnvironmentSecretTest extends RepositoryStubs {
    private EnvironmentSecret environmentSecretsEmpty;
    private EnvironmentSecret environmentSecretsExisting;

    @BeforeEach
    public void setup() {
        EnvironmentSecret.databaseCache.evictAll();
        EnvironmentSecret.credentialCache.evictAll();

        environmentSecretsEmpty = new EnvironmentSecret(
                mockDataDir,
                RandomStringUtils.secure().nextAlphanumeric(8)
        );

        environmentSecretsEmpty.setGetPassword(() -> "supersecret");
        environmentSecretsEmpty.setGetNewPassword(() -> "supersecret");

        environmentSecretsExisting = new EnvironmentSecret(
                new File(testData, "workspace-nPUaq0TC"),
                "7rZO7Z1T"
        );
        environmentSecretsExisting.setGetPassword(() -> "supersecret");
    }

    @Nested
    class getDatabase {
        @Test
        public void whenNewDatabase() throws IOException {
            var result = environmentSecretsEmpty.getDatabase();
            assertNotNull(result);
        }

        @Test
        public void whenExistingDatabase() throws IOException {
            var result = environmentSecretsExisting.getDatabase();
            assertNotNull(result);
        }

        @Test
        public void whenExistingDatabaseAndWrongPassword() {
            environmentSecretsExisting.setGetPassword(() -> "wrongpassword");

            assertThrows(
                    IllegalStateException.class,
                    environmentSecretsExisting::getDatabase
            );
        }
    }

    @Nested
    class saveDatabase {
        @Test
        public void whenNewDatabase() throws IOException {
            environmentSecretsEmpty.saveDatabase(
                    new JacksonDatabase(),
                    new KdbxCreds("supersecret".getBytes())
            );

            Assertions.assertThat(environmentSecretsEmpty.getDatabaseFile())
                    .isFile();
        }

        @Test
        public void whenExistingDatabase() throws IOException {
            for(int i = 0; i <= 2; i++) {
                environmentSecretsEmpty.saveDatabase(
                        new JacksonDatabase(),
                        new KdbxCreds("supersecret".getBytes())
                );
            }

            var backupFile = new File(
                    environmentSecretsEmpty.getDatabaseFile().getParentFile(),
                    environmentSecretsEmpty.getDatabaseFile().getName() + ".bkp"
            );

            Assertions.assertThat(environmentSecretsEmpty.getDatabaseFile())
                    .isFile();
            Assertions.assertThat(backupFile)
                    .isFile();
        }
    }

    @Nested
    class saveEntry {
        @Test
        public void whenNewEntry() throws IOException {
            var entries = environmentSecretsEmpty.saveEntries(List.of(new EnvironmentSecret.Entry(
                    null,
                    "token",
                    "mysecrettoken"
            )));

            var database = environmentSecretsEmpty.getDatabase();
            var result = database.getRootGroup().findEntries("token", false)
                    .stream()
                    .findFirst()
                    .orElseThrow();

            assertEquals("token", result.getTitle());
            assertEquals(entries.get(0).getEntryId().toString(), result.getUsername());
            assertEquals("mysecrettoken", result.getPassword());
        }

        @Test
        public void whenExistingEntry() throws IOException {
            var entryId = UUID.randomUUID();

            for(int i = 0; i <= 2; i++) {
                environmentSecretsEmpty.saveEntries(List.of(new EnvironmentSecret.Entry(
                        entryId,
                        "token",
                        "mysecrettoken"
                )));
            }

            var database = environmentSecretsEmpty.getDatabase();
            int result = database.getRootGroup()
                    .findEntries("token", false)
                    .size();

            assertEquals(1, result);
        }
    }

    @Nested
    class loadSecret {
        @Test
        public void loadSecret_byUUID() throws IOException {
            var secret = environmentSecretsExisting.loadSecret(
                    UUID.fromString("28780cff-2570-415f-8e9d-b91d891beb25")
            );

            assertEquals("mysecrettoken", secret.orElseThrow());
        }

        @Test
        public void loadSecret_ByItensWithoutSecretType() throws IOException {
            var input = List.of(new EnvironmentItemDto("key", "value"));
            var result = environmentSecretsExisting.loadSecret(input);
            Assertions.assertThat(result)
                    .isEqualTo(input);
        }

        @Test
        public void loadSecret_ByItensWithSecretType() throws IOException {
            var optA = new EnvironmentItemDto("key", "value");
            var optB = new EnvironmentItemDto(
                    SECRET,
                    UUID.fromString("28780cff-2570-415f-8e9d-b91d891beb25"),
                    "foo",
                    null
            );
            var input = List.of(optA, optB);

            var result = environmentSecretsExisting.loadSecret(input);
            Assertions.assertThat(result)
                    .isNotEqualTo(input)
                    .doesNotContain(optB)
                    .hasSize(2)
            ;

            Assertions.assertThat(result)
                    .filteredOn(item -> item.getType().equals(SECRET))
                    .first()
                    .matches(item -> item != optB) // check new instance
                    .matches(item -> item.getValue().equals("mysecrettoken"));
        }
    }

    @Test
    public void changeDatabasePassword() throws IOException {
        environmentSecretsEmpty.setGetNewPassword(() -> "new-supersecret");
        environmentSecretsEmpty.changeDatabasePassword();

        environmentSecretsEmpty.setGetPassword(() -> "new-supersecret");
        environmentSecretsEmpty.getDatabase();
    }
}
