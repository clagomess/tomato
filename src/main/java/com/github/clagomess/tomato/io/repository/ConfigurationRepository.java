package com.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.clagomess.tomato.dto.data.ConfigurationDto;
import com.github.clagomess.tomato.util.CacheManager;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ConfigurationRepository extends AbstractRepository {
    protected static final CacheManager<String, ConfigurationDto> cache = new CacheManager<>("configuration");

    protected ConfigurationDto load() throws IOException {
        return cache.get(() -> {
            var file = new File(
                    getHomeDir(),
                    "configuration.json"
            );

            Optional<ConfigurationDto> configuration = readFile(
                    file,
                    new TypeReference<>(){}
            );

            if (configuration.isPresent()) {
                return configuration.get();
            }

            var defaultConfiguration = new ConfigurationDto();
            defaultConfiguration.setDataDirectory(new File(
                    getHomeDir(),
                    "data"
            ));

            writeFile(file, defaultConfiguration);

            return defaultConfiguration;
        });
    }

    protected static final CacheManager<String, File> cacheDatadir = new CacheManager<>("dataDir");
    protected File getDataDir() throws IOException {
        return cacheDatadir.get(() -> {
            if(StringUtils.isNotBlank(System.getProperty("TOMATO_AWAYS_USE_TEST_DATA"))){
                return new File("src/test/resources/com/github/clagomess/tomato/io/repository/home/data");
            }

            return createDirectoryIfNotExists(
                    load().getDataDirectory()
            );
        });
    }
}
