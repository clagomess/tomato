rm -rf /tmp/tomato
mkdir -p /tmp/tomato

# base dirs
mkdir -p /tmp/tomato/DEBIAN
mkdir -p /tmp/tomato/usr/share/tomato
mkdir -p /tmp/tomato/usr/bin

# copy release
cp -R ./target/release/** /tmp/tomato/usr/share/tomato

# copy executable
cp ./build-debian/tomato.sh /tmp/tomato/usr/bin/tomato
chmod +x /tmp/tomato/usr/bin/tomato

# copy icons
mkdir -p /tmp/tomato/usr/share/icons/hicolor/64x64/apps
cp ./build-debian/icon-64x64.png /tmp/tomato/usr/share/icons/hicolor/64x64/apps/tomato.png

# copy desktop entry
mkdir -p /tmp/tomato/usr/share/applications
cp ./build-debian/tomato.desktop /tmp/tomato/usr/share/applications/tomato.desktop

# copy control
cp ./build-debian/control /tmp/tomato/DEBIAN/
chmod -R 0755 /tmp/tomato/DEBIAN

# build
dpkg-deb --build /tmp/tomato

# install (Test Only)
# sudo dpkg -i /tmp/tomato.deb
