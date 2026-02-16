package userPackage;

public class DoctorBuilder extends AbstractUserBuilder<Doctor> {
    protected String specialization; // branş eklendi

    public DoctorBuilder(String identificationNumber, String name, String surname, String password) {
        super(UserType.DOCTOR, identificationNumber, name, surname, password);
    }

    public DoctorBuilder withSpecialization(String specialization) {
        this.specialization = specialization;
        return this;
    }

    @Override
    public Doctor build() {
        return new Doctor(this);
    }

}
