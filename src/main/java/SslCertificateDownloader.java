import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.relation.RelationServiceNotRegisteredException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public final class SslCertificateDownloader {

    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";

    private final String url;

    public SslCertificateDownloader(String url) {
        this.url = url;
    }

    public void run() throws Exception {
        SSLContext ctx = null;
        ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[] { new CustomTrustManager() }, new SecureRandom());

        println("Connecting to " + url);
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });

        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            while (reader.readLine() != null) {
            }
        }

    }

    private static void println(String s) {
        System.out.println(s);
    }

    class CustomTrustManager implements X509TrustManager {

        boolean done;

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            if (done) {
                return;
            }
            done = true;
            for (X509Certificate cert : x509Certificates) {
                println("\nCertificate: ");
                println("  Subject: " + cert.getSubjectDN());
                println("  Issuer : " + cert.getIssuerDN());
                try {
                    writeCertificatePem(cert, new File("target/" + getCN(cert.getIssuerDN().toString()) + ".pem"));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static String getCN(String s) {
        Pattern p = Pattern.compile(".*\\bCN=([^,]*),.*");
        Matcher m = p.matcher(s);
        m.find();
        return m.group(1);
    }

    private static void writeCertificatePem(X509Certificate cert, File file)
            throws IOException, CertificateEncodingException {
        // write out the root
        try (FileOutputStream out = new FileOutputStream(file)) {
            Base64.Encoder encoder = Base64.getMimeEncoder(64, new byte[] { 0x0a });
            out.write(BEGIN_CERT.getBytes(StandardCharsets.US_ASCII));
            out.write(0x0a); // Newline
            out.write(encoder.encode(cert.getEncoded()));
            out.write(0x0a); // Newline
            out.write(END_CERT.getBytes(StandardCharsets.US_ASCII));
            out.write(0x0a); // Newline
            println("Certificate written to " + file);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("downloading certs from " + Arrays.toString(args));
        new SslCertificateDownloader(args[0]).run();
    }

}
