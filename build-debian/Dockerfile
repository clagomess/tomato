FROM debian:12-slim

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-debian"

ENV GIT_TAG="0.0.0"
ARG APP_ID="io.github.clagomess.Tomato"
ARG BUILD_LINUX="/opt/build-linux"
ARG DEBIAN_TARGET="/opt/build-debian/tomato"

WORKDIR /opt/build-debian

RUN mkdir -pv ${DEBIAN_TARGET}/usr/share/tomato

COPY build-debian /opt/build-debian

COPY --from=ghcr.io/clagomess/tomato-build-linux \
    /opt/build-linux \
    /opt/build-linux

# desktop assets
RUN install -v -Dm755 ${BUILD_LINUX}/tomato.sh ${DEBIAN_TARGET}/usr/bin/tomato && \
    install -v -Dm644 ${BUILD_LINUX}/icons/64x64.png ${DEBIAN_TARGET}/usr/share/icons/hicolor/64x64/apps/${APP_ID}.png && \
    install -v -Dm644 ${BUILD_LINUX}/icons/128x128.png ${DEBIAN_TARGET}/usr/share/icons/hicolor/128x128/apps/${APP_ID}.png && \
    install -v -Dm644 ${BUILD_LINUX}/icons/256x256.png ${DEBIAN_TARGET}/usr/share/icons/hicolor/256x256/apps/${APP_ID}.png && \
    install -v -Dm644 ${BUILD_LINUX}/icons/scalable.svg ${DEBIAN_TARGET}/usr/share/icons/hicolor/scalable/apps/${APP_ID}.svg && \
    install -v -Dm644 ${BUILD_LINUX}/${APP_ID}.desktop ${DEBIAN_TARGET}/usr/share/applications/${APP_ID}.desktop

# debian assets
RUN install -v -Dm755 /opt/build-debian/control ${DEBIAN_TARGET}/DEBIAN/control && \
    install -v -Dm644 /opt/build-debian/copyright ${DEBIAN_TARGET}/usr/share/doc/tomato/copyright

CMD ["sh", "-xe", "/opt/build-debian/build-debian.sh"]
