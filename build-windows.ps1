# prepare
[System.IO.Directory]::CreateDirectory('build-windows')
$git_tag = $(git describe --tags --abbrev=0).Replace('v', '')

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

# build
[System.IO.Directory]::CreateDirectory('target/dist')

.\build-windows\jdk-17.0.13+11\bin\jpackage `
--type msi `
--name Tomato `
--app-version $git_tag `
--vendor Tomato `
--icon ./src/main/resources/com/github/clagomess/tomato/ui/component/favicon/favicon.ico `
--input ./target/release `
--main-jar tomato-$git_tag.jar `
--main-class com.github.clagomess.tomato.Main `
--java-options "-splash:`$APPDIR/splash.png -Dfile.encoding=UTF-8" `
--win-upgrade-uuid "a6f3d5f2-ad83-4fc7-97ce-6444c991e2fe" `
--win-per-user-install `
--win-menu `
--win-shortcut `
--dest ./target/dist `
--verbose

# rename
mv ./target/dist/Tomato-$git_tag.msi ./target/dist/tomato-$git_tag-amd64.msi

# sign
$SignToolPath = Get-ChildItem -Path "C:\Program Files (x86)\Windows Kits\10\bin\**\x64" `
-Recurse -Filter "signtool.exe" | Select-Object -ExpandProperty FullName -First 1
echo "SignTool Path: $SignToolPath"

& $SignToolPath sign /v `
/td SHA256 `
/tr http://timestamp.digicert.com `
/f "./build-windows/code-sign-ks.pfx" `
/p "password" `
/fd SHA256 `
"./target/dist/tomato-$git_tag-amd64.msi"
