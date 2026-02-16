package databasePackage;
import userPackage.Patient;
import java.util.Map;

public interface IPatientDatabase {
    public boolean savePatient(Patient patient);
    public boolean updatePatient(Patient patient);
    public boolean removePatient(Patient patient);
    public boolean removePatient(String systemId);
    public Map<String,Patient> getPatientMap();
    public int patientCount();
    public Patient findPatientById(String systemId);
    public Patient findPatientByIdentificationNumber(String identificationNumber);
    public boolean containsPatient(String identificationNumber);
}
