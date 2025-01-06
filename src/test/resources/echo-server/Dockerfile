FROM docker.io/php:8.2-apache

LABEL org.opencontainers.image.source=https://github.com/clagomess/tomato

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt update \
    && apt install ssl-cert -y

RUN a2enmod ssl
RUN a2ensite default-ssl

RUN make-ssl-cert generate-default-snakeoil
