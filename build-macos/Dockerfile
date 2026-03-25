FROM alpine:3.22 AS build-icons

WORKDIR /opt/build-macos

RUN apk add --no-cache inkscape

RUN mkdir /opt/build-macos/favicon.iconset

COPY ./src/main/resources/io/github/clagomess/tomato/ui/component/favicon/favicon.svg \
    /opt/build-macos/

RUN cd /opt/build-macos/ && \
    inkscape -w 16 -h 16 favicon.svg -o favicon.iconset/icon_16x16.png && \
    inkscape -w 32 -h 32 favicon.svg -o favicon.iconset/icon_16x16@2x.png && \
    inkscape -w 32 -h 32 favicon.svg -o favicon.iconset/icon_32x32.png && \
    inkscape -w 64 -h 64 favicon.svg -o favicon.iconset/icon_32x32@2x.png && \
    inkscape -w 128 -h 128 favicon.svg -o favicon.iconset/icon_128x128.png && \
    inkscape -w 256 -h 256 favicon.svg -o favicon.iconset/icon_128x128@2x.png && \
    inkscape -w 256 -h 256 favicon.svg -o favicon.iconset/icon_256x256.png && \
    inkscape -w 512 -h 512 favicon.svg -o favicon.iconset/icon_256x256@2x.png && \
    inkscape -w 512 -h 512 favicon.svg -o favicon.iconset/icon_512x512.png && \
    inkscape -w 1024 -h 1024 favicon.svg -o favicon.iconset/icon_512x512@2x.png

RUN echo "cp -rv /opt/build-macos/favicon.iconset /opt/release/" > /opt/build-macos/release.sh
RUN chmod +x /opt/build-macos/release.sh

FROM alpine:3.22

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-macos: assets"

COPY --from=build-icons /opt/build-macos /opt/build-macos
CMD ["sh", "-xe", "/opt/build-macos/release.sh"]
