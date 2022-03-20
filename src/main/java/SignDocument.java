import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;

public class SignDocument {
    protected final KeyPair TRUSTED_PARTY_KEYS = initTrustedPartyKeys();
    protected final String TRUSTED_PARTY_NAME = "TrustedParty";
    protected final String ALGORITHM = "SHA256withDSA";
    protected final String FACTORY_ALG = "DSA";

    public KeyPair generateKeys() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert keyPairGenerator != null;
        return keyPairGenerator.generateKeyPair();
    }

    public byte[] sign(PrivateKey privateKey, byte[] data) {
        SecureRandom secureRandom = new SecureRandom();
        try {
            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initSign(privateKey, secureRandom);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean verify(PublicKey publicKey, byte[] digitalSignature, byte[] data) {
        Signature signature;
        try {
            signature = Signature.getInstance(ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(digitalSignature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean isBothSignaturesIsValid(
            byte[] firstSignature,
            PublicKey firstKey,
            byte[] secondSignature,
            PublicKey secondKey,
            byte[] data
    ) {
        return verify(firstKey, firstSignature, data) && verify(secondKey, secondSignature, data);
    }

    private KeyPair initTrustedPartyKeys() {
        return generateKeys();
    }

    public byte[] signByTrustedParty(
            byte[] firstSignature,
            byte[] secondSignature
    ) {
        return sign(TRUSTED_PARTY_KEYS.getPrivate(), concatSignatures(firstSignature, secondSignature));
    }

    public byte[] concatSignatures(byte[] fpSignature, byte[] spSignature) {
        var byteStream = new ByteArrayOutputStream();
        byte[] commonSignature = new byte[0];
        try {
            byteStream.write(fpSignature);
            byteStream.write(spSignature);
            return byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return commonSignature;
        }
    }

    public Boolean checkTrustedPartySignature(byte[] tpSignature, byte[] fpSignature, byte[] spSignature) {
        return verify(TRUSTED_PARTY_KEYS.getPublic(), tpSignature, concatSignatures(fpSignature, spSignature));
    }
}
