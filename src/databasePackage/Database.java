package databasePackage;

import userPackage.Admin;
import userPackage.Doctor;
import userPackage.Patient;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Appointment.Appointment;
import Appointment.AppointmentService;

public class Database implements IDatabase {

    private final static String PATIENT_FILE_PATH = "datafiles/patients.json";
    private final static String DOCTOR_FILE_PATH = "datafiles/doctors.json";
    private final static String ADMIN_FILE_PATH = "datafiles/admins.json";

    private final static UserRepository<Patient> patientRepository = new UserRepository<>(new File(PATIENT_FILE_PATH),Patient.class);
    private final static UserRepository<Doctor> doctorRepository = new UserRepository<>(new File(DOCTOR_FILE_PATH),Doctor.class);
    private final static UserRepository<Admin> adminRepository = new UserRepository<>(new File(ADMIN_FILE_PATH),Admin.class);

    private static Database instance = null;

    public static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    public boolean savePatient(Patient patient){
        return patientRepository.save(patient);
    }
    public boolean saveDoctor(Doctor doctor){
        return doctorRepository.save(doctor);
    }
    public boolean saveAdmin(Admin admin){
        return adminRepository.save(admin);
    }

    public boolean updatePatient(Patient patient){return patientRepository.update(patient.getSystemId(),patient);}
    public boolean updateDoctor(Doctor doctor){return doctorRepository.update(doctor.getSystemId(),doctor);}
    public boolean updateAdmin(Admin admin){return adminRepository.update(admin.getSystemId(),admin);}

    public boolean removePatient(Patient patient){
        return patientRepository.remove(patient);
    }
    public boolean removeDoctor(Doctor doctor){
        return doctorRepository.remove(doctor);
    }
    public boolean removeAdmin(Admin admin){
        return adminRepository.remove(admin);
    }

    public boolean removePatient(String systemId){
        Patient patient = patientRepository.findById(systemId);
        return removePatient(patient);
    }
    public boolean removeDoctor(String systemId){
        Doctor doctor = doctorRepository.findById(systemId);
        return removeDoctor(doctor);
    }
    public boolean removeAdmin(String systemId){
        Admin admin = adminRepository.findById(systemId);
        return removeAdmin(admin);
    }

    public Map<String,Patient> getPatientMap(){
        return patientRepository.getMap();
    }
    public Map<String,Doctor> getDoctorMap(){
        return doctorRepository.getMap();
    }
    public Map<String,Admin> getAdminMap(){
        return adminRepository.getMap();
    }

    public int patientCount(){
        return patientRepository.size();
    }
    public int doctorCount(){
        return doctorRepository.size();
    }
    public int adminCount(){
        return adminRepository.size();
    }

    public Patient findPatientById(String systemId){
        return patientRepository.findById(systemId);
    }
    public Doctor findDoctorById(String userSystemId){
        return doctorRepository.findById(userSystemId);
    }
    public Admin findAdminById(String userSystemId){
        return adminRepository.findById(userSystemId);
    }

    public Patient findPatientByIdentificationNumber(String identificationNumber){
        return patientRepository.findByIdentificationNumber(identificationNumber);
    }
    public Doctor findDoctorByIdentificationNumber(String identificationNumber){
        return doctorRepository.findByIdentificationNumber(identificationNumber);
    }
    public Admin findAdminByIdentificationNumber(String identificationNumber){
        return adminRepository.findByIdentificationNumber(identificationNumber);
    }

    public boolean containsPatient(String identificationNumber){return patientRepository.contains(identificationNumber);}
    public boolean containsDoctor(String identificationNumber) {return doctorRepository.contains(identificationNumber);}
    public boolean containsAdmin(String identificationNumber){
        return adminRepository.contains(identificationNumber);
    }

    public void refresh(){
        patientRepository.refresh();
        doctorRepository.refresh();
        adminRepository.refresh();
    }
    public List<String> getAllDoctorBranches() {
        return getDoctorMap().values().stream()
            .map(Doctor::getSpecialization)
            .filter(branch -> branch != null && !branch.isBlank())
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Doctor> findDoctorsBySpecialization(String specialization) {
        return getDoctorMap().values().stream()
            .filter(doc -> specialization.equals(doc.getSpecialization()))
            .collect(Collectors.toList());
    }
    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return AppointmentService.getInstance()
                .getAppointments().stream()
                .filter(appt -> appt.getDoctor().equals(doctor))
                .collect(Collectors.toList());
    }
    public void listAllDoctors() {
        doctorRepository.listAllUsers();
    }

    public void listAllPatients() {
        patientRepository.listAllUsers();
    }
    public boolean removeDoctorByTc(String tc) {
        return doctorRepository.removeByTc(tc);
    }


}
