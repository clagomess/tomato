#!/bin/bash

# define tarball content
TOMATO_TARBALL="${RPMBUILD_HOME}/SOURCES/tomato-${GIT_TAG}"
rpmdev-setuptree && \
mkdir -pv "${TOMATO_TARBALL}/release"

cp -R /opt/build-linux ${TOMATO_TARBALL}/build-linux

# copy release
cp -R /opt/release/** "${TOMATO_TARBALL}/release"

# tar
tar -cvf "${TOMATO_TARBALL}.tar.gz" -C ${RPMBUILD_HOME}/SOURCES .
rm -rf "${TOMATO_TARBALL}"

# copy spec
cp /opt/build-fedora/tomato.spec ${RPMBUILD_HOME}/SPECS/
sed -i "s/0.0.0/${GIT_TAG}/" ${RPMBUILD_HOME}/SPECS/tomato.spec
echo "* $(date '+%a %b %d %Y') Cláudio Gomes <cla.gomess@gmail.com>" >> ${RPMBUILD_HOME}/SPECS/tomato.spec
echo "- ${GIT_TAG}" >> ${RPMBUILD_HOME}/SPECS/tomato.spec

# build
rpmlint ${RPMBUILD_HOME}/SPECS/tomato.spec
rpmbuild -bb ${RPMBUILD_HOME}/SPECS/tomato.spec
cp ${RPMBUILD_HOME}/RPMS/noarch/tomato-*.rpm /opt/result/

# install (Test Only)
# sudo dnf install ${RPMBUILD_HOME}/RPMS/noarch/tomato-*.rpm
