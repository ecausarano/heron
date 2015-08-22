package eu.heronnet;

import eu.heronnet.module.pgp.PGPUtils;
import eu.heronnet.module.storage.Persistence;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author edoardocausarano
 */
@ContextConfiguration(classes = {Scratch.TestConfig.class})

public class Scratch extends AbstractTestNGSpringContextTests {

    @Inject
    private PGPUtils pgpUtils;

    public static PGPKeyRingGenerator generateKeyRingGenerator
            (String id, char[] pass)
            throws Exception {
        return generateKeyRingGenerator(id, pass, 0xc0);
    }

    public final static PGPKeyRingGenerator generateKeyRingGenerator
            (String id, char[] pass, int s2kcount)
            throws Exception {
        // This object generates individual key-pairs.
        RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();

        // Boilerplate RSA parameters, no need to change anything
        // except for the RSA key-size (2048). You can use whatever
        // key-size makes sense for you -- 4096, etc.
        kpg.init
                (new RSAKeyGenerationParameters
                        (BigInteger.valueOf(0x10001),
                                new SecureRandom(), 2048, 12));

        // First create the master (signing) key with the generator.
        PGPKeyPair rsakp_sign =
                new BcPGPKeyPair
                        (PGPPublicKey.RSA_SIGN, kpg.generateKeyPair(), new Date());
        // Then an encryption subkey.
        PGPKeyPair rsakp_enc =
                new BcPGPKeyPair
                        (PGPPublicKey.RSA_ENCRYPT, kpg.generateKeyPair(), new Date());

        // Add a self-signature on the id
        PGPSignatureSubpacketGenerator signhashgen =
                new PGPSignatureSubpacketGenerator();

        // Add signed metadata on the signature.
        // 1) Declare its purpose
        signhashgen.setKeyFlags
                (false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
        // 2) Set preferences for secondary crypto algorithms to use
        //    when sending messages to this key.
        signhashgen.setPreferredSymmetricAlgorithms
                (false, new int[]{
                        SymmetricKeyAlgorithmTags.AES_256,
                        SymmetricKeyAlgorithmTags.AES_192,
                        SymmetricKeyAlgorithmTags.AES_128
                });
        signhashgen.setPreferredHashAlgorithms
                (false, new int[]{
                        HashAlgorithmTags.SHA256,
                        HashAlgorithmTags.SHA1,
                        HashAlgorithmTags.SHA384,
                        HashAlgorithmTags.SHA512,
                        HashAlgorithmTags.SHA224,
                });
        // 3) Request senders add additional checksums to the
        //    message (useful when verifying unsigned messages.)
        signhashgen.setFeature
                (false, Features.FEATURE_MODIFICATION_DETECTION);

        // Create a signature on the encryption subkey.
        PGPSignatureSubpacketGenerator enchashgen =
                new PGPSignatureSubpacketGenerator();
        // Add metadata to declare its purpose
        enchashgen.setKeyFlags
                (false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);

        // Objects used to encrypt the secret key.
        PGPDigestCalculator sha1Calc =
                new BcPGPDigestCalculatorProvider()
                        .get(HashAlgorithmTags.SHA1);
        PGPDigestCalculator sha256Calc =
                new BcPGPDigestCalculatorProvider()
                        .get(HashAlgorithmTags.SHA256);

        // bcpg 1.48 exposes this API that includes s2kcount. Earlier
        // versions use a default of 0x60.
        PBESecretKeyEncryptor pske =
                (new BcPBESecretKeyEncryptorBuilder
                        (PGPEncryptedData.AES_256, sha256Calc, s2kcount))
                        .build(pass);

        // Finally, create the keyring itself. The constructor
        // takes parameters that allow it to generate the self
        // signature.
        PGPKeyRingGenerator keyRingGen =
                new PGPKeyRingGenerator
                        (PGPSignature.POSITIVE_CERTIFICATION, rsakp_sign,
                                id, sha1Calc, signhashgen.generate(), null,
                                new BcPGPContentSignerBuilder
                                        (rsakp_sign.getPublicKey().getAlgorithm(),
                                                HashAlgorithmTags.SHA1),
                                pske);

        // Add our encryption subkey, together with its signature.
        keyRingGen.addSubKey
                (rsakp_enc, enchashgen.generate(), null);
        return keyRingGen;
    }

    @Test
    public void testPrefs() throws BackingStoreException {
        Preferences userRoot = Preferences.userRoot();
        for (String name : userRoot.childrenNames()) {
            System.out.println(name);
        }
        String herondb = userRoot.node("eu.heronnet").node("persistence").get("herondb", "herondb");
        System.out.println(herondb);

        Preferences jetbrains = userRoot.node("jetbrains");
        for (String s : jetbrains.node("communicator/core/impl").keys()) {
            System.out.println("jetbrains= " + s);
        }
    }

    @Test
    public void testGetPrivateKey() throws Exception {
        PGPSecretKey privateKey = pgpUtils.getPrivateKey();
        System.out.println(privateKey.toString());

    }

    @Test
    public void test()
            throws Exception {
        char pass[] = {'h', 'e', 'l', 'l', 'o'};
        PGPKeyRingGenerator krgen = generateKeyRingGenerator
                ("alice@example.com", pass);

        // Generate public key ring, dump to file.
        PGPPublicKeyRing pkr = krgen.generatePublicKeyRing();
        BufferedOutputStream pubout = new BufferedOutputStream
                (new FileOutputStream("dummy.pkr"));
        pkr.encode(pubout);
        pubout.close();

        // Generate private key, dump to file.
        PGPSecretKeyRing skr = krgen.generateSecretKeyRing();
        BufferedOutputStream secout = new BufferedOutputStream
                (new FileOutputStream("dummy.skr"));
        skr.encode(secout);
        secout.close();
    }

    // Note: s2kcount is a number between 0 and 0xff that controls the
    // number of times to iterate the password hash before use. More
    // iterations are useful against offline attacks, as it takes more
    // time to check each password. The actual number of iterations is
    // rather complex, and also depends on the hash function in use.
    // Refer to Section 3.7.1.3 in rfc4880.txt. Bigger numbers give
    // you more iterations.  As a rough rule of thumb, when using
    // SHA256 as the hashing function, 0x10 gives you about 64
    // iterations, 0x20 about 128, 0x30 about 256 and so on till 0xf0,
    // or about 1 million iterations. The maximum you can go to is
    // 0xff, or about 2 million iterations.  I'll use 0xc0 as a
    // default -- about 130,000 iterations.

    @Configuration
    public static class TestConfig {
        @Bean
        public String heronDataRoot() {
            return System.getProperty("user.home") + "/Library/eu.heronnet.Heron";
        }

        @Bean
        public PGPUtils pgpUtils() {
            return new PGPUtils();
        }

        @Bean
        public Persistence persistence() {
            return null;
        }

    }
}
