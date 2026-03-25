# build-macos

Build image `ghcr.io/clagomess/tomato-build-macos`:

```bash
docker build --progress=plain \
-t ghcr.io/clagomess/tomato-build-macos \
-f build-macos/Dockerfile .

docker push ghcr.io/clagomess/tomato-build-macos:latest
```

Build MACOS icon:
```bash
docker run --rm -v ./build-macos:/opt/release \
ghcr.io/clagomess/tomato-build-macos

cd build-macos && iconutil -c icns favicon.iconset
```
