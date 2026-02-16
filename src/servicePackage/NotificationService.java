package servicePackage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import userPackage.AbstractUser;

public class NotificationService {

    /**
     * Kanal bilgisiyle bildirim gönderir.
     * @param user Bildirim gönderilecek kullanıcı
     * @param message Mesaj içeriği
     * @param channel "gmail" veya "sms"
     */
    public void send(AbstractUser user, String message, String channel) {

        LocalDateTime sentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedTime = sentTime.format(formatter);

        String fullName = user.getName() + " " + user.getSurname();

        System.out.println(" ");
        System.out.println("Notification sent to: " + fullName);

        // Kanal bilgisine göre iletişim bilgisi ekle
        if ("gmail".equalsIgnoreCase(channel)) {
            System.out.println("Channel: Gmail (" + user.getEmail() + ")");
        } else if ("sms".equalsIgnoreCase(channel)) {
            System.out.println("Channel: SMS (" + user.getPhone() + ")");
        } else {
            System.out.println("Channel: Unknown");
        }

        System.out.println("Message: " + message);
        System.out.println("Sent Time: " + formattedTime);
        System.out.println(" ");
    }
}
