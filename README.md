<p align="center">
    <img src="https://github.com/clagomess/tomato/blob/master/src/main/resources/io/github/clagomess/tomato/ui/component/favicon/favicon.svg" width="64" alt="Tomato">
</p>

# Tomato

[![GitHub License](https://img.shields.io/github/license/clagomess/tomato)](https://github.com/clagomess/tomato/blob/master/LICENSE)
[![Release CI](https://github.com/clagomess/tomato/actions/workflows/release.yml/badge.svg)](https://github.com/clagomess/tomato/actions/workflows/release.yml)
[![Test CI](https://github.com/clagomess/tomato/actions/workflows/test.yml/badge.svg)](https://github.com/clagomess/tomato/actions/workflows/test.yml)
[![Release](https://img.shields.io/github/v/release/clagomess/tomato)](https://github.com/clagomess/tomato/releases)

The open source and 100% offline REST Client tool.

Features:

- All created data is managed by you. You own the data! No freak cloud sync. You can easily select where it'll be stored and decide which strategy will be used to backup and share.
- All generated data is git friendly in readable JSON format.
- You can import and export collections from/for other tools.
- No account required.
- No sneaky subscriptions plans.

Actually the project are in BETA stage, more functionalities will come soon, be patient.

## Download

Check: [Latest Release](https://github.com/clagomess/tomato/releases/latest)

Avaiable binaries:

| Platform                                  | Arch | Filename                      |
|-------------------------------------------|------|-------------------------------|
| Windows                                   | x64  | Tomato-[version]-x64.msi      |
| Debian and derivatives                    | all  | tomato-[version]-all.deb      |
| Fedora/RHEL                               | all  | tomato-[version]-1.noarch.rpm |
| Flatpak                                   | x64  | tomato-[version]-x64.flatpak  |
| Zip without JRE (Required Java JRE >= 17) | all  | tomato-[version].zip          |

## Screenshot

![Screenshot of Main UI](https://github.com/clagomess/tomato/blob/master/screenshots/screenshot-001.png)

## Data structure

By default, Tomato save configuration and your data at `~/.tomato` for POSIX and `%homepath%\.tomato` for Windows.

All HTTP request create a temporary file at `/tmp` or `%temp%` and destroy when application exit. 
Why these approch? This evicts application crash when some api response with a big large content. Tomato just give an option to save the content.

The `~/.tomato/data` can be changed to be in another location like One Drive, Dropbox, mounted drive, etc. The data follow this structure:

```
~/.tomato/
├── data/
│   └── data-session.json
│   └── workspace-{ID}/
│       └── workspace-{ID}.json
│       └── environment-{ID}.json
│       └── environment-{ID}.kdbx
│       └── environment-{ID}.kdbx.bkp
│       └── workspace-session.json
│       └── collection-{ID}/
│           └── collection-{ID}.json
│           └── request-{ID}.json
│           └── collection-{ID}/
│               └── collection-{ID}.json
│               └── request-{ID}.json
│       └── request-{ID}.json
└── configuration.json
```

## Development

JVM properties:

- Enable debug Log: `TOMATO_LOG_LEVEL=DEBUG`
- Aways point to test data: `TOMATO_AWAYS_USE_TEST_DATA=true`
