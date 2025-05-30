# build-fedora

Build image `ghcr.io/clagomess/tomato-build-fedora`:

```bash
docker build --progress=plain \
-t ghcr.io/clagomess/tomato-build-fedora \
-f build-fedora/Dockerfile .

docker push ghcr.io/clagomess/tomato-build-fedora:latest
```

Build:

```bash
docker run --rm \
-v "./build-fedora:/opt/result" \
-v "./target/release:/opt/release" \
-e GIT_TAG=0.0.0 \
ghcr.io/clagomess/tomato-build-fedora
```
