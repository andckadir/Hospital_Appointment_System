package servicePackage;

import databasePackage.Database;

import userPackage.Doctor;
import userPackage.Patient;

public class AuthenticationService {

	private final Database database = Database.getInstance();

    public Patient loginPatient(String tc, String password) {
        Patient patient = database.findPatientByIdentificationNumber(tc);
        if (patient != null && PasswordService.getInstance().verifyPassword(patient.getSystemId(), password)) {
            return patient;
        }
        return null;
    }

    public Doctor loginDoctor(String tc, String password) {
        Doctor doctor = database.findDoctorByIdentificationNumber(tc);
        if (doctor != null && PasswordService.getInstance().verifyPassword(doctor.getSystemId(), password)) {
            return doctor;
        }
        return null;
    }
}
