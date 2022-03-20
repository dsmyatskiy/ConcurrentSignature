import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SignManager extends SignDocument {
    FilesManager filesManager = new FilesManager();
    String firstParticipantName;
    String secondParticipantName;
    String directory;

    SignManager(String firstParticipantName, String secondParticipantName, String directory) {
        this.firstParticipantName = firstParticipantName;
        this.secondParticipantName = secondParticipantName;
        this.directory = directory;
    }

    public boolean signAll(String dataPath) {
        var data = filesManager.readFile(dataPath);
        var fpKeys = generateKeys();
        var spKeys = generateKeys();
        var fpSignature = sign(fpKeys.getPrivate(), data);
        var spSignature = sign(spKeys.getPrivate(), data);
        var tpSign = signByTrustedParty(fpSignature, spSignature);
        var fpPath = directory + "/" + firstParticipantName;
        var spPath = directory + "/" + secondParticipantName;
        var tpPath = directory + "/" + TRUSTED_PARTY_NAME;

        try {
            filesManager.saveKeyPair(directory, firstParticipantName, fpKeys);
            filesManager.saveKeyPair(directory, secondParticipantName, spKeys);
            filesManager.saveKeyPair(directory, TRUSTED_PARTY_NAME, TRUSTED_PARTY_KEYS);
            filesManager.writeSignature(tpPath, tpSign);
            filesManager.writeSignature(fpPath, fpSignature);
            filesManager.writeSignature(spPath, spSignature);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAll(String dataPath) {
        var data = filesManager.readFile(dataPath);
        KeyPair fpKeys;
        KeyPair spKeys;
        KeyPair tpKeys;
        try {
            fpKeys = filesManager.loadKeyPair(directory, firstParticipantName, FACTORY_ALG);
            spKeys = filesManager.loadKeyPair(directory, secondParticipantName, FACTORY_ALG);
            tpKeys = filesManager.loadKeyPair(directory, TRUSTED_PARTY_NAME, FACTORY_ALG);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }

        var tpSignature = filesManager.readSignature(directory + "/" + TRUSTED_PARTY_NAME);
        var fpSignature = filesManager.readSignature(directory + "/" + firstParticipantName);
        var spSignature = filesManager.readSignature(directory + "/" + secondParticipantName);
        if (!isBothSignaturesIsValid(fpSignature, fpKeys.getPublic(), spSignature, spKeys.getPublic(), data)) {
            System.out.println("Подписи не совпадают");
            return false;
        }
        return verify(tpKeys.getPublic(), tpSignature, concatSignatures(fpSignature, spSignature));
    }

}
