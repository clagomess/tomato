#!/bin/bash

# config executable
sed -i "s/\${TOMATO_HOME}/\/app\/share/" /opt/build-linux/tomato.sh
sed -i "s/\${TOMATO_TAG}/${GIT_TAG}/" /opt/build-linux/tomato.sh

flatpak install -y --no-deps flathub \
  org.freedesktop.Sdk//24.08
flatpak install -y --no-deps flathub \
  org.freedesktop.Platform//24.08

flatpak-builder --ccache --force-clean --repo=repo \
  build-dir io.github.clagomess.Tomato.yml

flatpak build-bundle repo \
  tomato-${GIT_TAG}-$(uname -m).flatpak \
  io.github.clagomess.Tomato

cp tomato-${GIT_TAG}-$(uname -m).flatpak /opt/result

# install (Test Only)
# sudo service dbus start

# flatpak install --user tomato-*.flatpak
# flatpak run io.github.clagomess.Tomato
# flatpak remove io.github.clagomess.Tomato
