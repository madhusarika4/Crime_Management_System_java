import java.io.*;
import java.nio.file.*;
import java.util.*;
public class CrimeRecordManager implements RecordManager, Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<CrimeRecord> records=new ArrayList<>();
    private final HashMap<String,Criminal> criminals=new HashMap<>();
    private final HashMap<String,Officer> officers=new HashMap<>();
    private int crimeSeq=1005, criminalSeq=4, officerSeq=4;
    private transient Path file=Paths.get("data","crms-data.ser");
    public CrimeRecordManager(){ }
    public static CrimeRecordManager load(Path path){
        try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(path))){
            CrimeRecordManager m=(CrimeRecordManager)in.readObject();m.file=path;m.repairLinks();return m;
        }catch(Exception e){CrimeRecordManager m=new CrimeRecordManager();m.file=path;m.seed();m.save();return m;}
    }
    public void save(){
        try{Files.createDirectories(file.getParent());try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(file))){out.writeObject(this);}}
        catch(IOException e){throw new RuntimeException("Could not save data: "+e.getMessage(),e);}
    }
    private void repairLinks(){
        for(CrimeRecord r:records){if(!officers.containsKey(r.getOfficerId())){} if(!criminals.containsKey(r.getCriminalId())){}}
    }
    private void seed(){
        officers.put("O-1",new Officer("O-1","Anita Sharma","Inspector","HYD-2201"));
        officers.put("O-2",new Officer("O-2","Vikram Rao","Sub-Inspector","HYD-2217"));
        officers.put("O-3",new Officer("O-3","Meera Nair","Inspector","HYD-2230"));
        criminals.put("C-1",new Criminal("C-1","Ravi Teja",29,"Mehdipatnam","Wanted"));
        criminals.put("C-2",new Criminal("C-2","Imran Khan",34,"Kukatpally","Arrested"));
        criminals.put("C-3",new Criminal("C-3","Sunil Reddy",41,"Uppal","Released"));
        records.add(new CrimeRecord("CR-1001","Theft","Banjara Hills","2026-10-02","Two-wheeler stolen from a parking lot.","Investigating","O-1","C-1"));
        records.add(new CrimeRecord("CR-1002","Fraud","Madhapur","2026-10-11","Online payment fraud reported by a shop owner.","Open","O-2","C-2"));
        records.add(new CrimeRecord("CR-1003","Assault","Secunderabad","2026-09-21","Altercation outside a bus stand.","Closed","O-1","C-3"));
        records.add(new CrimeRecord("CR-1004","Cyber crime","Gachibowli","2026-11-03","Phishing messages sent to bank customers.","Open","O-3",""));
    }
    public synchronized List<CrimeRecord> getRecords(){return new ArrayList<>(records);}
    public synchronized List<Criminal> getCriminals(){return new ArrayList<>(criminals.values());}
    public synchronized List<Officer> getOfficers(){return new ArrayList<>(officers.values());}
    public synchronized Map<String,Object> snapshot(){
        Map<String,Object> m=new LinkedHashMap<>();m.put("crimes",getRecords());m.put("criminals",getCriminals());m.put("officers",getOfficers());return m;
    }
    public synchronized void add(CrimeRecord r){records.add(r);save();}
    public synchronized boolean update(String id,CrimeRecord r){for(int i=0;i<records.size();i++)if(records.get(i).getId().equals(id)){records.set(i,r);save();return true;}return false;}
    public synchronized boolean delete(String id){boolean ok=records.removeIf(r->r.getId().equals(id));if(ok)save();return ok;}
    public synchronized List<CrimeRecord> search(String keyword){
        String q=keyword==null?"":keyword.toLowerCase();List<CrimeRecord> out=new ArrayList<>();
        for(CrimeRecord r:records)if(r.toString().toLowerCase().contains(q))out.add(r);return out;
    }
    public synchronized CrimeRecord findCrime(String id){return records.stream().filter(r->r.getId().equals(id)).findFirst().orElse(null);}
    public synchronized Criminal findCriminal(String id){return criminals.get(id);}
    public synchronized Officer findOfficer(String id){return officers.get(id);}
    public synchronized String nextCrimeId(){return "CR-"+(crimeSeq++);}
    public synchronized String nextCriminalId(){return "C-"+(criminalSeq++);}
    public synchronized String nextOfficerId(){return "O-"+(officerSeq++);}
    public synchronized void putCriminal(Criminal c){criminals.put(c.getId(),c);save();}
    public synchronized boolean deleteCriminal(String id){
        if(criminals.remove(id)==null)return false;
        List<CrimeRecord> changed=new ArrayList<>();
        for(CrimeRecord r:records)if(r.getCriminalId().equals(id))changed.add(new CrimeRecord(r.getId(),r.getType(),r.getLocation(),r.getDate(),r.getDescription(),r.getStatus(),r.getOfficerId(),""));
        for(CrimeRecord r:changed)updateInMemory(r);save();return true;
    }
    public synchronized void putOfficer(Officer o){officers.put(o.getId(),o);save();}
    public synchronized boolean deleteOfficer(String id){
        if(officers.remove(id)==null)return false;
        List<CrimeRecord> changed=new ArrayList<>();
        for(CrimeRecord r:records)if(r.getOfficerId().equals(id))changed.add(new CrimeRecord(r.getId(),r.getType(),r.getLocation(),r.getDate(),r.getDescription(),r.getStatus(),"",r.getCriminalId()));
        for(CrimeRecord r:changed)updateInMemory(r);save();return true;
    }
    private void updateInMemory(CrimeRecord r){for(int i=0;i<records.size();i++)if(records.get(i).getId().equals(r.getId()))records.set(i,r);}
}
