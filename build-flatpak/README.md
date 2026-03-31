# build-flatpak

Build image `ghcr.io/clagomess/tomato-build-flatpak`:

```bash
docker build --progress=plain \
-t ghcr.io/clagomess/tomato-build-flatpak \
-f build-flatpak/Dockerfile .

docker push ghcr.io/clagomess/tomato-build-flatpak:latest
```

Build:

```bash
docker run --rm --privileged \
-v "./build-flatpak:/opt/result" \
-v "./target/tomato-1.0.0:/opt/release" \
-e GIT_TAG=1.0.0 \
ghcr.io/clagomess/tomato-build-flatpak
```
