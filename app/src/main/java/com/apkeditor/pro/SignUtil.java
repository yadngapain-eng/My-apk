
package com.apkeditor.pro;

import android.content.Context;

import com.android.apksig.ApkSigner;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

public class SignUtil {
    private static final String KS_PASS   = "apkeditor";
    private static final String KEY_ALIAS = "apkeditor";
    private static final String KEY_PASS  = "apkeditor";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static File sign(Context ctx, File input) throws Exception {
        File ksFile = new File(ctx.getFilesDir(), "debug.keystore");
        if (!ksFile.exists()) createKeystore(ksFile);

        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(ksFile)) {
            ks.load(fis, KS_PASS.toCharArray());
        }
        PrivateKey key = (PrivateKey) ks.getKey(KEY_ALIAS, KEY_PASS.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate(KEY_ALIAS);

        String base = input.getName().replaceAll("\\.apk$", "");
        File out = new File(input.getParent(), base + "_signed.apk");

        ApkSigner.SignerConfig cfg = new ApkSigner.SignerConfig.Builder(
            KEY_ALIAS, key, Collections.singletonList(cert)).build();

        new ApkSigner.Builder(Collections.singletonList(cfg))
            .setInputApk(input)
            .setOutputApk(out)
            .setV1SigningEnabled(true)
            .setV2SigningEnabled(true)
            .setV3SigningEnabled(false)
            .build()
            .sign();
        return out;
    }

    private static void createKeystore(File out) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        long now = System.currentTimeMillis();
        Date from = new Date(now);
        Date to   = new Date(now + 3650L * 24 * 60 * 60 * 1000);

        X500Name name = new X500Name("CN=APK Editor Pro, O=APKEditor, C=ID");
        JcaX509v3CertificateBuilder builder =
            new JcaX509v3CertificateBuilder(
                name, BigInteger.valueOf(now), from, to, name, kp.getPublic());

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
            .setProvider("BC").build(kp.getPrivate());

        X509Certificate cert = new JcaX509CertificateConverter()
            .setProvider("BC").getCertificate(builder.build(signer));

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        ks.setKeyEntry(KEY_ALIAS, kp.getPrivate(), KEY_PASS.toCharArray(),
            new java.security.cert.Certificate[]{cert});

        try (FileOutputStream fos = new FileOutputStream(out)) {
            ks.store(fos, KS_PASS.toCharArray());
        }
    }
}
