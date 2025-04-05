package io.github.clagomess.tomato.io.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.clagomess.tomato.dto.data.CollectionDto;
import io.github.clagomess.tomato.dto.tree.CollectionTreeDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class CollectionRepository extends AbstractRepository {
    protected File getCollectionFilePath(File collectionDir, String id){
        return new File(collectionDir, String.format(
                "collection-%s.json",
                id
        ));
    }

    public Optional<CollectionDto> load(CollectionTreeDto collection) throws IOException {
        return readFile(getCollectionFilePath(
                collection.getPath(),
                collection.getId()
        ), new TypeReference<>(){});
    }

    protected Optional<CollectionTreeDto> loadTree(File collectionDir) throws IOException {
        String id = collectionDir.getName().replace("collection-", "");

        return readFile(
                getCollectionFilePath(collectionDir, id),
                new TypeReference<>() {}
        );
    }

    /**
     * @param parentPath example: workspace-xyz/
     * @param collection will write into workspace-xyz/<b>collection-zzz/collection-zzz.json</b>
     * @return collectionDir
     */
    public File save(
            File parentPath,
            CollectionDto collection
    ) throws IOException {
        var collectionDir = createDirectoryIfNotExists(new File(
                parentPath,
                String.format("collection-%s", collection.getId())
        ));

        var collectionFile = getCollectionFilePath(
                collectionDir,
                collection.getId()
        );

        writeFile(collectionFile, collection);

        return collectionDir;
    }

    protected Stream<File> listCollectionFiles(File rootPath) {
        return Arrays.stream(listFiles(rootPath)).parallel()
                .filter(File::isDirectory)
                .filter(item -> item.getName().startsWith("collection"));
    }

    public void move(
            CollectionTreeDto source,
            CollectionTreeDto target
    ) throws IOException {
        move(source.getPath(), target.getPath());
    }

    public void delete(CollectionTreeDto tree) throws IOException {
        deleteDirectory(tree.getPath());
    }
}
