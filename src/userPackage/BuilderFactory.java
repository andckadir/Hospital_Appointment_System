package userPackage;

public class BuilderFactory {
    public BuilderFactory() {

    }

    public PatientBuilder createPatient(String identificationNumber, String name, String surname, String password) {
        return new PatientBuilder(identificationNumber, name, surname, password);
    }

    public DoctorBuilder createDoctor(String identificationNumber, String name, String surname, String password) {
        return new DoctorBuilder(identificationNumber, name, surname, password);
    }

    public AdminBuilder createAdmin(String identificationNumber, String name, String surname, String password) {
        return new AdminBuilder(identificationNumber, name, surname, password);
    }

    public PatientUpdateBuilder createUpdatedPatient(Patient patient) {
        return new PatientUpdateBuilder(patient);
    }

    public DoctorUpdateBuilder createUpdatedDoctor(Doctor doctor) {
        return new DoctorUpdateBuilder(doctor);
    }

    public AdminUpdateBuilder createUpdatedAdmin(Admin admin) {
        return new AdminUpdateBuilder(admin);
    }
}
