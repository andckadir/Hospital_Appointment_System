package servicePackage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SlotGenerator {

    public static List<LocalDateTime> generateSlotsFromToday(int days) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int day = 0; day < days; day++) {
            LocalDate currentDate = today.plusDays(day);

            if (currentDate.getDayOfWeek().getValue() >= 6) continue;

            // Sabah 09:00 - 12:00
            LocalTime time = LocalTime.of(9, 0);
            while (time.isBefore(LocalTime.of(12, 0))) {
                slots.add(currentDate.atTime(time));
                time = time.plusMinutes(20);
            }

            // Öğleden sonra 13:00 - 16:00
            time = LocalTime.of(13, 0);
            while (time.isBefore(LocalTime.of(16, 0))) {
                slots.add(currentDate.atTime(time));
                time = time.plusMinutes(20);
            }
        }

        return slots;
    }
    
    public static List<LocalDateTime> extendSlots(List<LocalDateTime> existingSlots, int targetDays) {
        List<LocalDateTime> futureSlots = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Bugünden sonrası için slotları tut
        for (LocalDateTime slot : existingSlots) {
            if (!slot.toLocalDate().isBefore(today)) {
                futureSlots.add(slot);
            }
        }

        int existingDays = (int) futureSlots.stream()
            .map(LocalDateTime::toLocalDate)
            .distinct()
            .count();

        int daysToGenerate = targetDays - existingDays;
        if (daysToGenerate <= 0) return futureSlots;

        // Yeni slotları ekle
        LocalDate start = today.plusDays(1);
        List<LocalDateTime> newSlots = generateSlotsFromDate(start, daysToGenerate);

        futureSlots.addAll(newSlots);
        return futureSlots;
    }
    public static List<LocalDateTime> generateSlotsFromDate(LocalDate startDate, int days) {
        List<LocalDateTime> slots = new ArrayList<>();

        for (int day = 0; day < days; day++) {
            LocalDate currentDate = startDate.plusDays(day);
            if (currentDate.getDayOfWeek().getValue() >= 6) continue;

            LocalTime time = LocalTime.of(9, 0);
            while (time.isBefore(LocalTime.of(12, 0))) {
                slots.add(currentDate.atTime(time));
                time = time.plusMinutes(20);
            }

            time = LocalTime.of(13, 0);
            while (time.isBefore(LocalTime.of(16, 0))) {
                slots.add(currentDate.atTime(time));
                time = time.plusMinutes(20);
            }
        }

        return slots;
    }

}
