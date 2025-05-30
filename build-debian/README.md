# build-debian

Build image `ghcr.io/clagomess/tomato-build-debian`:

```bash
docker build --progress=plain \
-t ghcr.io/clagomess/tomato-build-debian \
-f build-debian/Dockerfile .

docker push ghcr.io/clagomess/tomato-build-debian:latest
```

Build:

```bash
docker run --rm \
-v "./build-debian:/opt/result" \
-v "./target/release:/opt/release" \
-e GIT_TAG=0.0.0 \
ghcr.io/clagomess/tomato-build-debian
```
