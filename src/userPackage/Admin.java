package userPackage;

public class Admin extends AbstractUser {
    private final static String INDEX_FILE = "datafiles/lastAdminIndex.txt";
    private static int lastAdminIndex = -1;

    static {
        lastAdminIndex = AbstractUser.loadLastIndex(INDEX_FILE);
        if (lastAdminIndex == Integer.MIN_VALUE) {
            throw new IllegalStateException("lastAdminIndex can not loaded");
        }
    }

    private String systemId;
    public Admin(AdminBuilder builder) {
        super(builder);

        if (validateAllUserInputs() && validatePassword(builder.password)){
            if (AbstractUser.DATABASE.containsAdmin(getIdentificationNumber())){
                throw new IllegalStateException("Admin can not be created again with registered identification number");
            }else {
                lastAdminIndex++;
                this.systemId = "A" + lastAdminIndex;
            }
            if (!initializePassword(builder.password)){
                lastAdminIndex--;
            }
            saveLastIndex(INDEX_FILE,lastAdminIndex);
        }
    }

    public Admin(AdminUpdateBuilder builder){
        super(builder);
        this.systemId = builder.systemId;
        this.isInitialized = true;
        validateAllUserInputs();
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public boolean removeFromDatabase(String password) {
        if (!this.verifyPassword(password)){
            System.err.println("Admin can not be removed from database. The password is incorrect!");
            return false;
        }
        if (!AbstractUser.DATABASE.removeAdmin(this) || !AbstractUser.PASSWORD_SERVICE.removePassword(getSystemId(),password)){
            throw new IllegalStateException("There is a mismatch in Admin.removeFromDatabase");
        }
        return true;
    }
}
