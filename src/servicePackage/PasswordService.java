package servicePackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class PasswordService {
    //TODO will be redesigned
    private final static String PASSWORD_DATA_FILE = "datafiles/passwordData.json";
    private final static PasswordService INSTANCE = new PasswordService();
    private final Map<String, PasswordData> passwordDataMap;
    private final ValidationService validationService;
    private final Gson gson;

    public static PasswordService getInstance() {
        return INSTANCE;
    }

    PasswordService(){
        //!! create gson object before passwordMap !!
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.validationService = ValidationService.getInstance();
        this.passwordDataMap = loadFromFile();
    }

    public final boolean changePassword(String userSystemId, String oldPassword, String newPassword) {
        if (!validationService.validatePassword(newPassword)) {
            System.err.println("New password does not meet validation requirements!");
            printPasswordRequirements();
            return false;
        }
        if (verifyPassword(userSystemId, oldPassword)) {
            String salt = generateSalt();
            passwordDataMap.put(userSystemId, hashPassword(salt, newPassword));
            saveToFile(passwordDataMap);
            return true;
        } else {
            System.err.println("Entered password does not match the registered password!");
            return false;
        }
    }

    public boolean savePassword(String userSystemID, String password) {
        if (!passwordDataMap.containsKey(userSystemID)) {
            if (!validationService.validatePassword(password)) {
                System.err.println("Password does not meet validation requirements!");
                printPasswordRequirements();
                return false;
            }
            passwordDataMap.put(userSystemID, hashPassword(generateSalt(), password));
            saveToFile(passwordDataMap);
            return true;
        }
        return false;
    }

    public boolean removePassword(String userSystemID, String password) {
        if (verifyPassword(userSystemID, password)) {
            passwordDataMap.remove(userSystemID);
            saveToFile(passwordDataMap);
            return true;
        } else {
            System.err.println("Password does not match with the registered password!");
            return false;
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private PasswordData hashPassword(String salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedBytes = md.digest(password.getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hashedBytes);
            return new PasswordData(salt,hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm can not be found", e);
        }
    }

    public final boolean verifyPassword(String userSystemId, String inputPassword) {
        if (inputPassword == null) {
            return false;
        }

        PasswordData storedPasswordData = passwordDataMap.get(userSystemId);
        if (storedPasswordData == null) {
            return false;
        }

        String salt = storedPasswordData.salt;
        PasswordData inputPasswordData = hashPassword(salt, inputPassword);
        return storedPasswordData.equals(inputPasswordData);
    }

    private Map<String, PasswordData> loadFromFile() {
        File file = new File(PASSWORD_DATA_FILE);
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }

        Map<String, PasswordData> map;
        try (FileReader fReader = new FileReader(PASSWORD_DATA_FILE)) {
            Type mapType = new TypeToken<Map<String, PasswordData>>() {}.getType();
            map = gson.fromJson(fReader, mapType);
            if (map == null) {
                return new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading password data: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
        return map;
    }

    private void saveToFile(Map<String, PasswordData> map) {
        try (FileWriter fWriter = new FileWriter(PASSWORD_DATA_FILE)) {
            gson.toJson(map, fWriter);
        } catch (Exception e) {
            System.err.println("Error saving password data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private record PasswordData(String salt, String hashedPassword) {

    }

    private void printPasswordRequirements(){
        System.err.println("---------------------------------");
        System.err.println("Password can not contains turkish letters.");
        System.err.println("Password must at least one uppercase letter and one lowercase letter.");
        System.err.println("Minimum allowed password length is 8.");
        System.err.println("---------------------------------");
    }
}