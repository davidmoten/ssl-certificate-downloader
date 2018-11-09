# ssl-certificate-downloader
Logs and downloads all certificates in the chain presented by a server.

## Usage
```java
new SslCertificateDownloader("https://google.com");
```

output:
```
Downloading certs from [https://google.com]

Connecting to https://google.com

Certificate: 
  Subject: CN=*.google.com, O=Google LLC, L=Mountain View, ST=California, C=US
  Issuer : CN=Google Internet Authority G3, O=Google Trust Services, C=US
Certificate written to target/Google Internet Authority G3.pem

Certificate: 
  Subject: CN=Google Internet Authority G3, O=Google Trust Services, C=US
  Issuer : CN=GlobalSign, O=GlobalSign, OU=GlobalSign Root CA - R2
Certificate written to target/GlobalSign.pem
```


