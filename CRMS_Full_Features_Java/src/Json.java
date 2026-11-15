import java.lang.reflect.*;import java.util.*;
public class Json {
 public static String str(String s){if(s==null)return "null";StringBuilder b=new StringBuilder("\"");for(char c:s.toCharArray()){switch(c){case '"':b.append("\\\"");break;case '\\':b.append("\\\\");break;case '\n':b.append("\\n");break;case '\r':b.append("\\r");break;case '\t':b.append("\\t");break;default:if(c<32)b.append(String.format("\\u%04x",(int)c));else b.append(c);}}return b.append('"').toString();}
 public static String val(Object o){if(o==null)return "null";if(o instanceof String)return str((String)o);if(o instanceof Number||o instanceof Boolean)return o.toString();if(o instanceof Collection){List<String>a=new ArrayList<>();for(Object v:(Collection<?>)o)a.add(val(v));return "["+String.join(",",a)+"]";}if(o instanceof Map){List<String>a=new ArrayList<>();((Map<?,?>)o).forEach((k,v)->a.add(str(String.valueOf(k))+":"+val(v)));return "{"+String.join(",",a)+"}";}
  Map<String,Object> m=new LinkedHashMap<>();for(Method method:o.getClass().getMethods())if(method.getName().startsWith("get")&&method.getParameterCount()==0&&!method.getName().equals("getClass"))try{String k=Character.toLowerCase(method.getName().charAt(3))+method.getName().substring(4);m.put(k,method.invoke(o));}catch(Exception ignored){}return val(m);
 }
}
