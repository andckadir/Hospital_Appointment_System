package userPackage;

public class Patient extends AbstractUser {
    private final static String INDEX_FILE = "datafiles/lastPatientIndex.txt";
    private static int lastPatientIndex;

    static {
        lastPatientIndex = AbstractUser.loadLastIndex(INDEX_FILE);
        if (lastPatientIndex == Integer.MIN_VALUE) {
            throw new IllegalStateException("lastPatientIndex can not loaded");
        }
    }

    private String systemId;

    public Patient(PatientBuilder builder) {
        super(builder);

        if (validateAllUserInputs() && validatePassword(builder.password)) {
            if (AbstractUser.DATABASE.containsPatient(getIdentificationNumber())) {
                this.isValidated = false;
                throw new IllegalStateException("Patient can not be created again with registered identification number");
            }
            else {
                lastPatientIndex++;
                this.systemId = "P" + lastPatientIndex;
            }
            if (!initializePassword(builder.password)) {
                this.isValidated = false;
                lastPatientIndex--;
            }
            saveLastIndex(INDEX_FILE, lastPatientIndex);
        }
        else {
            isValidated = false;
        }
    }

    public Patient(PatientUpdateBuilder builder) {
        super(builder);
        this.systemId = builder.systemId;
        this.isInitialized = true;
        if (!validateAllUserInputs()) {
            isValidated = false;
        }
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public boolean removeFromDatabase(String password) {
        if (!this.verifyPassword(password)) {
            System.err.println("Patient can not be removed from database. The password is incorrect!");
            return false;
        }
        if (!AbstractUser.DATABASE.removePatient(this) || !AbstractUser.PASSWORD_SERVICE.removePassword(getSystemId(), password)) {
            throw new IllegalStateException("There is a mismatch in Patient.removeFromDatabase");
        }
        return true;
    }
}
