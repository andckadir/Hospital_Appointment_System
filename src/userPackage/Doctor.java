package userPackage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Appointment.Appointment;
import databasePackage.Database;
import servicePackage.SlotGenerator;

public class Doctor extends AbstractUser {
    private static int lastDoctorIndex = -1;
    private final static String INDEX_FILE = "datafiles/lastDoctorIndex.txt";

    static {
        lastDoctorIndex = AbstractUser.loadLastIndex(INDEX_FILE);
        if (lastDoctorIndex == Integer.MIN_VALUE) {
            throw new IllegalStateException("lastDoctorIndex can not loaded");
        }
    }

    private String systemId;
    private String specialization; // branş

    public Doctor(DoctorBuilder builder) {
        super(builder);
        this.specialization = builder.specialization; // branş

        if (validateAllUserInputs() && validatePassword(builder.password)) {
            if (AbstractUser.DATABASE.containsDoctor(getIdentificationNumber())) {
                throw new IllegalStateException("Doctor can not be created again with registered identification number");
            }
            else {
                lastDoctorIndex++;
                this.systemId = "D" + lastDoctorIndex;
            }
            if (!initializePassword(builder.password)) {
                lastDoctorIndex--;
            }
            saveLastIndex(INDEX_FILE, lastDoctorIndex);
        }
    }

    public Doctor(DoctorUpdateBuilder builder) {
        super(builder);
        this.systemId = builder.systemId;
        this.isInitialized = true;
        this.specialization = builder.specialization; // branş
        validateAllUserInputs();
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean removeFromDatabase(String password) {
        if (!this.verifyPassword(password)) {
            System.err.println("Doctor can not be removed from database. The password is incorrect!");
            return false;
        }
        if (!AbstractUser.DATABASE.removeDoctor(this) || !AbstractUser.PASSWORD_SERVICE.removePassword(getSystemId(), password)) {
            throw new IllegalStateException("There is a mismatch in Doctor.removeFromDatabase");
        }
        return true;
    }

    private transient Map<LocalDateTime, Boolean> availableSlots = new LinkedHashMap<>();

    public Map<LocalDateTime, Boolean> getAvailableSlots() {
        if (availableSlots == null || availableSlots.isEmpty()) {
            List<LocalDateTime> allSlots = SlotGenerator.generateSlotsFromToday(14);
            List<LocalDateTime> occupiedSlots = Database.getInstance()
                    .getAppointmentsByDoctor(this).stream()
                    .filter(appt -> !appt.isCancelled())
                    .map(Appointment::getDateTime)
                    .toList();

            availableSlots = allSlots.stream()
                    .filter(slot -> !occupiedSlots.contains(slot))
                    .collect(Collectors.toMap(slot -> slot, slot -> true,
                            (v1, v2) -> v1, LinkedHashMap::new));
        }
        return availableSlots;
    }


}