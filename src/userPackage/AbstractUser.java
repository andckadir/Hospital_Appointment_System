package userPackage;

import databasePackage.IDatabase;
import databasePackage.Database;
import servicePackage.NotificationService;
import servicePackage.PasswordService;
import servicePackage.ValidationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Objects;

public abstract class AbstractUser {
    protected final static ValidationService VALIDATION_SERVICE;
    protected final static PasswordService PASSWORD_SERVICE;
    protected final static IDatabase DATABASE;

    static {
        VALIDATION_SERVICE = ValidationService.getInstance();
        PASSWORD_SERVICE = PasswordService.getInstance();
        DATABASE = Database.getInstance();
    }

    private final UserType userType;
    private final String identificationNumber;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private Integer age;
    private Integer height;
    private Integer weight;

    protected boolean isInitialized = false;
    protected boolean isValidated = true;

    public AbstractUser(AbstractUserBuilder<?> builder) {
        this.userType = builder.userType;
        this.identificationNumber = builder.identificationNumber;
        this.name = builder.name;
        this.surname = builder.surname;
        this.phone = builder.phone;
        this.email = builder.email;
        this.age = builder.age;
        this.height = builder.height;
        this.weight = builder.weight;
    }

    public abstract String getSystemId();

    public abstract boolean removeFromDatabase(String password);

    public UserType getUserType() {
        return userType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWeight() {
        return weight;
    }

    public boolean isValidated(){
        return this.isValidated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        boolean result = AbstractUser.PASSWORD_SERVICE.changePassword(this.getSystemId(), oldPassword, newPassword);
        if (result) {

            NotificationService notifier = new NotificationService();
            String message = "Your password has been successfully changed.";
            notifier.send(this, message,"gmail");
        }
        return result;
    }


    protected boolean validateAllUserInputs() {
        return AbstractUser.VALIDATION_SERVICE.validateAllUserInputs(identificationNumber, name, surname, phone, email, age, height, weight);
    }

    protected boolean validatePassword(String password) {
        return AbstractUser.VALIDATION_SERVICE.validatePassword(password);
    }

    protected boolean savePassword(String password) {
        return AbstractUser.PASSWORD_SERVICE.savePassword(this.getSystemId(), password);
    }

    public boolean verifyPassword(String password) {
        return AbstractUser.PASSWORD_SERVICE.verifyPassword(this.getSystemId(), password);
    }

    protected final boolean initializePassword(String password) {
        if (!isInitialized && password != null) {
            AbstractUser.PASSWORD_SERVICE.savePassword(this.getSystemId(), password);
            isInitialized = true;
            return true;
        }
        return false;
    }

    protected static int loadLastIndex(String filename) {
        int lastIndex;
        try (BufferedReader bReader = new BufferedReader(new FileReader(filename))) {
            lastIndex = Integer.parseInt(bReader.readLine().trim());
        } catch (Exception e) {
            return Integer.MIN_VALUE;
        }
        return lastIndex;
    }

    protected static boolean saveLastIndex(String filename, int lastIndex) {
        try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(filename))) {
            bWriter.write(String.valueOf(lastIndex));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        AbstractUser abstractUser = (AbstractUser) o;
        return Objects.equals(getSystemId(), abstractUser.getSystemId()) && Objects.equals(userType, abstractUser.userType)
                && Objects.equals(identificationNumber, abstractUser.identificationNumber);

    }

    @Override
    public int hashCode() {
        return Objects.hash(getSystemId(), identificationNumber, userType);
    }

    @Override
    public String toString() {
        return "User{\n\t" +
                "userType=" + userType + ",\n\t" +
                "systemId=" + getSystemId() + ",\n\t" +
                "identificationNumber=" + identificationNumber + ",\n\t" +
                "name=" + name + ",\n\t" +
                "surname=" + surname + ",\n\t" +
                "phone=" + phone + ",\n\t" +
                "email=" + email + ",\n\t" +
                "age=" + age + ",\n\t" +
                "height=" + height + ",\n\t" +
                "weight=" + weight + ",\n" +
                "isvalidated="+isValidated+ ",\n" +
                '}';
    }
}
