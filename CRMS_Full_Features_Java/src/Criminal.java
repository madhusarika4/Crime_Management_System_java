public class Criminal extends Person {
    private static final long serialVersionUID = 1L;
    private int age; private String address, status;
    public Criminal(String id,String name,int age,String address,String status){
        super(id,name); setAge(age); setAddress(address); setStatus(status);
    }
    public int getAge(){return age;} public void setAge(int a){if(a<1||a>120)throw new InvalidRecordException("Age must be between 1 and 120.");age=a;}
    public String getAddress(){return address;} public void setAddress(String a){address=a==null?"":a.trim();}
    public String getStatus(){return status;} public void setStatus(String s){if(!java.util.List.of("Wanted","Arrested","Released").contains(s))throw new InvalidRecordException("Invalid criminal status.");status=s;}
    @Override public String getRole(){return "Criminal";}
}
