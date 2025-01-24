#!/bin/bash

# prepare
rm -rf /tmp/tomato
mkdir -p /tmp/tomato
git_tag=`git describe --tags --abbrev=0 | sed 's/v//'`

# base dirs
mkdir -p /tmp/tomato/DEBIAN
mkdir -p /tmp/tomato/usr/share/tomato
mkdir -p /tmp/tomato/usr/bin

# copy release
cp -R ./target/release/** /tmp/tomato/usr/share/tomato

# copy executable
cp ./build-debian/tomato.sh /tmp/tomato/usr/bin/tomato
echo "/usr/share/tomato/tomato-$git_tag.jar" >> /tmp/tomato/usr/bin/tomato
chmod +x /tmp/tomato/usr/bin/tomato

# copy icons
mkdir -p /tmp/tomato/usr/share/icons/hicolor/64x64/apps
mkdir -p /tmp/tomato/usr/share/icons/hicolor/128x128/apps
cp ./build-debian/icon-64x64.png /tmp/tomato/usr/share/icons/hicolor/64x64/apps/tomato.png
cp ./build-debian/icon-128x128.png /tmp/tomato/usr/share/icons/hicolor/128x128/apps/tomato.png

# copy desktop entry
mkdir -p /tmp/tomato/usr/share/applications
echo "Version=$git_tag" >> /tmp/tomato/usr/share/applications/tomato.desktop
cp ./build-debian/tomato.desktop /tmp/tomato/usr/share/applications/tomato.desktop

# copy control
cp ./build-debian/control /tmp/tomato/DEBIAN/
echo "Version: $git_tag" >> /tmp/tomato/DEBIAN/control
chmod -R 0755 /tmp/tomato/DEBIAN

# build
dpkg-deb --build /tmp/tomato

# install (Test Only)
# sudo apt install /tmp/tomato.deb -y
