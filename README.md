# ssl-certificate-downloader
Logs and downloads all certificates in the chain presented by a server as pem files.

Note that the java code has no dependencies so you can compile and run the single class on a server if you want just using javac.

## Usage
```java
SslCertificateDownloader.run("https://google.com");
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

You can also run the main method of `SslCertificateDownloader` and pass the url as the first argument.

You can also run the jar file like so:

```bash
## build the jar file into the target directory
mvn clean install
cd target
java -jar -Dhttps.proxyHost=proxy -Dhttps.proxyPort=8080 -jar *.jar https://google.com
## list the downloaded pem files
ls target
```
