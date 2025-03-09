#!/bin/bash

# sudo dnf install -y rpmdevtools rpmlint

GIT_TAG=$(git describe --tags --abbrev=0 | sed 's/v//')

rm -rf ~/rpmbuild
rpmdev-setuptree

# define tarball content
TOMATO_TARBALL="${HOME}/rpmbuild/SOURCES/tomato-${GIT_TAG}"
mkdir -p "${TOMATO_TARBALL}/release"
mkdir -p "${TOMATO_TARBALL}/desktop"

# copy release
cp -R ./target/release/** "${TOMATO_TARBALL}/release"
cp ./build-fedora/tomato.sh "${TOMATO_TARBALL}/desktop"

# copy icons
cp ./build-fedora/icon-64x64.png "${TOMATO_TARBALL}/desktop"
cp ./build-fedora/icon-128x128.png "${TOMATO_TARBALL}/desktop"

# copy desktop entry
cp ./build-fedora/tomato.desktop "${TOMATO_TARBALL}/desktop"

# tar
tar -cvf "${HOME}/rpmbuild/SOURCES/tomato-${GIT_TAG}.tar.gz" -C ~/rpmbuild/SOURCES .
rm -rf "${TOMATO_TARBALL}"

# copy spec
cp ./build-fedora/tomato.spec ~/rpmbuild/SPECS
sed -i "s/0.0.0/${GIT_TAG}/" ~/rpmbuild/SPECS/tomato.spec
echo "* $(date '+%a %b %d %Y') Cláudio Gomes <cla.gomess@gmail.com>" >> ~/rpmbuild/SPECS/tomato.spec
echo "- ${GIT_TAG}" >> ~/rpmbuild/SPECS/tomato.spec

# build
rpmlint ~/rpmbuild/SPECS/tomato.spec
rpmbuild -bb ~/rpmbuild/SPECS/tomato.spec

# install (Test Only)
# sudo dnf install ~/rpmbuild/RPMS/noarch/tomato-*.rpm
