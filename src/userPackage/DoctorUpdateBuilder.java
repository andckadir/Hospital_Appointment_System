package userPackage;

public class DoctorUpdateBuilder extends AbstractUserUpdateBuilder<Doctor> {

    protected String systemId;
    protected String specialization; // branş

    public DoctorUpdateBuilder(Doctor doctor) {
        super(doctor);
        this.systemId = doctor.getSystemId();
        this.specialization = doctor.getSpecialization(); // branş için
    }

    public DoctorUpdateBuilder withSpecialization(String specialization) {
        this.specialization = specialization;
        return this;
    }

    @Override
    public Doctor build() {
        return new Doctor(this);
    }
}
