import java.io.Serializable;
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private String name;
    protected Person(String id, String name) { this.id=id; setName(name); }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){
        if(name==null || name.trim().isEmpty()) throw new InvalidRecordException("Name cannot be empty.");
        this.name=name.trim();
    }
    public abstract String getRole();
}
