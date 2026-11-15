import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
public class CrimeRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private String type,location,date,description,status,officerId,criminalId;
    public CrimeRecord(String id,String type,String location,String date,String description,String status,String officerId,String criminalId){
        this.id=id; update(type,location,date,description,status,officerId,criminalId);
    }
    public void update(String type,String location,String date,String description,String status,String officerId,String criminalId){
        if(type==null||type.isBlank())throw new InvalidRecordException("Crime type is required.");
        if(location==null||location.isBlank())throw new InvalidRecordException("Location is required.");
        try { LocalDate d=LocalDate.parse(date); if(d.isAfter(LocalDate.now()))throw new InvalidRecordException("Date cannot be in the future."); }
        catch(DateTimeParseException e){throw new InvalidRecordException("Enter a valid date.");}
        if(!java.util.List.of("Open","Investigating","Closed").contains(status))throw new InvalidRecordException("Invalid case status.");
        this.type=type;this.location=location.trim();this.date=date;this.description=description==null?"":description.trim();this.status=status;
        this.officerId=officerId==null?"":officerId;this.criminalId=criminalId==null?"":criminalId;
    }
    public String getId(){return id;} public String getType(){return type;} public String getLocation(){return location;}
    public String getDate(){return date;} public String getDescription(){return description;} public String getStatus(){return status;}
    public String getOfficerId(){return officerId;} public String getCriminalId(){return criminalId;}
    public String toString(){return id+" "+type+" "+location+" "+description+" "+status;}
}
