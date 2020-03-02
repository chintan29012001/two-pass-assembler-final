import org.json.simple.*;
import java.io.*;
//import org.json.*;
import java.util.Map;
import org.json.simple.parser.*;

class Sample
{
    static String LineCommentRemoved(String s)
    {
        int indexComment=s.indexOf("\\");
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
        while(x>0||8-s.length()>0)
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
        //System.out.println(ans);

        return ans;

    }
    static void removeSymbol(JSONObject SymbolTable,JSONObject availableOpcodes,String opcode, String s,int lc) throws IOException
    {
         
          
        // typecasting obj to JSONObject 
        FileWriter fileWriter = new FileWriter("SymbolTable.json");
        int indexOfColon=s.indexOf(':');
        int indexOfQuotation1=s.indexOf("'");
        int indexOfQuotation2=s.indexOf("'",indexOfQuotation1+1);
        try
        {
            //int indexOfColon=s.indexOf(':');
            if(indexOfColon!=-1)
            {
                SymbolTable.put(s.substring(0,indexOfColon),addSymbol(binconvert(lc)));
                int indexOfspace1=s.indexOf(' ');
                int indexOfspace2=s.indexOf(' ',indexOfspace1+1);    
                Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
                long noOfOperands= (long)opcodeJSON.get("operands");
                if(noOfOperands==1&&indexOfQuotation1==-1&&indexOfQuotation2==-1)
                {
                    //System.out.println("dhajkshdkj");
                    
                    SymbolTable.put(s.substring(indexOfspace2+1),addSymbol("NULL"));
                }
                else if(noOfOperands==2&&indexOfQuotation1==-1&&indexOfQuotation2==-1)
                {
                    SymbolTable.put(s.substring(indexOfspace2+1,indexOfspace2),addSymbol("NULL"));
                    indexOfspace1=s.indexOf(' ',indexOfspace2+1);
                    SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL"));
                }
            }
            else
            {
                int indexOfspace1=s.indexOf(' ');
                int indexOfspace2=s.indexOf(' ',indexOfspace1+1);    
                Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
                long noOfOperands= (long)opcodeJSON.get("operands");
                if(noOfOperands==1&&indexOfQuotation1==-1&&indexOfQuotation2==-1)
                {
                    SymbolTable.put(s.substring(indexOfspace1+1),addSymbol("NULL"));
                }
                else if(noOfOperands==2&&indexOfQuotation1==-1&&indexOfQuotation2==-1)
                {
                    indexOfspace1=s.indexOf(' ',indexOfspace2+1);
                    SymbolTable.put(s.substring(indexOfspace2+1,indexOfspace1),addSymbol("NULL"));
                    SymbolTable.put(s.substring(indexOfspace1),addSymbol("NULL"));
                }
                
            }
            fileWriter.write(SymbolTable.toString());
            //input.close();
            fileWriter.close();
            System.out.println("normal mode");
        }
        catch(IOException e)
        {
            
            
           // input.close();
            fileWriter.close();
            //System.out.println("error mode");
        }
        

    }
    static void updateAddress(JSONObject s,String Symbol,int address) throws IOException
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");
        JSONObject abc=(JSONObject)s.get(Symbol);
        abc.put("address",binconvert(address));
        s.put(Symbol,abc);
        fileWriter.write(s.toString());
        //input.close();
        fileWriter.close();

    }
    public static void main(String[] args) throws IOException,ParseException {
        String s="flg: CLA";
        JSONObject availableOpcodes= (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
        JSONObject SymbolTable = new JSONObject();
        int lc=0;
        String opcode="SUB";
        s="SUB X";
        //System.out.println(binconvert(lc));
        removeSymbol(SymbolTable,availableOpcodes,opcode,s, lc);
        opcode="ADD";
        s="flg2: ADD X\\abc";
        s=LineCommentRemoved(s);
        lc++;
        removeSymbol(SymbolTable,availableOpcodes,opcode,s, lc);
        lc++;
        updateAddress(SymbolTable,"X",lc);
    }
}
