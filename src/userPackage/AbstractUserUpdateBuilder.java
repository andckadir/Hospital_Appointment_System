package userPackage;

public abstract class AbstractUserUpdateBuilder<T extends AbstractUser> extends AbstractUserBuilder<T> {

    public String systemId;
    public AbstractUserUpdateBuilder(T user) {
        super(user.getUserType(), user.getIdentificationNumber(), user.getName(), user.getSurname(),null);
        this.age = user.getAge();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.height = user.getHeight();
        this.weight = user.getWeight();
        this.systemId = user.getSystemId();
    }

    public AbstractUserUpdateBuilder<T> withName(String name){
        this.name = name;
        return this;
    }

    public AbstractUserUpdateBuilder<T> withSurname(String surname){
        this.surname = surname;
        return this;
    }
}
