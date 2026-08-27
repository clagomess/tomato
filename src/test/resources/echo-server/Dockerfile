FROM docker.io/php:8.2-apache

LABEL org.opencontainers.image.source=https://github.com/clagomess/tomato

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt update \
    && apt install ssl-cert ssh -y

RUN a2enmod ssl
RUN a2ensite default-ssl

RUN make-ssl-cert generate-default-snakeoil

# ssh user
RUN useradd -m -s /bin/bash -d /var/www/html php && \
    echo "php:php" | chpasswd && \
    usermod -aG www-data php

RUN echo "service ssh start & \\" > /opt/init.sh && \
    echo "apachectl -D FOREGROUND" >> /opt/init.sh

CMD ["bash", "/opt/init.sh"]
