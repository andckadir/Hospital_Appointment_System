package Appointment;

import userPackage.Doctor;
import userPackage.Patient;
import servicePackage.SlotGenerator;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import databasePackage.Database;
import databasePackage.LocalDateTimeTypeAdapter;
import servicePackage.NotificationService;


public class AppointmentService {
    private static AppointmentService instance = null;
    private List<Appointment> appointments;
    private final File appointmentFile = new File("dataFiles/appointments.json");
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .setPrettyPrinting()
            .create();

    private AppointmentService() {
        // JSON'dan randevuları yükle
        appointments = loadFromFile();
    }

    public static AppointmentService getInstance() {
        if (instance == null) {
            instance = new AppointmentService();
        }
        return instance;
    }

    private void saveToFile() {
        // JSON dosyasına randevuları kaydet
        try (Writer writer = new FileWriter(appointmentFile)) {
            gson.toJson(appointments, writer);
        } catch (IOException e) {
            System.err.println("Error writing appointments to file: " + e.getMessage());
        }
    }

    private List<Appointment> loadFromFile() {
        // JSON dosyasından randevuları oku
        if (!appointmentFile.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(appointmentFile)) {
            Type listType = new TypeToken<List<Appointment>>() {}.getType();
            List<Appointment> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error reading appointments from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void bookAppointment(Patient patient, Doctor doctor, LocalDateTime dateTime) {
        // Aynı saatte aktif randevu olup olmadığını kontrol et
        for (Appointment appt : appointments) {
            if (appt.getDoctor().equals(doctor) && appt.getDateTime().equals(dateTime) && !appt.isCancelled()) {
                System.out.println("This doctor already has an appointment at this time.");
                return;
            }
        }

        // Doktorun bu slotta müsait olup olmadığını kontrol et
        Boolean isAvailable = doctor.getAvailableSlots().get(dateTime);
        if (isAvailable == null || !isAvailable) {
            System.out.println("The doctor is not available at this time.");
            return;
        }

        // Randevuyu oluştur
        Appointment appointment = new Appointment(patient, doctor, dateTime);
        appointments.add(appointment);
        saveToFile();

        // Slotu meşgul olarak işaretle
        doctor.getAvailableSlots().put(dateTime, false);

        // 14 günlük slotu güncelle
        for (LocalDateTime slot : SlotGenerator.generateSlotsFromToday(14)) {
            doctor.getAvailableSlots().putIfAbsent(slot, true);
        }

        Database.getInstance().updateDoctor(doctor);
        System.out.println("Appointment successfully booked:\n" + appointment);

        NotificationService notifier = new NotificationService();
        notifier.send(patient, "Your appointment with Dr. " + doctor.getName() + " has been successfully booked on " + dateTime,"sms");
        notifier.send(doctor, "A new appointment has been booked with patient " + patient.getName() + " " + patient.getSurname() + " for " + dateTime,"sms");
    }

    public void listAppointmentsByPatient(Patient patient, String filter) {
        // Hastanın filtreye göre randevularını listele
        boolean found = false;
        for (Appointment appt : appointments) {
            if (appt.getPatient().equals(patient)) {
                if (filter.equals("all") ||
                        (filter.equals("active") && !appt.isCancelled()) ||
                        (filter.equals("cancelled") && appt.isCancelled())) {
                    System.out.println(appt);
                    found = true;
                }
            }
        }
        if (!found) System.out.println("No appointments for this patient.");
    }

    public List<Appointment> getAppointmentsByPatient(Patient patient, String filter) {
        // Hastanın randevularını filtreyle getir
        return appointments.stream()
                .filter(appt -> appt.getPatient().equals(patient))
                .filter(appt ->
                        filter.equals("all") ||
                        (filter.equals("active") && !appt.isCancelled()) ||
                        (filter.equals("cancelled") && appt.isCancelled()))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        // Doktorun aktif randevularını getir
        return appointments.stream()
                .filter(appt -> appt.getDoctor().equals(doctor) && !appt.isCancelled())
                .collect(Collectors.toList());
    }

    public void cancelAppointment(Patient patient, LocalDateTime dateTime) {
        // Hasta tarafından randevu iptali
        for (Appointment appt : appointments) {
            if (appt.getPatient().equals(patient) && appt.getDateTime().equals(dateTime) && !appt.isCancelled()) {
                appt.cancel();
                appt.getDoctor().getAvailableSlots().put(dateTime, true);
                Database.getInstance().updateDoctor(appt.getDoctor());
                saveToFile();
                System.out.println("Appointment cancelled:\n" + appt);

                NotificationService notifier = new NotificationService();
                notifier.send(appt.getDoctor(), "The appointment with patient " + patient.getName() + " on " + dateTime + " has been cancelled by the patient.","sms");
                return;
            }
        }
        System.out.println("No matching active appointment found to cancel.");
    }

    public void cancelAppointmentByDoctor(Appointment appointment) {
        // Doktor tarafından randevu iptali
        if (appointment != null && !appointment.isCancelled()) {
            appointment.cancel();
            saveToFile();
            System.out.println("Appointment successfully cancelled.");

            NotificationService notifier = new NotificationService();
            notifier.send(appointment.getPatient(), "Your appointment with Dr. " + appointment.getDoctor().getName() + " on " + appointment.getDateTime() + " has been cancelled by the doctor.","sms");
        } else {
            System.out.println("Appointment is already cancelled.");
        }
    }

    public List<LocalDateTime> getAvailableSlots(Doctor doctor) {
        // Doktorun müsait olduğu slotları getir
        return doctor.getAvailableSlots().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
