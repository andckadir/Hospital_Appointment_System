package servicePackage;

public class ValidationService {

    private static ValidationService instance = null;

    public static ValidationService getInstance(){
        if (instance == null){
            instance = new ValidationService();
        }
        return instance;
    }

    private ValidationService(){}

    public boolean validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println(fieldName + " can not be empty!");
            return false;
        }
        if (name.trim().length() < 2) {
            System.err.println("Minimum allowed length for " + fieldName + " is 2! Available "+ fieldName +" -> "+name);
            return false;
        }
        if (!name.trim().matches("[a-zA-ZğüşıöçĞÜŞİÖÇ\\s]+")) {
            System.err.println(fieldName + " can only contain letters! Available name -> "+name);
            return false;
        }
        return true;
    }

    public boolean validateIdentificationNumber(String identificationNumber) {
        if (!identificationNumber.matches("^[0-9]+$")) {
            System.err.println("identification number can contains only numbers! Available identificationNumber -> "+identificationNumber);
            return false;
        }
        if (identificationNumber.length() != 11) {
            System.err.println("Allowed length for identification number is 11! Available identificationNumber -> "+identificationNumber);
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            System.err.println("Minimum allowed length for password is 8!");
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            System.err.println("Password must contain at least one number!");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            System.err.println("Password must contain at least one uppercase letter!");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            System.err.println("Password must contain at least one lowercase letter!");
            return false;
        }
        if (password.matches(".*[çÇğĞıİöÖşŞüÜ].*")) {
            System.err.println("Password cannot contain Turkish letters!");
            return false;
        }
        return true;
    }

    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        if (!email.contains("@") || !email.contains(".")) {
            System.err.println("Invalid email address!");
            return false;
        }
        return true;
    }

    public boolean validatePhone(String phone){
        if (phone == null || phone.trim().isEmpty())
            return true;
        return phone.matches("\\d{10,}");
    }

    public boolean validateAge(Integer age){
        if (age == null)
            return true;
        return age > 0;
    }

    public boolean validateHeight(Integer height){
        if (height == null)
            return true;
        return height > 0;
    }

    public boolean validateWeight(Integer weight){
        if (weight == null)
            return true;
        return weight > 0;
    }

    public boolean validateAllUserInputs(String identificationNumber,String name,String surname,String phone,String email,
                                         Integer age,Integer height,Integer weight){
        return validateIdentificationNumber(identificationNumber) &&
                validateName(name,"name") && validateName(surname,"surname") &&
                validatePhone(phone) && validateEmail(email) && validateAge(age) && validateHeight(height) && validateWeight(weight);
    }
}
