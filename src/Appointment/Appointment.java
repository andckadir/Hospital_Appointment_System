package Appointment;

import userPackage.Doctor;
import userPackage.Patient;
import java.time.LocalDateTime;

public class Appointment {
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private boolean isCancelled = false;

    public Appointment(Patient patient, Doctor doctor, LocalDateTime dateTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
    public void cancel() {
        isCancelled = true;
    }

    @Override
    public String toString() {
        return "Appointment: " + patient.getName() + " - " + doctor.getName() + " on " + dateTime +
                (isCancelled ? " (Cancelled)" : "");
    }
}
