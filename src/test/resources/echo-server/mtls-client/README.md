# mTLS — test certificates

Client-side certificates for testing mutual TLS against the test server
(`echo-server`, see `docker-compose.yml`).

> ⚠️ **Test material only.** These keys are public — never use them outside local tests.

**Password (PFX and encrypted key): `tomato`**

## Endpoints

| Port   | TLS                                              |
|--------|--------------------------------------------------|
| `8443` | regular HTTPS (self-signed snakeoil), no client certificate |
| `8444` | **mTLS** — client certificate issued by *Tomato Test CA* is **required** |

## Files

| File                   | Format          | Description                                                      | Password |
|------------------------|-----------------|------------------------------------------------------------------|----------|
| `client.pfx`           | PKCS#12         | client cert + private key + CA chain (AES-256/PBKDF2), alias `tomato-client` | `tomato` |
| `client-legacy.pfx`    | PKCS#12 legacy  | same as above with 3DES/RC2 (older tools, e.g. macOS Keychain)   | `tomato` |
| `client.crt`           | PEM             | client certificate (`CN=Tomato Client,O=Tomato,C=BR`)            | —        |
| `client.key`           | PEM (PKCS#8)    | client private key, unencrypted                                  | —        |
| `client-encrypted.key` | PEM (PKCS#8)    | client private key, encrypted (AES-256-CBC)                      | `tomato` |
| `ca.crt`               | PEM             | Tomato Test CA — trusts the server certificate                   | —        |

## cURL examples

### PFX (PKCS#12) + password

```bash
curl --cacert ca.crt \
     --cert-type P12 --cert client.pfx:tomato \
     https://localhost:8444/echo.php
```

### PEM: certificate + unencrypted key

```bash
curl --cacert ca.crt \
     --cert client.crt \
     --key client.key \
     https://localhost:8444/echo.php
```

### PEM: certificate + encrypted key + password

```bash
curl --cacert ca.crt \
     --cert client.crt \
     --key client-encrypted.key --pass tomato \
     https://localhost:8444/echo.php
```
