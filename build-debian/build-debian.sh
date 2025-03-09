#!/bin/bash

# prepare
GIT_TAG=$(git describe --tags --abbrev=0 | sed 's/v//')

rm -rf /tmp/tomato**
mkdir -p /tmp/tomato/DEBIAN
mkdir -p /tmp/tomato/usr/share/doc/tomato
mkdir -p /tmp/tomato/usr/share/tomato
mkdir -p /tmp/tomato/usr/bin

# copy release
cp -R ../target/release/** /tmp/tomato/usr/share/tomato

# copy executable
cp ../build-linux/tomato.sh /tmp/tomato/usr/bin/tomato
echo "/usr/share/tomato/tomato-${GIT_TAG}.jar" >> /tmp/tomato/usr/bin/tomato
chmod +x /tmp/tomato/usr/bin/tomato

# copy icons
mkdir -p /tmp/tomato/usr/share/icons/hicolor/64x64/apps
mkdir -p /tmp/tomato/usr/share/icons/hicolor/128x128/apps
cp ../build-linux/icon-64x64.png /tmp/tomato/usr/share/icons/hicolor/64x64/apps/tomato.png
cp ../build-linux/icon-128x128.png /tmp/tomato/usr/share/icons/hicolor/128x128/apps/tomato.png

# copy desktop entry
mkdir -p /tmp/tomato/usr/share/applications
cp ../build-linux/tomato.desktop /tmp/tomato/usr/share/applications/tomato.desktop
echo "Version=${GIT_TAG}" >> /tmp/tomato/usr/share/applications/tomato.desktop

# copy control
cp control /tmp/tomato/DEBIAN/
echo "Version: ${GIT_TAG}" >> /tmp/tomato/DEBIAN/control
chmod -R 755 /tmp/tomato/DEBIAN

# copy copyright
cp copyright /tmp/tomato/usr/share/doc/tomato/

# build
dpkg-deb --build /tmp/tomato
mv /tmp/tomato.deb "/tmp/tomato-${GIT_TAG}-all.deb"

# install (Test Only)
# sudo apt install /tmp/tomato.deb -y
