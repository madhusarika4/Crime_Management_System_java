public class Officer extends Person {
    private static final long serialVersionUID = 1L;
    private String rank,badge;
    public Officer(String id,String name,String rank,String badge){super(id,name);setRank(rank);setBadge(badge);}
    public String getRank(){return rank;} public void setRank(String r){if(r==null||r.isBlank())throw new InvalidRecordException("Rank is required.");rank=r.trim();}
    public String getBadge(){return badge;} public void setBadge(String b){if(b==null||b.isBlank())throw new InvalidRecordException("Badge number is required.");badge=b.trim();}
    @Override public String getRole(){return "Officer";}
}
