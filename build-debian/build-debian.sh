#!/bin/bash

DEBIAN_TARGET="/opt/build-debian/tomato"

# copy release
cp -R /opt/release/** "${DEBIAN_TARGET}/usr/share/tomato/"

# config executable
sed -i "s/\${TOMATO_HOME}/\/usr\/share\/tomato/" "${DEBIAN_TARGET}/usr/bin/tomato"
sed -i "s/\${TOMATO_TAG}/${GIT_TAG}/" "${DEBIAN_TARGET}/usr/bin/tomato"

# config control
echo "Version: ${GIT_TAG}" >> "${DEBIAN_TARGET}/DEBIAN/control"

# build
dpkg-deb --build ${DEBIAN_TARGET}
cp /opt/build-debian/tomato.deb /opt/result/tomato-${GIT_TAG}-all.deb

# install (Test Only)
# sudo apt install ./tomato-0.0.0-all.deb -y
