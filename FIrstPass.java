
import org.json.simple.*;
import java.io.*;
import java.util.*;
import org.json.simple.parser.*;

class FirstPass
{
    static String LineCommentRemoved(String s)
    {
        int indexComment=s.indexOf("//");
        if(indexComment==-1)
            return s;
        else
            return s.substring(0, indexComment);
    }
    static JSONObject addSymbol(String address)
    {
        JSONObject obj=new JSONObject();
        obj.put("address",address);
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
    static void removeSymbol(JSONObject SymbolTable,JSONObject availableOpcodes,String opcode, String s,int lc) throws IOException,Exception
    {
         
          
        // typecasting obj to JSONObject 
        FileWriter fileWriter = new FileWriter("SymbolTable.json");
        int indexOfColon=s.indexOf(':');
        try
        {
            //int indexOfColon=s.indexOf(':');
            if(indexOfColon!=-1)
            {
                SymbolTable.put(s.substring(0,indexOfColon),addSymbol(binconvert(lc)));
                int indexOfspace1=s.indexOf(' ');
                int indexOfspace2=s.indexOf(' ',indexOfspace1+1);    
                int indexOfspace3=s.indexOf(' ',indexOfspace2+1);
                if(indexOfspace3!=-1)
                    throw new Exception() ;
                Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
                long noOfOperands= (long)opcodeJSON.get("operands");
                if(noOfOperands==1)
                {
                    //System.out.println("dhajkshdkj");
                    SymbolTable.put(s.substring(indexOfspace2+1,indexOfspace3),addSymbol("NULL"));
                }
                
            }
            else
            {
                int indexOfspace1=s.indexOf(' ');
                int indexOfspace2=s.indexOf(' ',indexOfspace1+1);
                if(indexOfspace2!=-1)
                    throw new Exception() ;    
                Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
                long noOfOperands= (long)opcodeJSON.get("operands");
                if(noOfOperands==1)
                {
                    SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL"));
                }
            }
            fileWriter.write(SymbolTable.toString());
            //input.close();
            fileWriter.close();
            //System.out.println("normal mode");
        }
        catch(Exception e)
        {
            
            System.out.println("EXCESS OPERANDS AT "+ lc);
           // input.close();
            fileWriter.close();
            //System.out.println("error mode");
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
    public static void main(String[] args) throws IOException,ParseException,Exception
    {
        File file = new File("input.txt"); 
        Scanner sc = new Scanner(file);
        int lc=0;
        JSONObject availableOpcodes= (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
        JSONObject SymbolTable = new JSONObject();
        //JSONObject errorTable =new JSONObject();
        while(sc.hasNextLine()) 
        {
            String s=sc.nextLine();
            s=LineCommentRemoved(s);
            s=s.strip();
            String opcode=checkAndreturnOpcode(s,lc);
            removeSymbol(SymbolTable,availableOpcodes,opcode,s, lc);
            lc+=12;
        }
        sc.close();        
    }
}
