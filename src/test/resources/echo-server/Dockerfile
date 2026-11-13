FROM docker.io/php:8.2-apache

LABEL org.opencontainers.image.source=https://github.com/clagomess/tomato

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt update \
    && apt install ssl-cert ssh -y

RUN a2enmod ssl
RUN a2ensite default-ssl

RUN make-ssl-cert generate-default-snakeoil

# mTLS (client certificate required) on port 4443
COPY src/test/resources/echo-server/mtls/ca.crt \
     src/test/resources/echo-server/mtls/server.crt \
     src/test/resources/echo-server/mtls/server.key \
     /etc/apache2/mtls/
COPY src/test/resources/echo-server/mtls/mtls-ssl.conf /etc/apache2/sites-available/
RUN a2ensite mtls-ssl

# ssh user
RUN useradd -m -s /bin/bash -d /var/www/html php && \
    echo "php:php" | chpasswd && \
    usermod -aG www-data php

COPY src/test/resources/echo-server/init.sh /opt/init.sh

CMD ["bash", "/opt/init.sh"]
