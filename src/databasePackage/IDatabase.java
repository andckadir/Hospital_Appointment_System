package databasePackage;

public interface IDatabase extends IPatientDatabase,IDoctorDatabase,IAdminDatabase {
    public void refresh();
}
