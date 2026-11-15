import com.sun.net.httpserver.*;
import java.io.*;import java.net.*;import java.nio.charset.StandardCharsets;import java.nio.file.*;import java.util.*;import java.util.stream.Collectors;

public class Main {
 static CrimeRecordManager db=CrimeRecordManager.load(Paths.get("data","crms-data.ser"));
 public static void main(String[] args)throws Exception{
  HttpServer s=HttpServer.create(new InetSocketAddress(8765),0);
  s.createContext("/api/login",Main::login);s.createContext("/api/data",Main::data);
  s.createContext("/api/crimes",Main::crimes);s.createContext("/api/criminals",Main::criminals);
  s.createContext("/api/officers",Main::officers);s.createContext("/api/export",Main::export);
  s.createContext("/",Main::staticFile);s.setExecutor(null);s.start();
  System.out.println("CRMS running: http://localhost:8765  |  Demo login: admin / admin123");
 }
 static void login(HttpExchange x)throws IOException{Map<String,String>d=body(x);boolean ok="admin".equals(d.get("username"))&&"admin123".equals(d.get("password"));json(x,ok?200:401,ok?"{\"success\":true}":"{\"message\":\"Invalid username or password.\"}");}
 static void data(HttpExchange x)throws IOException{json(x,200,Json.val(db.snapshot()));}
 static void crimes(HttpExchange x)throws IOException{
  String p=x.getRequestURI().getPath(),m=x.getRequestMethod();
  if(p.equals("/api/crimes")&&m.equals("GET")){String q=query(x).getOrDefault("q",""),st=query(x).getOrDefault("status",""),ty=query(x).getOrDefault("type","");List<CrimeRecord> a=db.search(q).stream().filter(r->st.isEmpty()||r.getStatus().equals(st)).filter(r->ty.isEmpty()||r.getType().equals(ty)).collect(Collectors.toList());json(x,200,Json.val(a));return;}
  if(p.equals("/api/crimes")&&m.equals("POST")){Map<String,String>d=body(x);try{CrimeRecord r=crimeFrom(d,db.nextCrimeId());db.add(r);json(x,201,Json.val(r));}catch(Exception e){error(x,e);}return;}
  if(p.startsWith("/api/crimes/")){String id=decode(p.substring(12));if(m.equals("PUT")){try{CrimeRecord r=crimeFrom(body(x),id);if(!db.update(id,r)){json(x,404,"{\"message\":\"Record not found\"}");return;}json(x,200,Json.val(r));}catch(Exception e){error(x,e);}return;}if(m.equals("DELETE")){json(x,db.delete(id)?200:404,"{\"success\":true}");return;}}
  json(x,405,"{\"message\":\"Unsupported request\"}");
 }
 static CrimeRecord crimeFrom(Map<String,String>d,String id){String officer=d.getOrDefault("officerId",""),criminal=d.getOrDefault("criminalId","");if(!officer.isEmpty()&&db.findOfficer(officer)==null)throw new IllegalArgumentException("Select a valid officer.");if(!criminal.isEmpty()&&db.findCriminal(criminal)==null)throw new IllegalArgumentException("Select a valid suspect.");return new CrimeRecord(id,d.get("type"),d.get("location"),d.get("date"),d.get("description"),d.getOrDefault("status","Open"),officer,criminal);}
 static void criminals(HttpExchange x)throws IOException{
  String p=x.getRequestURI().getPath(),m=x.getRequestMethod();
  if(p.equals("/api/criminals")&&m.equals("POST")){Map<String,String>d=body(x);try{String id=db.nextCriminalId();Criminal c=new Criminal(id,d.get("name"),Integer.parseInt(d.get("age")),d.get("address"),d.getOrDefault("status","Wanted"));db.putCriminal(c);json(x,201,Json.val(c));}catch(Exception e){error(x,e);}return;}
  if(p.startsWith("/api/criminals/")){String id=decode(p.substring(15));if(m.equals("PUT")){Map<String,String>d=body(x);try{if(db.findCriminal(id)==null){json(x,404,"{\"message\":\"Criminal not found\"}");return;}Criminal c=new Criminal(id,d.get("name"),Integer.parseInt(d.get("age")),d.get("address"),d.get("status"));db.putCriminal(c);json(x,200,Json.val(c));}catch(Exception e){error(x,e);}return;}if(m.equals("DELETE")){json(x,db.deleteCriminal(id)?200:404,"{\"success\":true}");return;}}
  json(x,405,"{\"message\":\"Unsupported request\"}");
 }
 static void officers(HttpExchange x)throws IOException{
  String p=x.getRequestURI().getPath(),m=x.getRequestMethod();
  if(p.equals("/api/officers")&&m.equals("POST")){Map<String,String>d=body(x);try{String badge=d.get("badge");if(db.getOfficers().stream().anyMatch(o->o.getBadge().equals(badge)))throw new IllegalArgumentException("That badge number is already in use.");Officer o=new Officer(db.nextOfficerId(),d.get("name"),d.get("rank"),badge);db.putOfficer(o);json(x,201,Json.val(o));}catch(Exception e){error(x,e);}return;}
  if(p.startsWith("/api/officers/")){String id=decode(p.substring(14));if(m.equals("PUT")){Map<String,String>d=body(x);try{if(db.findOfficer(id)==null){json(x,404,"{\"message\":\"Officer not found\"}");return;}String badge=d.get("badge");if(db.getOfficers().stream().anyMatch(o->o.getBadge().equals(badge)&&!o.getId().equals(id)))throw new IllegalArgumentException("That badge number is already in use.");Officer o=new Officer(id,d.get("name"),d.get("rank"),badge);db.putOfficer(o);json(x,200,Json.val(o));}catch(Exception e){error(x,e);}return;}if(m.equals("DELETE")){json(x,db.deleteOfficer(id)?200:404,"{\"success\":true}");return;}}
  json(x,405,"{\"message\":\"Unsupported request\"}");
 }
 static void export(HttpExchange x)throws IOException{StringBuilder b=new StringBuilder("ID,Type,Location,Date,Status,Officer,Suspect,Details\\r\\n");for(CrimeRecord r:db.getRecords()){b.append(csv(r.getId())).append(',').append(csv(r.getType())).append(',').append(csv(r.getLocation())).append(',').append(csv(r.getDate())).append(',').append(csv(r.getStatus())).append(',').append(csv(nameOfficer(r.getOfficerId()))).append(',').append(csv(nameCriminal(r.getCriminalId()))).append(',').append(csv(r.getDescription())).append("\\r\\n");}byte[] a=b.toString().getBytes(StandardCharsets.UTF_8);x.getResponseHeaders().set("Content-Type","text/csv; charset=utf-8");x.getResponseHeaders().set("Content-Disposition","attachment; filename=crime-records.csv");x.sendResponseHeaders(200,a.length);x.getResponseBody().write(a);x.close();}
 static String nameOfficer(String id){Officer o=db.findOfficer(id);return o==null?"":o.getName();}static String nameCriminal(String id){Criminal c=db.findCriminal(id);return c==null?"":c.getName();}
 static String csv(String s){return "\""+(s==null?"":s).replace("\"","\"\"")+"\"";}
 static void staticFile(HttpExchange x)throws IOException{String u=x.getRequestURI().getPath();if(u.equals("/"))u="/index.html";Path base=Paths.get("web").toAbsolutePath().normalize(),f=base.resolve(u.substring(1)).normalize();if(!f.startsWith(base)||!Files.exists(f)){send(x,404,"Not found");return;}String type=u.endsWith(".css")?"text/css; charset=utf-8":u.endsWith(".js")?"text/javascript; charset=utf-8":"text/html; charset=utf-8";byte[] a=Files.readAllBytes(f);x.getResponseHeaders().set("Content-Type",type);x.sendResponseHeaders(200,a.length);x.getResponseBody().write(a);x.close();}
 static Map<String,String> body(HttpExchange x)throws IOException{return parse(new String(x.getRequestBody().readAllBytes(),StandardCharsets.UTF_8));}
 static Map<String,String> query(HttpExchange x){return parse(x.getRequestURI().getRawQuery()==null?"":x.getRequestURI().getRawQuery());}
 static Map<String,String> parse(String raw){Map<String,String>m=new HashMap<>();if(raw!=null)for(String p:raw.split("&")){String[]a=p.split("=",2);if(a.length==2)m.put(decode(a[0]),decode(a[1]));}return m;}
 static String decode(String s){return URLDecoder.decode(s,StandardCharsets.UTF_8);}
 static void error(HttpExchange x,Exception e)throws IOException{json(x,400,"{\"message\":"+Json.str(e.getMessage()==null?"Invalid input":e.getMessage())+"}");}
 static void json(HttpExchange x,int status,String s)throws IOException{x.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");send(x,status,s);}
 static void send(HttpExchange x,int status,String s)throws IOException{byte[]a=s.getBytes(StandardCharsets.UTF_8);x.sendResponseHeaders(status,a.length);x.getResponseBody().write(a);x.close();}
}
