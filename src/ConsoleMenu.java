import databasePackage.Database;
import servicePackage.AuthenticationService;
import servicePackage.PasswordService;
import servicePackage.ValidationService;
import userPackage.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import Appointment.Appointment;
import Appointment.AppointmentService;

public class ConsoleMenu {
	private final Scanner scanner = new Scanner(System.in);
	private final ValidationService validationService = ValidationService.getInstance();
	private final AuthenticationService authService = new AuthenticationService();
	private final BuilderFactory builderFactory = new BuilderFactory();
	private final Database database = Database.getInstance();
	private final String[] specialties = {"Cardiology", "Neurology", "Internal Medicine", "Ophthalmology",
			"Orthopedics", "Psychiatry", "Otorhinolaryngology (ENT)", "Urology", "Dermatology",
			"Gynecology and Obstetrics", "Pediatrics", "Endocrinology", "Gastroenterology", "Hematology", "Oncology",
			"Nephrology", "Pulmonology", "Rheumatology", "Allergy and Immunology", "Anesthesiology",
			"Emergency Medicine", "General Surgery", "Plastic Surgery", "Thoracic Surgery", "Cardiovascular Surgery",
			"Infectious Diseases", "Geriatrics", "Medical Genetics", "Nuclear Medicine", "Pathology"};

	public void run() {
		while (true) {
			System.out.println("=== HOSPITAL APPOINTMENT SYSTEM ===");
			System.out.println("1. Login");
			System.out.println("2. Register");
			System.out.println("3. Admin Login");
			System.out.println("0. Exit");
			System.out.print("Your choice: ");

			int choice = getIntInput();

			switch (choice) {
				case 1 -> login();
				case 2 -> register();
				case 3 -> adminMenu();
				case 0 -> {
					System.out.println("Exiting system...");
					return;
				}
				default -> System.out.println("Invalid selection.");
			}
		}
	}

	// Ortak input metodları
	private int getIntInput() {
		try {
			return Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
			return -1;
		}
	}

	private int getValidChoice(int min, int max, String prompt) {
		int choice = -1;
		while (choice < min || choice > max) {
			System.out.print(prompt);
			choice = getIntInput();
			if (choice < min || choice > max) {
				System.out.println("Invalid selection. Please choose between " + min + " and " + max + ".");
			}
		}
		return choice;
	}

	// Kullanıcı bilgilerini görüntüleme (ortak metod)
	private void displayUserInfo(AbstractUser user) {
		System.out.println("\n=== User Information ===");
		System.out.println("System ID: " + user.getSystemId());
		System.out.println("Name: " + user.getName() + " " + user.getSurname());
		System.out.println("ID Number: " + user.getIdentificationNumber());
		System.out.println("Phone: " + (user.getPhone() != null ? user.getPhone() : "Not provided"));
		System.out.println("Email: " + (user.getEmail() != null ? user.getEmail() : "Not provided"));
		System.out.println("Age: " + (user.getAge() != null ? user.getAge() : "Not provided"));
		System.out.println("Height: " + (user.getHeight() != null ? user.getHeight() + " cm" : "Not provided"));
		System.out.println("Weight: " + (user.getWeight() != null ? user.getWeight() + " kg" : "Not provided"));

		if (user instanceof Doctor) {
			Doctor doctor = (Doctor) user;
			System.out.println("Specialization: " + (doctor.getSpecialization() != null ? doctor.getSpecialization() : "Not specified"));
		}
		System.out.println("========================");
	}

	// Şifre değiştirme (ortak metod)
	private void changePassword(AbstractUser user) {
		System.out.println("\n=== Change Password ===");
		System.out.print("Enter current password: ");
		String currentPassword = scanner.nextLine();

		System.out.print("Enter new password: ");
		String newPassword = scanner.nextLine();

		System.out.print("Confirm new password: ");
		String confirmPassword = scanner.nextLine();

		if (!newPassword.equals(confirmPassword)) {
			System.out.println("Passwords do not match!");
			return;
		}

		if (user.changePassword(currentPassword, newPassword)) {
			System.out.println("Password changed successfully!");
		} else {
			System.out.println("Password change failed!");
		}
	}

	// Hasta bilgilerini güncelleme
	private void updatePatientInfo(Patient patient) {
		System.out.println("\n=== Update Patient Information ===");
		System.out.println("Current Information:");
		displayUserInfo(patient);

		System.out.println("\nWhat would you like to update?");
		System.out.println("1. Name");
		System.out.println("2. Surname");
		System.out.println("3. Phone");
		System.out.println("4. Email");
		System.out.println("5. Age");
		System.out.println("6. Height");
		System.out.println("7. Weight");
		System.out.println("8. Update All Information");
		System.out.println("0. Cancel");

		int choice = getValidChoice(0, 8, "Your choice: ");

		if (choice == 0) return;

		PatientUpdateBuilder builder = builderFactory.createUpdatedPatient(patient);

		switch (choice) {
			case 1 -> {
				System.out.print("Enter new name: ");
				String name = scanner.nextLine();
				if (validationService.validateName("name",name))
					builder.withName(scanner.nextLine());
			}
			case 2 -> {
				System.out.print("Enter new surname: ");
				String surname = scanner.nextLine();
				if (validationService.validateName("surname",surname))
					builder.withSurname(scanner.nextLine());
			}
			case 3 -> {
				System.out.print("Enter new phone: ");
				String  phone = scanner.nextLine();
				if (validationService.validatePhone(phone))
					builder.withPhone(scanner.nextLine());
			}
			case 4 -> {
				System.out.print("Enter new email: ");
				String  email = scanner.nextLine();
				if (validationService.validateEmail(email))
					builder.withEmail(scanner.nextLine());
			}
			case 5 -> {
				System.out.print("Enter new age: ");
				int age = getIntInput();
				if (validationService.validateAge(age))
					builder.withAge(age);
			}
			case 6 -> {
				System.out.print("Enter new height (cm): ");
				int height = getIntInput();
				if (validationService.validateHeight(height))
					builder.withHeight(height);
			}
			case 7 -> {
				System.out.print("Enter new weight (kg): ");
				int weight = getIntInput();
				if (validationService.validateWeight(weight))
					builder.withWeight(weight);
			}
			case 8 -> updateAllPatientInfo(builder);
		}

		try {
			Patient updatedPatient = builder.build();
			if (updatedPatient.isValidated()) {
				database.updatePatient(updatedPatient);
				System.out.println("Patient information updated successfully!");
			} else {
				System.out.println("Update failed due to validation errors.");
			}
		} catch (Exception e) {
			System.out.println("Update failed: " + e.getMessage());
		}
	}

	private void updateAllPatientInfo(PatientUpdateBuilder builder) {
		System.out.print("Enter new name: ");
		String name = scanner.nextLine();
		if (validationService.validateName("name",name))
			builder.withName(scanner.nextLine());

		System.out.print("Enter new surname: ");
		String surname = scanner.nextLine();
		if (validationService.validateName("surname",surname))
			builder.withSurname(scanner.nextLine());

		System.out.print("Enter new phone: ");
		String phone = scanner.nextLine();
		if (validationService.validatePhone(phone))
			builder.withPhone(scanner.nextLine());

		System.out.print("Enter new email: ");
		String email = scanner.nextLine();
		if (validationService.validateEmail(email))
			builder.withEmail(scanner.nextLine());

		System.out.print("Enter new age: ");
		int age = getIntInput();
		if (validationService.validateAge(age))
			builder.withAge(age);

		System.out.print("Enter new height (cm): ");
		int height = getIntInput();
		if (validationService.validateHeight(height))
			builder.withHeight(height);

		System.out.print("Enter new weight (kg): ");
		int weight = getIntInput();
		if (validationService.validateWeight(weight))
			builder.withWeight(weight);
	}

	// Doktor bilgilerini güncelleme
	private void updateDoctorInfo(Doctor doctor) {
		System.out.println("\n=== Update Doctor Information ===");
		System.out.println("Current Information:");
		displayUserInfo(doctor);

		System.out.println("\nWhat would you like to update?");
		System.out.println("1. Name");
		System.out.println("2. Surname");
		System.out.println("3. Phone");
		System.out.println("4. Email");
		System.out.println("5. Age");
		System.out.println("6. Height");
		System.out.println("7. Weight");
		System.out.println("8. Specialization");
		System.out.println("9. Update All Information");
		System.out.println("0. Cancel");

		int choice = getValidChoice(0, 9, "Your choice: ");

		if (choice == 0) return;

		DoctorUpdateBuilder builder = builderFactory.createUpdatedDoctor(doctor);

		switch (choice) {
			case 1 -> {
				System.out.print("Enter new name: ");
				String name = scanner.nextLine();
				if (validationService.validateName("name",name))
					builder.withName(scanner.nextLine());
			}
			case 2 -> {
				System.out.print("Enter new surname: ");
				String surname = scanner.nextLine();
				if (validationService.validateName("surname",surname))
					builder.withSurname(scanner.nextLine());
			}
			case 3 -> {
				System.out.print("Enter new phone: ");
				String  phone = scanner.nextLine();
				if (validationService.validatePhone(phone))
					builder.withPhone(scanner.nextLine());
			}
			case 4 -> {
				System.out.print("Enter new email: ");
				String email = scanner.nextLine();
				if (validationService.validateEmail(email))
					builder.withEmail(scanner.nextLine());
			}
			case 5 -> {
				System.out.print("Enter new age: ");
				int age = getIntInput();
				if (validationService.validateAge(age))
					builder.withAge(age);
			}
			case 6 -> {
				System.out.print("Enter new height (cm): ");
				int height = getIntInput();
				if (validationService.validateHeight(height))
					builder.withHeight(height);
			}
			case 7 -> {
				System.out.print("Enter new weight (kg): ");
				int weight = getIntInput();
				if (validationService.validateWeight(weight))
					builder.withWeight(weight);
			}
			case 8 -> {
				System.out.println("Select new specialization:");
				for (int i = 0; i < specialties.length; i++) {
					System.out.println((i + 1) + ". " + specialties[i]);
				}
				int specChoice = getValidChoice(1, specialties.length, "Choice: ");
				builder.withSpecialization(specialties[specChoice - 1]);
			}
			case 9 -> updateAllDoctorInfo(builder);
		}

		try {
			Doctor updatedDoctor = builder.build();
			if (updatedDoctor.isValidated()) {
				database.updateDoctor(updatedDoctor);
				System.out.println("Doctor information updated successfully!");
			} else {
				System.out.println("Update failed due to validation errors.");
			}
		} catch (Exception e) {
			System.out.println("Update failed: " + e.getMessage());
		}
	}

	private void updateAllDoctorInfo(DoctorUpdateBuilder builder) {
		System.out.print("Enter new name: ");
		String name = scanner.nextLine();
		if (validationService.validateName("name",name))
			builder.withName(scanner.nextLine());

		System.out.print("Enter new surname: ");
		String surname = scanner.nextLine();
		if (validationService.validateName("surname",surname))
			builder.withSurname(scanner.nextLine());

		System.out.print("Enter new phone: ");
		String phone = scanner.nextLine();
		if (validationService.validatePhone(phone))
			builder.withPhone(scanner.nextLine());

		System.out.print("Enter new email: ");
		String email = scanner.nextLine();
		if (validationService.validateEmail(email))
			builder.withEmail(scanner.nextLine());

		System.out.print("Enter new age: ");
		int age = getIntInput();
		if (validationService.validateAge(age))
			builder.withAge(age);

		System.out.print("Enter new height (cm): ");
		int height = getIntInput();
		if (validationService.validateHeight(height))
			builder.withHeight(height);

		System.out.print("Enter new weight (kg): ");
		int weight = getIntInput();
		if (validationService.validateWeight(weight))
			builder.withWeight(weight);

		System.out.println("Select new specialization:");
		for (int i = 0; i < specialties.length; i++) {
			System.out.println((i + 1) + ". " + specialties[i]);
		}
		int specChoice = getValidChoice(1, specialties.length, "Choice: ");
		builder.withSpecialization(specialties[specChoice - 1]);
	}

	// Kayıt ekranı
	private void register() {
		int selection = getValidChoice(1, 2, "1. Patient Registration\n2. Doctor Registration\nSelection: ");

		System.out.print("ID Number: ");
		String idNumber = scanner.nextLine();
		System.out.print("Name: ");
		String name = scanner.nextLine();
		System.out.print("Surname: ");
		String surname = scanner.nextLine();
		System.out.print("Password: ");
		String password = scanner.nextLine();

		if (selection == 1) {
			registerPatient(idNumber, name, surname, password);
		} else {
			registerDoctor(idNumber, name, surname, password);
		}
	}

	private void registerPatient(String idNumber, String name, String surname, String password) {
		try {
			System.out.print("Age: ");
			int age = getIntInput();
			System.out.print("Phone: ");
			String phone = scanner.nextLine();
			System.out.print("Email: ");
			String email = scanner.nextLine();
			System.out.print("Height (cm): ");
			Integer height = getIntInput();
			if (height<0) height = null;
			System.out.print("Weight (kg): ");
			Integer weight = getIntInput();
			if (weight<0) weight = null;

			Patient patient = builderFactory.createPatient(idNumber, name, surname, password)
					.withAge(age).withPhone(phone).withEmail(email)
					.withHeight(height).withWeight(weight).build();

			database.savePatient(patient);
			PasswordService.getInstance().savePassword(patient.getSystemId(), password);
			System.out.println("Patient registered successfully!");
			loginPatientDirect();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	private void registerDoctor(String idNumber, String name, String surname, String password) {
		try {
			System.out.println("Select your specialization:");
			for (int i = 0; i < specialties.length; i++) {
				System.out.println((i + 1) + ". " + specialties[i]);
			}

			int specChoice = getValidChoice(1, specialties.length, "Choice: ");
			String specialization = specialties[specChoice - 1];

			System.out.print("Phone: ");
			String phone = scanner.nextLine();
			System.out.print("Email: ");
			String email = scanner.nextLine();
			System.out.print("Age: ");
			int age = getIntInput();

			Doctor doctor = new DoctorBuilder(idNumber, name, surname, password)
					.withSpecialization(specialization).withPhone(phone)
					.withEmail(email).withAge(age).build();

			database.saveDoctor(doctor);
			System.out.println("Doctor registered successfully!");
			loginDoctorDirect();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	// Giriş işlemleri
	private void login() {
		int loginType = getValidChoice(1, 2, "1. Patient Login\n2. Doctor Login\nSelection: ");

		System.out.print("ID Number: ");
		String id = scanner.nextLine();
		System.out.print("Password: ");
		String password = scanner.nextLine();

		if (loginType == 1) {
			Patient patient = authService.loginPatient(id, password);
			if (patient != null) {
				System.out.println("Welcome, " + patient.getName());
				patientMenu(patient);
			} else {
				System.out.println("Login failed.");
			}
		} else {
			Doctor doctor = authService.loginDoctor(id, password);
			if (doctor != null) {
				System.out.println("Welcome Dr. " + doctor.getName());
				doctorMenu(doctor);
			} else {
				System.out.println("Login failed.");
			}
		}
	}

	// Hasta girişini doğrudan yapan metod
	private void loginPatientDirect() {
		boolean success = false;
		while (!success) {
			System.out.println("\n=== PATIENT LOGIN ===");
			System.out.print("Identification Number: ");
			String idNumber = scanner.nextLine();
			System.out.print("Password: ");
			String password = scanner.nextLine();

			Patient patient = authService.loginPatient(idNumber, password);
			if (patient != null) {
				System.out.println("Welcome, " + patient.getName());
				patientMenu(patient);
				success = true;
			} else {
				System.out.println("Login failed, please try again.");
			}
		}
	}

	// Doktor girişini doğrudan yapan metod
	private void loginDoctorDirect() {
		boolean success = false;
		while (!success) {
			System.out.println("\n=== DOCTOR LOGIN ===");
			System.out.print("Identification Number: ");
			String idNumber = scanner.nextLine();
			System.out.print("Password: ");
			String password = scanner.nextLine();

			Doctor doctor = authService.loginDoctor(idNumber, password);
			if (doctor != null) {
				System.out.println("Welcome, Dr. " + doctor.getName());
				doctorMenu(doctor);
				success = true;
			} else {
				System.out.println("Login failed, please try again.");
			}
		}
	}

	// Hasta paneli
	private void patientMenu(Patient patient) {
		while (true) {
			System.out.println("\n=== PATIENT PANEL ===");
			System.out.println("1. Book Appointment");
			System.out.println("2. View / Cancel Appointments");
			System.out.println("3. View Profile");
			System.out.println("4. Update Profile");
			System.out.println("5. Change Password");
			System.out.println("6. Logout");
			System.out.print("Your choice: ");

			int choice = getIntInput();
			patient = database.findPatientById(patient.getSystemId());
			switch (choice) {
				case 1 -> bookAppointmentFlow(patient);
				case 2 -> viewOrCancelAppointments(patient);
				case 3 -> displayUserInfo(patient);
				case 4 -> updatePatientInfo(patient);
				case 5 -> changePassword(patient);
				case 6 -> {
					System.out.println("Logging out...");
					return;
				}
				default -> System.out.println("Invalid selection.");
			}
		}
	}

	private void viewOrCancelAppointments(Patient patient) {
		System.out.println("\n=== View / Cancel Appointments ===");
		System.out.println("1. All Appointments");
		System.out.println("2. Active Appointments (Cancelable)");
		System.out.println("3. Cancelled Appointments");

		int filterChoice = getValidChoice(1, 3, "Your choice: ");

		String filter;
		switch (filterChoice) {
			case 1:
				filter = "all";
				AppointmentService.getInstance().listAppointmentsByPatient(patient, filter);
				break;

			case 2:
				filter = "active";
				List<Appointment> activeAppointments = AppointmentService.getInstance().getAppointmentsByPatient(patient, filter);
				if (activeAppointments.isEmpty()) {
					System.out.println("You have no active appointments.");
					break;
				}

				for (int i = 0; i < activeAppointments.size(); i++) {
					System.out.println((i + 1) + ". " + activeAppointments.get(i));
				}

				System.out.print("Enter the number of the appointment to cancel (0 to cancel): ");
				int cancelIndex = getIntInput() - 1;

				if (cancelIndex >= 0 && cancelIndex < activeAppointments.size()) {
					LocalDateTime dateTime = activeAppointments.get(cancelIndex).getDateTime();
					AppointmentService.getInstance().cancelAppointment(patient, dateTime);
				} else {
					System.out.println("Cancel operation aborted or invalid selection.");
				}
				break;

			case 3:
				filter = "cancelled";
				AppointmentService.getInstance().listAppointmentsByPatient(patient, filter);
				break;
		}
	}

	// Doktor paneli
	private void doctorMenu(Doctor doctor) {
		while (true) {
			System.out.println("\n=== DOCTOR PANEL ===");
			System.out.println("1. View Active Appointments");
			System.out.println("2. View Profile");
			System.out.println("3. Update Profile");
			System.out.println("4. Change Password");
			System.out.println("5. Logout");
			System.out.print("Your choice: ");

			int choice = getIntInput();

			switch (choice) {
				case 1 -> viewDoctorAppointments(doctor);
				case 2 -> displayUserInfo(doctor);
				case 3 -> updateDoctorInfo(doctor);
				case 4 -> changePassword(doctor);
				case 5 -> {
					System.out.println("Logging out...");
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void viewDoctorAppointments(Doctor doctor) {
		List<Appointment> doctorAppointments = AppointmentService.getInstance().getAppointmentsByDoctor(doctor)
				.stream().filter(appt -> !appt.isCancelled()).toList();

		if (doctorAppointments.isEmpty()) {
			System.out.println("You have no active appointments.");
			return;
		}

		System.out.println("\n=== Active Appointments ===");
		for (int i = 0; i < doctorAppointments.size(); i++) {
			System.out.printf("%d. %s\n", i + 1, doctorAppointments.get(i).getDateTime());
		}

		System.out.print("Select an appointment to view details (0 to go back): ");
		int detailChoice = getIntInput();

		if (detailChoice <= 0 || detailChoice > doctorAppointments.size())
			return;

		Appointment selected = doctorAppointments.get(detailChoice - 1);
		System.out.println("\n=== Appointment Details ===");
		System.out.println("Date: " + selected.getDateTime());
		System.out.println("Status: Active");
		Patient p = selected.getPatient();
		System.out.println("--- Patient Info ---");
		System.out.println("Name: " + p.getName() + " " + p.getSurname());
		System.out.println("ID Number: " + p.getIdentificationNumber());
		System.out.println("Age: " + p.getAge());
		System.out.println("Phone: " + p.getPhone());
		System.out.println("Email: " + p.getEmail());

		System.out.print("Do you want to cancel this appointment? (Y/N): ");
		String cancel = scanner.nextLine();
		if (cancel.equalsIgnoreCase("Y")) {
			AppointmentService.getInstance().cancelAppointmentByDoctor(selected);
		}
	}

	// Admin paneli
	private void adminMenu() {
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Enter admin password: ");
	    String password = scanner.nextLine();

	    if (!password.equals("semihutku")) {
	        System.out.println("Incorrect password. Access denied.");
	        return;
	    }

	    while (true) {
	        System.out.println("\n=== ADMIN PANEL ===");
	        System.out.println("1. List All Doctors");
	        System.out.println("2. List All Patients");
	        System.out.println("3. Delete Doctor");
	        System.out.println("4. Logout");
	        System.out.print("Your choice: ");

	        int choice = getIntInput();

	        switch (choice) {
	            case 1 -> Database.getInstance().listAllDoctors();
	            case 2 -> Database.getInstance().listAllPatients();
	            case 3 -> deleteDoctorById();
	            case 4 -> {
	                System.out.println("Logging out from admin panel...");
	                return;
	            }
	            default -> System.out.println("Invalid choice.");
	        }
	    }
	}


	// TC numarasına göre doktor silme
	private void deleteDoctorById() {
		System.out.println("\n=== Delete Doctor ===");
		Database.getInstance().listAllDoctors();
		System.out.print("Enter the ID number of the doctor to delete: ");
		String idNumber = scanner.nextLine();

		boolean deleted = Database.getInstance().removeDoctorByTc(idNumber);
		if (deleted) {
			System.out.println("Doctor successfully deleted.");
		} else {
			System.out.println("No doctor found with the given ID number.");
		}
	}

	// Hasta için randevu alma akışı
	private void bookAppointmentFlow(Patient patient) {
		System.out.println("\n=== Select Specialization ===");
		List<String> branchList = database.getAllDoctorBranches();
		for (int i = 0; i < branchList.size(); i++) {
			System.out.println((i + 1) + ". " + branchList.get(i));
		}

		int branchIndex = getValidChoice(1, branchList.size(), "Your choice: ") - 1;
		String selectedBranch = branchList.get(branchIndex);

		List<Doctor> doctors = database.findDoctorsBySpecialization(selectedBranch);
		if (doctors.isEmpty()) {
			System.out.println("No doctors found in this specialization.");
			return;
		}

		System.out.println("\n=== Select Doctor ===");
		for (int i = 0; i < doctors.size(); i++) {
			System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName() + " " + doctors.get(i).getSurname());
		}

		int docIndex = getValidChoice(1, doctors.size(), "Your choice: ") - 1;
		Doctor selectedDoctor = doctors.get(docIndex);

		List<LocalDateTime> availableSlots = AppointmentService.getInstance().getAvailableSlots(selectedDoctor);
		if (availableSlots.isEmpty()) {
			System.out.println("No available appointment slots for this doctor.");
			return;
		}

		System.out.println("\n=== Available Slots ===");
		for (int i = 0; i < availableSlots.size(); i++) {
			System.out.println((i + 1) + ". " + availableSlots.get(i));
		}

		int slotIndex = getValidChoice(1, availableSlots.size(), "Your choice: ") - 1;
		LocalDateTime selectedSlot = availableSlots.get(slotIndex);

		AppointmentService.getInstance().bookAppointment(patient, selectedDoctor, selectedSlot);
	}
}