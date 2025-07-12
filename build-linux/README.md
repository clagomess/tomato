# build-linux

Build image `ghcr.io/clagomess/tomato-build-linux`:

```bash
docker build --progress=plain \
-t ghcr.io/clagomess/tomato-build-linux \
-f build-linux/Dockerfile .

docker push ghcr.io/clagomess/tomato-build-linux:latest
```
