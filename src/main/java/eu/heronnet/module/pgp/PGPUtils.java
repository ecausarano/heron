package eu.heronnet.module.pgp;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.StringNode;
import eu.heronnet.model.builder.StringNodeBuilder;
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
import org.bouncycastle.openpgp.operator.bc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author edoardocausarano
 */
@Component
public class PGPUtils {

    private static final Logger logger = LoggerFactory.getLogger(PGPUtils.class);

    // WOT Ontology public key class
    private static StringNode PUBLIC_KEY_CLASS;

    static {
        try {
            PUBLIC_KEY_CLASS = StringNodeBuilder.withString("http://xmlns.com/wot/0.1/PubKey");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Could not SHA-256 hash algorithm, this should never happen");
        }
    }

    @Inject
    private String heronDataRoot;
    @Inject
    private Persistence persistence;

    public static StringNode getPublicKeyClass() {
        return PUBLIC_KEY_CLASS;
    }

    private static void createKeys(final String email, final String password) throws Exception {
        logger.debug("Generating public keyring");

        PGPKeyRingGenerator pgpKeyRingGenerator = generateKeyRingGenerator(email, password.toCharArray(), 0xc0);

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("dummy.pkr"), StandardOpenOption.CREATE_NEW)) {
            PGPPublicKeyRing pgpPublicKeyRing = pgpKeyRingGenerator.generatePublicKeyRing();

            pgpPublicKeyRing.encode(outputStream);
        }

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("dummy.skr"), StandardOpenOption.CREATE_NEW)) {
            PGPSecretKeyRing secretKeyRing = pgpKeyRingGenerator.generateSecretKeyRing();
            secretKeyRing.encode(outputStream);
        }
    }

    private static PGPKeyRingGenerator generateKeyRingGenerator(String id, char[] pass, int s2kcount) throws Exception {
        // This object generates individual key-pairs.
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();

        // Boilerplate RSA parameters, no need to change anything
        // except for the RSA key-size (2048). You can use whatever
        // key-size makes sense for you -- 4096, etc.
        keyPairGenerator.init(new RSAKeyGenerationParameters(
                BigInteger.valueOf(0x10001), new SecureRandom(), 2048, 12));

        // First create the master (signing) key with the generator.
        PGPKeyPair rsakp_sign = new BcPGPKeyPair(PGPPublicKey.RSA_SIGN,
                keyPairGenerator.generateKeyPair(), new Date());

        // Add a self-signature on the id
        PGPSignatureSubpacketGenerator signatureSubpacketGenerator = new PGPSignatureSubpacketGenerator();

        // Add signed metadata on the signature.
        // 1) Declare its purpose
        signatureSubpacketGenerator.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
        // 2) Set preferences for secondary crypto algorithms to use
        //    when sending messages to this key.
        signatureSubpacketGenerator.setPreferredSymmetricAlgorithms(false, new int[]{
                SymmetricKeyAlgorithmTags.AES_256,
                SymmetricKeyAlgorithmTags.AES_128
        });
        signatureSubpacketGenerator.setPreferredHashAlgorithms(false, new int[]{
                HashAlgorithmTags.SHA256,
                HashAlgorithmTags.SHA512,
        });
        // 3) Request senders add additional checksums to the
        //    message (useful when verifying unsigned messages.)
        signatureSubpacketGenerator.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);

        PGPDigestCalculator sha256Calc = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);

        // bcpg 1.48 exposes this API that includes s2kcount. Earlier
        // versions use a default of 0x60.
        PBESecretKeyEncryptor pske = (new BcPBESecretKeyEncryptorBuilder
                (PGPEncryptedData.AES_256, sha256Calc, s2kcount)).build(pass);

        // Finally, create the keyring itself. The constructor
        // takes parameters that allow it to generate the self
        // signature.

        return new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION,
                rsakp_sign, id, sha256Calc,
                signatureSubpacketGenerator.generate(), null,
                new BcPGPContentSignerBuilder(rsakp_sign.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA256), pske
        );
    }

    /**
     * Load known public keys from {@link Persistence}
     *
     * @throws Exception
     */
    public List<Bundle> getKnownPublicKeys() throws Exception {
        List<Bundle> bundles = persistence.findByPredicate(Collections.singletonList(PUBLIC_KEY_CLASS));
        logger.debug("found {} known public keys in storage", bundles.size());
        return bundles;
    }

    public boolean hasPrivateKey() {
        try (InputStream inputStream = Files.newInputStream(Paths.get(heronDataRoot + "/identity.skr"))) {
            PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(inputStream, new BcKeyFingerprintCalculator());
            final boolean[] found = {false};
            pgpObjectFactory.forEach(o -> {
                if (o instanceof PGPSecretKeyRing) {
                    PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) o;
                    PGPSecretKey secretKey = secretKeyRing.getSecretKey();
                    found[0] = !secretKey.isPrivateKeyEmpty() && secretKey.isSigningKey();
                }
            });
            return found[0];
        } catch (IOException e) {
            logger.error("Error handling keyring", e.getMessage());
            return false;
        }

    }

    public PGPSecretKey getPrivateKey() throws Exception {
//        TODO - on a Mac store the key pair in the KeychainStore
//        KeyStore instance = KeyStore.getInstance("KeychainStore", "Apple");
//        instance.load(null, null);
//        return instance.getKey("Heron Identity", "any".toCharArray());

        try (InputStream inputStream = Files.newInputStream(Paths.get(heronDataRoot + "/identity.skr"))) {
            PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(inputStream, new BcKeyFingerprintCalculator());
            final PGPSecretKey[] secretKey = {null};
            pgpObjectFactory.forEach(o -> {
                if (o instanceof PGPSecretKeyRing) {
                    PGPSecretKeyRing secretKeyRing = (PGPSecretKeyRing) o;
                    secretKey[0] = secretKeyRing.getSecretKey();
                }
            });
            return secretKey[0];
        }
    }

    public PGPPublicKey getPublicKey() throws Exception {
        return getPrivateKey().getPublicKey();
    }
}
