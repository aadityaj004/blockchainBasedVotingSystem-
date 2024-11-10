import java.security.MessageDigest;
import java.util.List;

public class CryptoUtils {
    public static String calculateHash(int index, long timestamp, List<Transaction> transactions, String previousHash) {
        StringBuilder data = new StringBuilder();
        data.append(index).append(timestamp).append(transactions.toString()).append(previousHash);
        return applySha256(data.toString());
    }

    private static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}