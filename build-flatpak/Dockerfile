FROM debian:12

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-flatpak"

ENV DEBIAN_FRONTEND=noninteractive
ENV GIT_TAG="0.0.0"

WORKDIR /opt/build-flatpak

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt update  \
    && apt install flatpak-builder curl -y

RUN cd /opt && \
    curl -L https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.14%2B7/OpenJDK17U-jre_x64_linux_hotspot_17.0.14_7.tar.gz \
    -o temurin-17-jre.tar.gz

RUN flatpak remote-add \
    --if-not-exists \
    flathub https://dl.flathub.org/repo/flathub.flatpakrepo

RUN flatpak install -y flathub org.freedesktop.Sdk//24.08
RUN flatpak install -y flathub org.freedesktop.Platform//24.08

COPY build-flatpak /opt/build-flatpak
COPY build-linux /opt/build-linux

CMD ["sh", "-xe", "/opt/build-flatpak/build-flatpak.sh"]
