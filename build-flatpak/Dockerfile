FROM alpine:3

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-flatpak"

ENV GIT_TAG="0.0.0"

WORKDIR /opt/build-flatpak

RUN apk add --no-cache flatpak-builder curl

RUN flatpak remote-add \
    --if-not-exists \
    flathub https://dl.flathub.org/repo/flathub.flatpakrepo

RUN flatpak install -y --no-deps flathub org.freedesktop.Sdk//24.08
RUN flatpak install -y --no-deps flathub org.freedesktop.Platform//24.08

RUN cd /opt && \
    curl -L https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.15%2B6/OpenJDK17U-jre_x64_linux_hotspot_17.0.15_6.tar.gz \
    -o temurin-17-jre.tar.gz

COPY build-flatpak /opt/build-flatpak

COPY --from=ghcr.io/clagomess/tomato-build-linux \
    /opt/build-linux \
    /opt/build-linux

CMD ["sh", "-xe", "/opt/build-flatpak/build-flatpak.sh"]
