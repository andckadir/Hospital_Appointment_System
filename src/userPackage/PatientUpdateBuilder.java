package userPackage;

public class PatientUpdateBuilder extends AbstractUserUpdateBuilder<Patient> {

    public PatientUpdateBuilder(Patient patient) {
        super(patient);
    }

    @Override
    public Patient build() {
        return new Patient((PatientUpdateBuilder) this);
    }
}