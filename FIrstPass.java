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
    static void removeSymbol(JSONObject SymbolTable,JSONObject availableOpcodes,String opcode, String s,int lc) throws IOException
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
        catch(IOException e)
        {
            
            
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

    public static int searchOpcode (String code)
    {
        JSONParser temp = new JSONParser();

        try
        {
            FileReader file = new FileReader("availableOpcodes.json");
            JSONObject json = (JSONObject) temp.parse(file);
            return ((int)(((JSONObject) json.get(code)).get(operands)));
        }
        catch (Exception ex) 
        {
            System.out.println("Invalid Opcode");
        }
    }

    public static boolean checkOperands(String line, String opcode, int operands)
    {
        switch(operands)
        {
            case 0: if(line.charAt(line.indexOf(opcode) + 3) == "\n")
                        return true;
                    else
                    {
                        System.out.println("Invalid Operands: More Than Required");
                        return false;
                    }

            case 1: if(line.charAt(line.indexOf(opcode) + 3) == "\n")
                    {
                        System.out.println("Invalid Operands: Less Than Required");
                        return false;
                    }
                    else if(line.indexOf(",")==-1)
                        return true;
                    else
                    {
                        System.out.println("Invalid Operands: More Than Required");
                        return false;
                    }

            case 2: if(line.indexOf(",")==-1)
                    {
                        System.out.println("Invalid Operands: Less Than Required");
                        return false;
                    }
                    else if(line.indexOf(",",line.indexOf(",")+1)==-1)
                        return true;
                    else
                    {
                        System.out.println("Invalid Operands: More Than Required");
                        return false;
                    }
        }
    }

    public static void main(String[] args) throws IOException,ParseException
    {
        File file = new File("input.txt"); 
        Scanner sc = new Scanner(file);
        int lc=0;
        JSONObject availableOpcodes= (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
        JSONObject SymbolTable = new JSONObject();
        JSONObject errorTable =new JSONObject();
        while(sc.hasNextLine()) 
        {
            String s=sc.nextLine();
            String opcode="";
            s=LineCommentRemoved(s);
            removeSymbol(SymbolTable,availableOpcodes,opcode,s, lc);
            lc++;
        }
        sc.close();        
    }
}
