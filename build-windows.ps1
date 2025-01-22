# prepare
[System.IO.Directory]::CreateDirectory('build-windows')

# download JDK
If(!(Test-Path -Path 'build-windows/termurim-17.zip')){
    curl -o build-windows/termurim-17.zip https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_windows_hotspot_17.0.13_11.zip
}
If(!(Test-Path -Path 'build-windows/jdk-17.0.13+11')){
    tar -xvf build-windows/termurim-17.zip -C build-windows/
}

# download WIX
If(!(Test-Path -Path 'build-windows/wix314-binaries.zip')){
    curl -o build-windows/wix314-binaries.zip https://github.com/wixtoolset/wix3/releases/download/wix3141rtm/wix314-binaries.zip
}
If(!(Test-Path -Path 'build-windows/wix')){
    [System.IO.Directory]::CreateDirectory('build-windows/wix')
    tar -xvf build-windows/wix314-binaries.zip -C build-windows/wix
}

$env:Path += ";build-windows/wix"

# View Mods
.\build-windows\jdk-17.0.13+11\bin\jdeps `
    ./target/release/tomato-0.0.2.jar

# build
[System.IO.Directory]::CreateDirectory('target/dist')

.\build-windows\jdk-17.0.13+11\bin\jpackage `
--type msi `
--name Tomato `
--app-version 0.0.2 `
--vendor Tomato `
--icon ./src/main/resources/com/github/clagomess/tomato/ui/component/favicon/favicon.ico `
--input ./target/release `
--main-jar tomato-0.0.2.jar `
--main-class com.github.clagomess.tomato.Main `
--add-modules java.base,java.desktop,java.naming,java.net.http `
--java-options "-splash:`$APPDIR/splash.png" `
--win-dir-chooser `
--win-per-user-install `
--win-menu `
--win-shortcut `
--dest ./target/dist `
--verbose

