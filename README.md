# Tomato

The open source and 100% offline REST Client tool.

Features:

- All data created are managed by you. You own the data! No freak cloud sync. You can easily select where is stored and decide witch strategie to do backup and share.
- All data generated is git friendly in readable JSON format.
- You can import collections from other tools and export for them.
- No account is required.
- No sneaky subscriptions plans.

Actually the project are in BETA stage, more functionalities will come soon, be patient.

## Download

Check: [Latest Release](https://github.com/clagomess/tomato/releases/latest)

Avaiable binaries:

- Windows MSI Installer - x64
- Debian and derivatives (*.deb) - multiarch
- Fedora/RHEL (*.rpm) - multiarch
- Zip without JRE (Required Java JRE >= 17)

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
