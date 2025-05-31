FROM alpine:3 AS build-icons

WORKDIR /opt/build-linux

RUN apk add --no-cache inkscape

RUN mkdir /opt/build-linux/icons

COPY ./src/main/resources/io/github/clagomess/tomato/ui/component/favicon/favicon.svg \
    /opt/build-linux/icons/scalable.svg

COPY ./build-linux/tomato.sh /opt/build-linux/
COPY ./build-linux/io.github.clagomess.Tomato.desktop /opt/build-linux/

RUN cd /opt/build-linux/icons && \
    inkscape -w 64 -h 64 scalable.svg -o 64x64.png && \
    inkscape -w 128 -h 128 scalable.svg -o 128x128.png && \
    inkscape -w 256 -h 256 scalable.svg -o 256x256.png

FROM scratch

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-linux: assets"

COPY --from=build-icons /opt/build-linux /opt/build-linux
