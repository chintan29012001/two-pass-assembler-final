
import org.json.simple.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.json.simple.parser.*;

class FirstPass
{ 

    public static boolean checkORG(String line)
    {
	    if((line.strip()) == "START")
		    return true;
	    else
	    {
		    System.out.println("START Statement Missing");
		    return false;
	    }
    }
    static String LineCommentRemoved(String s)
    {
        int indexComment=s.indexOf("//");
        if(indexComment==-1)
            return s;
        else
            return s.substring(0, indexComment);
    }
    static JSONObject addSymbol(String address,String type,int c)
    {
        JSONObject obj=new JSONObject();
        obj.put("address",address);
        obj.put("type",type);
        if(type=="variable")
            obj.put("order",c);
        return obj;
    }
    static JSONObject addSymbol(String address,String type)
    {
        JSONObject obj=new JSONObject();
        obj.put("address",address);
        obj.put("type",type);
        return obj;
    }
    static String binconvert(long x)
    {
        String s="";
        //System.out.println(x);
        while(((int)x)>0)
        {
            s+=Long.toString(x%2);
            x=x/2;
        }
        if(s.length()>8)
        {
            System.out.println("Addressing not allowed");
            System.exit(1);
        }
        //System.out.println("x: "+x+" s:"+s);
        for(int i=0;s.length()<8;i++)
            s+="0";
        int len=0;
        String ans=""; 
        while(len<s.length())
        {
            ans+=s.charAt(s.length()-1-len);
            ++len;
        }
        //System.out.println("ans "+ans);
        //System.out.println(ans);
        return ans;

    }
    static String removeLabel(JSONObject SymbolTable,String s,int lc) throws IOException,Exception
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");       
        int indexOfColon=s.indexOf(':');
        if(indexOfColon!=-1)
        {
            SymbolTable.put(s.substring(0,indexOfColon),addSymbol(binconvert(lc),"label"));
            s=removeLabel(s);
        }
        fileWriter.close();     
        return s;

    }
    static int removeSymbol(JSONObject SymbolTable,JSONObject availableOpcodes,String opcode, String s,int lc,int countvars) throws IOException,Exception
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");       
        try
        {
            int indexOfspace1=s.indexOf(' ');
            int indexOfspace2=s.indexOf(' ',indexOfspace1+1);
            //System.out.println("2 "+indexOfspace2);
            if(indexOfspace2!=-1)
            {
                //System.out.println("2 "+indexOfspace2);
                throw new Exception();    
            }
            Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
            long noOfOperands= (long)opcodeJSON.get("operands");
            JSONObject a=(JSONObject)SymbolTable.get(s.substring(indexOfspace1+1));
            if(a==null)
            {
                if(!(opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ")))
                {
                    SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL","variable",countvars));
                    countvars++;    
                }
                else
                {
                    SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL","label"));

                }
            }
            fileWriter.write(SymbolTable.toString());
            fileWriter.close();
            return countvars;
        }
        catch(Exception e)
        {
            
            System.out.println("EXCESS OPERANDS AT "+ lc);
           // input.close();
            fileWriter.close();
            System.exit(1);
            //System.out.println("error mode");
        }
        
        return countvars;

    }
    public static String removeLabel(String line)
    {
        int i = line.indexOf(":");
        if(i==-1)
            return line;
        else
            return line.substring(i+2);
    }
    public static String extractOpcode(String line)
    {
        return ((removeLabel(line)).substring(0,3));	
    }

   public static String checkAndreturnOpcode (JSONObject temp,String code,int lc) throws Exception //reduntat
    {
        String opcode="";
        try
        {
            opcode=extractOpcode(code);
            JSONObject a=(JSONObject) temp.get(opcode);
            if(a!=null)
            {
                return opcode;
            }
            else
            {
                throw new NullPointerException();
            }
            
        }
        catch(Exception nulleException)
        {
            if(opcode.equals("MUL"))
                System.out.println("fo");
            System.out.println("Wrong opcode "+opcode+" at "+(lc/12+1));
            System.exit(1);
        }
        
        return "";
    }
    static int bintoint(String s)
    {
        int a=0;
        for(int i=0;i<s.length();i++)
        {
            a+=Math.pow(2,i)*(s.charAt(s.length()-i-1)-'0');
        }
        return a;

    } 
    static void updateSymbolTable(JSONObject s,int lc) throws IOException
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");
        Map mapSymbol=((Map)s);
        Iterator<Map.Entry> itr1=mapSymbol.entrySet().iterator();
        while(itr1.hasNext())
        {
            Entry p = itr1.next();
            JSONObject temp=(JSONObject) s.get(p.getKey());
            if(temp.get("type").equals("variable"))
            {
                temp.put("address",binconvert((lc + 12*(int)(temp.get("order")))));
                s.put((String)p.getKey(),temp);
            }
            else if(temp.get("type").equals("label"))
            {
                if(temp.get("address").equals("NULL"))
                    System.out.println("Label "+p.getKey()+" Not defined found at "+ ((bintoint((String)temp.get("address"))/12)+1));
            }
            
        }
        fileWriter.write(s.toString());
        fileWriter.close();
    }
    public static void main(String[] args) throws IOException,ParseException,Exception
    {
        File file = new File("input.txt"); 
        Scanner sc = new Scanner(file);
        FileWriter f2=new FileWriter("temp.txt");
        int lc=0;
        int countvars=0;
        JSONObject availableOpcodes= (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
        JSONObject SymbolTable = new JSONObject();
        //JSONObject errorTable =new JSONObject();
        while(sc.hasNextLine()) 
        {
            //System.out.println(lc);
            String s=sc.nextLine();
            s=LineCommentRemoved(s);
            s=s.strip();
            //System.out.println("in "+s);
            String opcode=checkAndreturnOpcode(availableOpcodes,s,lc);
            //System.out.println(opcode);
            s=removeLabel(SymbolTable, s, lc);
            s=s.strip();
            countvars=removeSymbol(SymbolTable,availableOpcodes,opcode,s, lc,countvars);
            f2.write(s);
            f2.write("\n");
            lc+=12;
       }
    //    System.out.println(SymbolTable.toString());  
        updateSymbolTable(SymbolTable, lc);
        f2.close();
        sc.close();        
    }
}
