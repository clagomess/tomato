FROM fedora:42

LABEL org.opencontainers.image.source="https://github.com/clagomess/tomato"
LABEL org.opencontainers.image.description="build-fedora"

ENV GIT_TAG="0.0.0"
ENV APP_ID="io.github.clagomess.Tomato"
ENV RPMBUILD_HOME="/root/rpmbuild"

WORKDIR /opt/build-fedora

RUN dnf install -y rpmdevtools rpmlint

COPY build-fedora /opt/build-fedora

COPY --from=ghcr.io/clagomess/tomato-build-linux \
    /opt/build-linux \
    /opt/build-linux

CMD ["sh", "-xe", "/opt/build-fedora/build-fedora.sh"]
