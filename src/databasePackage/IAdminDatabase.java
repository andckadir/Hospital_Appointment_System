package databasePackage;
import userPackage.Admin;
import java.util.Map;

public interface IAdminDatabase {
    public boolean saveAdmin(Admin admin);
    public boolean updateAdmin(Admin admin );
    public boolean removeAdmin(Admin admin);
    public boolean removeAdmin(String systemId);
    public Map<String,Admin> getAdminMap();
    public int adminCount();
    public Admin findAdminById(String systemId);
    public Admin findAdminByIdentificationNumber(String identificationNumber);
    public boolean containsAdmin(String identificationNumber);
}
