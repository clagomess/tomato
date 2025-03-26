#!/bin/bash

# sudo apt install flatpak-builder

# prepare
GIT_TAG=$(git describe --tags --abbrev=0 | sed 's/v//')

rm -rf /tmp/tomato
mkdir -p /tmp/tomato/release
mkdir -p /tmp/tomato/build-linux

# copy release
cp -R ../target/release/** /tmp/tomato/release/

# copy executable
cp ../build-linux/tomato.sh /tmp/tomato/build-linux/
echo "/app/share/tomato-${GIT_TAG}.jar" >> /tmp/tomato/build-linux/tomato.sh

# copy icons
cp ../build-linux/icon-64x64.png /tmp/tomato/build-linux/
cp ../build-linux/icon-128x128.png /tmp/tomato/build-linux/

# copy desktop entry
cp ../build-linux/tomato.desktop /tmp/tomato/build-linux/
echo "Version=${GIT_TAG}" >> /tmp/tomato/build-linux/tomato.desktop

flatpak remote-add --if-not-exists --user flathub https://dl.flathub.org/repo/flathub.flatpakrepo
flatpak-builder --force-clean --user --install-deps-from=flathub --repo=repo build-dir io.github.clagomess.Tomato.yml
flatpak build-bundle repo tomato-${GIT_TAG}-x64.flatpak io.github.clagomess.Tomato

# install (Test Only)
# sudo service dbus start

# flatpak install --user tomato-*.flatpak
# flatpak run io.github.clagomess.Tomato
# flatpak remove io.github.clagomess.Tomato
