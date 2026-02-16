package userPackage;

public class AdminUpdateBuilder extends AbstractUserUpdateBuilder<Admin> {
    public AdminUpdateBuilder(Admin user) {
        super(user);
    }

    @Override
    public Admin build() {
        return new Admin((AdminUpdateBuilder) this);
    }
}
