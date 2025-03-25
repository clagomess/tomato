#!/bin/bash

# sudo apt install rpm rpmlint

GIT_TAG=$(git describe --tags --abbrev=0 | sed 's/v//')

rm -rf ~/rpmbuild

# define tarball content
TOMATO_TARBALL="${HOME}/rpmbuild/SOURCES/tomato-${GIT_TAG}"
mkdir -p "${TOMATO_TARBALL}/release"
mkdir -p "${TOMATO_TARBALL}/desktop"

# copy release
cp -R ../target/release/** "${TOMATO_TARBALL}/release"
cp ../build-linux/tomato.sh "${TOMATO_TARBALL}/desktop"

# copy desktop assets
cp ../build-linux/icon-64x64.png "${TOMATO_TARBALL}/desktop"
cp ../build-linux/icon-128x128.png "${TOMATO_TARBALL}/desktop"
cp ../build-linux/tomato.desktop "${TOMATO_TARBALL}/desktop"

# tar
tar -cvf "${HOME}/rpmbuild/SOURCES/tomato-${GIT_TAG}.tar.gz" -C ~/rpmbuild/SOURCES .
rm -rf "${TOMATO_TARBALL}"

# copy spec
mkdir -p ~/rpmbuild/SPECS
cp tomato.spec ~/rpmbuild/SPECS/
sed -i "s/0.0.0/${GIT_TAG}/" ~/rpmbuild/SPECS/tomato.spec
echo "* $(date '+%a %b %d %Y') Cláudio Gomes <cla.gomess@gmail.com>" >> ~/rpmbuild/SPECS/tomato.spec
echo "- ${GIT_TAG}" >> ~/rpmbuild/SPECS/tomato.spec

# build
rpmlint ~/rpmbuild/SPECS/tomato.spec
rpmbuild -bb ~/rpmbuild/SPECS/tomato.spec
cp ~/rpmbuild/RPMS/noarch/tomato-*.rpm .

# install (Test Only)
# sudo dnf install ~/rpmbuild/RPMS/noarch/tomato-*.rpm
