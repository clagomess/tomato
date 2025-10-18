$ErrorActionPreference = "Stop"

# prepare
$git_tag = $(git describe --tags --abbrev=0).Replace('v', '')

# download JDK
If(!(Test-Path -Path 'termurim-17.zip')){
    curl -o termurim-17.zip https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/OpenJDK17U-jdk_x64_windows_hotspot_17.0.13_11.zip
}
If(!(Test-Path -Path 'jdk-17.0.13+11')){
    tar -xvf termurim-17.zip
}

# find signtool
$SignToolPath = Get-ChildItem -Path "C:\Program Files (x86)\Windows Kits\10\bin\**\x64" `
-Recurse -Filter "signtool.exe" | Select-Object -ExpandProperty FullName -First 1
echo "SignTool Path: $SignToolPath"

# download InnoSetup
winget install --id JRSoftware.InnoSetup -e -s winget -i

# build app-image
.\jdk-17.0.13+11\bin\jpackage `
--type app-image `
--name Tomato `
--app-version $git_tag `
--vendor Tomato `
--icon favicon.ico `
--input ../target/tomato-$git_tag `
--main-jar tomato-$git_tag.jar `
--main-class io.github.clagomess.tomato.Main `
--java-options "-splash:`$APPDIR/splash.png -Dfile.encoding=UTF-8" `
--verbose
mv ./Tomato ./tomato-$git_tag-x64

# sign app-image exe
# @TODO: "Access denied - check jdk version"
& $SignToolPath sign /v `
/td SHA256 `
/tr http://timestamp.digicert.com `
/f "code-sign-ks.pfx" `
/p "password" `
/fd SHA256 `
"./tomato-$git_tag-x64/Tomato.exe"

#  Run InnoSetup script
& "$env:LOCALAPPDATA\Programs\Inno Setup 6\ISCC.exe" `
/DMyAppVersion=$git_tag `
tomato-inno-setup.iss

# Sign installer
& $SignToolPath sign /v `
/td SHA256 `
/tr http://timestamp.digicert.com `
/f "code-sign-ks.pfx" `
/p "password" `
/fd SHA256 `
"./tomato-$git_tag-x64.exe"
