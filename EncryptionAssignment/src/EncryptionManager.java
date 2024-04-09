import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Scanner;

public class EncryptionManager {
    private static final String KEY_FILE = "hereismykey.key";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter data to encrypt: ");
        String input = scanner.nextLine();
        scanner.close();

        if (!validateInput(input)) {
            return;
        }

        try {
            EncryptionManager manager = new EncryptionManager();
            byte[] encryptedData = manager.encrypt(input);
            saveEncryptedData(encryptedData, "encrypted_data.bin");
            System.out.println("Data encrypted and saved.");

            System.out.println("Proving the data is encrypted by printing it:");
            byte[] encryptedContent = readEncryptedData("encrypted_data.bin");
            System.out.println(new String(encryptedContent));

            System.out.println("Decrypting the data:");
            String decryptedData = manager.decrypt(encryptedData);
            System.out.println("Decrypted data: " + decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean validateInput(String input) {
        if (input.length() < 1) {
            System.out.println("Input is too short.");
            return false;
        }
        return true;
    }

    private static void saveEncryptedData(byte[] encryptedData, String fileName) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(encryptedData);
        }
    }

    private static byte[] readEncryptedData(String fileName) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            byte[] content = new byte[(int) new File(fileName).length()];
            inputStream.read(content);
            return content;
        }
    }

    private SecretKey getKey() throws IOException, NoSuchAlgorithmException {
        File file = new File(KEY_FILE);
        if (!file.exists()) {
            generateKey();
        }
        byte[] encodedKey = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(encodedKey);
        }
        return new SecretKeySpec(encodedKey, "AES");
    }

    private void generateKey() throws IOException, NoSuchAlgorithmException {
        SecretKey key = generateAESKey();
        try (FileOutputStream fos = new FileOutputStream(KEY_FILE)) {
            fos.write(key.getEncoded());
        }
    }

    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    private byte[] encrypt(String data) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        return cipher.doFinal(data.getBytes());
    }

    private String decrypt(byte[] encryptedData) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData);
    }
}
