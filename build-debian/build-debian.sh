#!/bin/bash

DEBIAN_TARGET="/opt/build-debian/tomato"

# copy release
cp -R /opt/release/** "${DEBIAN_TARGET}/usr/share/tomato/"

# copy executable
echo "/usr/share/tomato/tomato-${GIT_TAG}.jar" >> "${DEBIAN_TARGET}/usr/bin/tomato"

# copy control
echo "Version: ${GIT_TAG}" >> "${DEBIAN_TARGET}/DEBIAN/control"

# build
dpkg-deb --build ${DEBIAN_TARGET}
cp /opt/build-debian/tomato.deb /opt/result/tomato-${GIT_TAG}-all.deb

# install (Test Only)
# sudo apt install ./tomato-0.0.0-all.deb -y
