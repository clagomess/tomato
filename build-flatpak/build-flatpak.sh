#!/bin/bash

# config executable
sed -i "s/\${TOMATO_HOME}/\/app\/share/" /opt/build-linux/tomato.sh
sed -i "s/\${TOMATO_TAG}/${GIT_TAG}/" /opt/build-linux/tomato.sh

flatpak-builder --ccache --force-clean --repo=repo \
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
