#!/bin/bash

mkdir -p /opt/tomato

# copy executable
echo "/app/share/tomato-${GIT_TAG}.jar" >> /opt/build-linux/tomato.sh

# copy desktop entry
echo "Version=${GIT_TAG}" >> /opt/build-linux/tomato.desktop

flatpak-builder --install-deps-from=flathub --repo=repo \
  build-dir io.github.clagomess.Tomato.yml

flatpak build-bundle repo \
  tomato-${GIT_TAG}-x64.flatpak \
  io.github.clagomess.Tomato

cp tomato-${GIT_TAG}-x64.flatpak /opt/result

# install (Test Only)
# sudo service dbus start

# flatpak install --user tomato-*.flatpak
# flatpak run io.github.clagomess.Tomato
# flatpak remove io.github.clagomess.Tomato
