package databasePackage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import userPackage.AbstractUser;
import java.lang.reflect.Type;
import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.*;
import databasePackage.LocalDateTimeTypeAdapter;

public class UserRepository<T extends AbstractUser> {
    private Map<String,T> map;
    private final Gson gson;
    private final File file;
    private final Class<T> clazz;

    public UserRepository(File file, Class<T> clazz) {
        this.file = file;
        this.clazz = clazz;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        this.map = loadFromFile();
    }


    public boolean save(T object) {
        if (map == null) {
            map = new HashMap<>();
        }
        if (object == null){
            System.err.println("User can not be saved. User is null!");
            return false;
        }
        if (!object.isValidated() || object.getSystemId() == null){
            System.err.println("User can not be saved. Not satisfied the required validation conditions!");
            return false;
        }
        if (!map.containsKey(object.getSystemId())) {
            if (map.containsValue(object)){
                throw new IllegalStateException("There is a contradiction in map");
            }
            map.put(object.getSystemId(),object);
            saveToFile();
            return true;
        }else {
            System.err.println(object.getUserType() + " already exist in database with identificationNumber -> " + object.getIdentificationNumber());
            return false;
        }
    }

    public boolean update(String systemId,T object){
        if (!object.isValidated()){
            System.err.println("User can not be saved. Not satisfied the required validation conditions!");
        }
        T existingUser = findById(systemId);
        if (existingUser != null){
            map.put(systemId,object);
            var p = map.get(systemId);
            saveToFile();
            return true;
        }
        System.err.println(object.getUserType() + " can not be updated because not exist in database with identificationNumber -> "+ object.getIdentificationNumber());
        return false;
    }

    public Map<String,T> getMap() {
        return map != null ? map : new HashMap<>();
    }

    public void clear() {
        if (map != null) {
            map.clear();
            saveToFile();
        }
    }

    public boolean remove(T object) {
        if (map != null && map.containsKey(object.getSystemId())){
            if (!map.containsValue(object)){
                throw new IllegalStateException("There is a contradiction in map");
            }
            map.remove(object.getSystemId());
            saveToFile();
            return true;
        }
        System.err.println(object.getUserType() + " can not be removed because not exist in database with identificationNumber -> "+ object.getIdentificationNumber());
        return false;
    }

    public int size() {
        return map != null ? map.size() : 0;
    }

    public boolean isEmpty() {
        return map == null || map.isEmpty();
    }

    public void refresh() {
        this.map = loadFromFile();
    }

    public T findById(String systemId){
        if (map == null){
            return null;
        }
        if (map.containsKey(systemId)){
            return map.get(systemId);
        }else {
            System.err.println("User can not be found with systemId -> "+ systemId);
            return null;
        }
    }

    public T findByIdentificationNumber(String identificationNumber){
        if (map == null){
            return null;
        }
        for (T t : map.values()) {
            if (t.getIdentificationNumber().equals(identificationNumber)) {
                return t;
            }
        }
        System.err.println("User can not be found with identificationNumber -> "+identificationNumber);
        return null;
    }

    public boolean contains(String identificationNumber){
        for (T value : map.values()){
            if (value.getIdentificationNumber().equals(identificationNumber)){
                return true;
            }
        }
        return false;
    }

    private void saveToFile() {
        if (map == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(map, writer);
        } catch (IOException e) {
            System.err.println("Error writing to file : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, T> loadFromFile() {
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Type mapType = TypeToken.getParameterized(Map.class, String.class, clazz).getType();
            Map<String, T> loadedMap = gson.fromJson(reader, mapType);
            return loadedMap != null ? loadedMap : new HashMap<>();
        } catch (IOException e) {
            System.err.println("Error while reading the file: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void listAllUsers() {
        if (map == null || map.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("=== User List ===");
        for (T user : map.values()) {
            System.out.println(user); // if toString is overridden, it will display detailed info
        }
    }

    public boolean removeByTc(String tc) {
        Iterator<Map.Entry<String, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, T> entry = iterator.next();
            T user = entry.getValue();
            if (user.getIdentificationNumber().equals(tc)) {
                iterator.remove();
                saveToFile(); // Also remove from JSON
                return true;
            }
        }
        return false;
    }

}