
import org.json.simple.*;
import java.io.*;
import java.util.*;
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
    static String binconvert(int x)
    {
        String s="";
        while(x>0)
        {
            s+=x%2;
            x=x/2;
        }
        int len=0;
        String ans=""; 
        while(len<s.length())
        {
            ans+=s.charAt(s.length()-1-len);
            ++len;
        }
        System.out.println(ans);
        return ans;

    }
    static int removeSymbol(JSONObject SymbolTable,JSONObject availableOpcodes,String opcode, String s,int lc,int countvars) throws IOException,Exception
    {
        // typecasting obj to JSONObject
        //System.out.println(s); 
        FileWriter fileWriter = new FileWriter("SymbolTable.json");       
        int indexOfColon=s.indexOf(':');
        try
        {
            //int indexOfColon=s.indexOf(':');
            if(indexOfColon!=-1)
            {
                SymbolTable.put(s.substring(0,indexOfColon),addSymbol(binconvert(lc),"label"));
                int indexOfspace1=s.indexOf(' ');
                int indexOfspace2=s.indexOf(' ',indexOfspace1+1);    
                //System.out.println(s);
                int indexOfspace3=s.indexOf(' ',indexOfspace2+1);
                System.out.println(indexOfspace3);
                if(indexOfspace3!=-1)
                    throw new Exception() ;
                Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
                long noOfOperands= (long)opcodeJSON.get("operands");
                if(noOfOperands==1)
                {
                    //System.out.println("dhajkshdkj");
                    JSONObject a=(JSONObject)SymbolTable.get(s.substring(indexOfspace2+1,indexOfspace3));
                    if(a==null)
                    {
                        if(!((opcode=="BRZ")|(opcode=="BRN")|(opcode=="BRP")))
                        {    
                            SymbolTable.put(s.substring(indexOfspace2+1,indexOfspace3),addSymbol("NULL","variable",countvars));
                            countvars++;
                        }
                        else
                        {
                            SymbolTable.put(s.substring(indexOfspace2+1,indexOfspace3),addSymbol("NULL","label"));

                        }
                    }
                    
                }

            }
            else
            {
                int indexOfspace1=s.indexOf(' ');
                int indexOfspace2=s.indexOf(' ',indexOfspace1+1);
                System.out.println("2 "+indexOfspace2);
                if(indexOfspace2!=-1)
                    throw new Exception();    
                Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
                long noOfOperands= (long)opcodeJSON.get("operands");
                if(noOfOperands==1)
                {
                    JSONObject a=(JSONObject)SymbolTable.get(s.substring(indexOfspace1+1));
                    if(a==null)
                    {
                        if(!((opcode=="BRZ")|(opcode=="BRN")|(opcode=="BRP")))
                        {
                            SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL","variable",countvars));
                            countvars++;    
                        }
                        else
                        {
                            SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL","label"));

                        }
                    }
                }
            }
            fileWriter.write(SymbolTable.toString());
            //input.close();
            fileWriter.close();
            //System.out.println("normal mode");
            return countvars;
        }
        catch(Exception e)
        {
            
            System.out.println("EXCESS OPERANDS AT "+ lc);
           // input.close();
            fileWriter.close();
            //System.out.println("error mode");
        }
        finally
        {
            return countvars;
        }

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

   public static String checkAndreturnOpcode (String code,int lc) throws Exception //reduntat
    {
        String opcode="";
        try
        {
            JSONObject temp= (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
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
            System.out.println("Wrong opcode "+opcode+" at "+lc/12);
        }
        
        return "";
    }
    static JSONObject updateSymbolTable(JSONObject s,int lc)
    {
        Iterator<Map.Entry> itr1=s.entrySet().iterator();
        JSONObject a=new JSONObject();
        while(itr1.hasNext())
        {
            JSONObject temp=(JSONObject) itr1.next();
            if(temp.get("type")=="variable")
            {
                temp.put("address",binconvert(lc + 12*(int)(temp.get("order"))));
                lc+=12;
            }

        }
        return a;

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
            String s=sc.nextLine();
            s=LineCommentRemoved(s);
            s=s.strip();
            //System.out.println("in "+s);
            String opcode=checkAndreturnOpcode(s,lc);
            //System.out.println(opcode);
            countvars=removeSymbol(SymbolTable,availableOpcodes,opcode,s, lc,countvars);
            s=removeLabel(s);
            f2.write(s);
            f2.write("\n");
            lc+=12;
        }  
        f2.close();
        sc.close();        
    }
}
