import java.util.List;
public interface RecordManager {
    void add(CrimeRecord record);
    boolean update(String id, CrimeRecord record);
    boolean delete(String id);
    List<CrimeRecord> search(String keyword);
}
