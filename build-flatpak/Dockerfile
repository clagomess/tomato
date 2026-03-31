FROM alpine:3

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-flatpak"

ENV GIT_TAG="0.0.0"

WORKDIR /opt/build-flatpak

RUN apk add --no-cache flatpak-builder curl

RUN flatpak remote-add \
    --if-not-exists \
    flathub https://dl.flathub.org/repo/flathub.flatpakrepo

COPY build-flatpak /opt/build-flatpak

COPY --from=ghcr.io/clagomess/tomato-build-linux \
    /opt/build-linux \
    /opt/build-linux

CMD ["sh", "-xe", "/opt/build-flatpak/build-flatpak.sh"]
