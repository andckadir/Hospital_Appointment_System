package userPackage;

public class PatientBuilder extends AbstractUserBuilder<Patient> {

    public PatientBuilder(String identificationNumber, String name, String surname, String password) {
        super(UserType.PATIENT, identificationNumber, name, surname, password);
    }

    @Override
    public Patient build() {
        return new Patient(this);
    }
}
