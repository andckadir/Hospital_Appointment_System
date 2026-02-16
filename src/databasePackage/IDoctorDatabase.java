package databasePackage;
import userPackage.Doctor;
import java.util.Map;

public interface IDoctorDatabase {
    public boolean saveDoctor(Doctor doctor);
    public boolean updateDoctor(Doctor doctor);
    public boolean removeDoctor(Doctor doctor);
    public boolean removeDoctor(String systemId);
    public Map<String,Doctor> getDoctorMap();
    public int doctorCount();
    public Doctor findDoctorById(String systemId);
    public Doctor findDoctorByIdentificationNumber(String identificationNumber);
    public boolean containsDoctor(String identificationNumber);
}
