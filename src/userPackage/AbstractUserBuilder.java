package userPackage;

public abstract class AbstractUserBuilder<T extends AbstractUser> {
    protected UserType userType;
    protected String identificationNumber;
    protected String name;
    protected String surname;
    protected String phone;
    protected String email;
    protected Integer age = null;
    protected Integer height = null;
    protected Integer weight = null;
    protected String password;

    public AbstractUserBuilder(UserType userType, String identificationNumber, String name, String surname, String password){
        this.userType = userType;
        this.identificationNumber = identificationNumber;
        this.name = name;
        this.surname = surname;
        this.password = password;
    }

    public abstract T build();

    public AbstractUserBuilder<T> withPhone(String phone){
        this.phone = phone;
        return this;
    }

    public AbstractUserBuilder<T> withEmail(String email){
        this.email = email;
        return this;
    }

    public AbstractUserBuilder<T> withAge(Integer age){
        this.age = age;
        return this;
    }

    public AbstractUserBuilder<T> withHeight(Integer height){
        this.height = height;
        return this;
    }

    public AbstractUserBuilder<T> withWeight(Integer weight){
        this.weight = weight;
        return this;
    }
}
