package userPackage;

public class AdminBuilder extends AbstractUserBuilder<Admin> {
    public AdminBuilder(String identificationNumber, String name, String surname, String password) {
        super(UserType.ADMIN, identificationNumber, name, surname, password);
    }

    @Override
    public Admin build() {
        return new Admin(this);
    }
}
