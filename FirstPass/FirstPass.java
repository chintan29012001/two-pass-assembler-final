package FirstPass;

import org.json.simple.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.json.simple.parser.*;

public class FirstPass
{ 

    public static boolean checkORG(String line) //To check for START instruction
    {
	    if((line.strip()) == "START")
		    return true;
	    else
	    {
		    System.out.println("Start of Program Not Found");
		    return false;
	    }
    }
    
    static String LineCommentRemoved(String s) //To remove Comments from the instruction, if any
    {
        int indexComment = s.indexOf("//");
        
        if(indexComment==-1)
            return s;
        else
            return s.substring(0, indexComment);
    }
    
    static JSONObject addSymbol(String address, String type, int c) //To enter an operand symbol in the Symbol Table
    {
        JSONObject obj=new JSONObject();
        
        obj.put("address",address);
        obj.put("type",type);
        
        if(type.equals("variable")|type.equals("constant"))
            obj.put("order",c);
        
        return obj;
    }
    
    static JSONObject addSymbol(String address, String type) //To enter a label symbol in the Symbol Table
    {
        JSONObject obj=new JSONObject();
        
        obj.put("address",address);
        obj.put("type",type);
        
        return obj;
    }
    
    static String binconvert(long x) //To convert a number to its Binary equivalent
    {
        String s="";
        
        while(((int)x)>0)
        {
            s+=Long.toString(x%2);
            x=x/2;
        }
        
        if(s.length()>8)
        {
            System.out.println("Address Out of Bounds [Overflow]"); //Addressing not allowed [Error]
            System.exit(1);
        }
        
        for(int i=0;s.length()<8;i++)
            s+="0";
        
        int len=0;
        String ans=""; 
        
        while(len<s.length())
        {
            ans+=s.charAt(s.length()-1-len);
            ++len;
        }
        
        return ans;

    }
    
    static String removeLabel(JSONObject SymbolTable, JSONObject availableOpcodeTable, String s, int lc) throws IOException,Exception //To remove Label from the instruction, if any, and enter it in the Symbol Table
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");       
        
        int indexOfColon=s.indexOf(':');
        
        if(indexOfColon!=-1) //Label found
        {
            JSONObject a = (JSONObject) SymbolTable.get(s.substring(0,indexOfColon));
            JSONObject b = (JSONObject) availableOpcodeTable.get(s.substring(0,indexOfColon));
            
            if(a==null && b==null) //Valid label
                SymbolTable.put(s.substring(0,indexOfColon).strip(),addSymbol(binconvert(lc),"label"));
            
            else if(b!=null) //Invalid label
            {
                System.out.println("Invalid Label at " + (lc+1)); //Invalid label name [Error]
                System.exit(1);
            }
            
            else //Label already present in Symbol Table
            {
                if(a.get("address").equals("NULL") && a.get("type").equals("label")) //Forward Referencing Case
                {
                    SymbolTable.put(s.substring(0,indexOfColon).strip(),addSymbol(binconvert(lc),"label"));
                }
                
                else //Multiple Declarations
                {
                    System.out.println("Multiple Declarations: " + s.substring(0,indexOfColon) + " defined again at " + (lc+1)); //Multiple declarations of a symbol [Error]
                    System.exit(1);
                }
            }
            
            s = removeLabel(s);
        }
        
        fileWriter.close();     
        
        return s;

    }
    
    static int removeSymbol(JSONObject SymbolTable, JSONObject availableOpcodes, String opcode, String s, int lc, int countvars) throws IOException,Exception //To remove Operand symbol from the instruction, if any, and enter it in the Symbol Table
    {
        
    	FileWriter fileWriter = new FileWriter("SymbolTable.json");       
        
    	try
        {
            String[] abc = s.split(" ");
            
            Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
            long noOfOperands = (long)opcodeJSON.get("operands");
            
            if(noOfOperands==1)
            {
                if(abc.length<2)
                {
                    System.out.println("Less Operands supplied at " + (lc+1)); //Operands less than required [Error]
                    throw new Exception();    
                    
                    
                }
                
                else if(abc.length>2)
                {
                    System.out.println("Excess Operands supplied at " + (lc+1)); //Operands more than required [Error]
                    throw new Exception();
                    
                }
            }
            
            else
            {
                if(abc.length>1)
                {
                    System.out.println("Excess Operands supplied at " + (lc+1)); //Operands more than required [Error]
                    throw new Exception();                   
                     
                }
                
                else
                {
                    return countvars;
                }
            }
            
            JSONObject a = null;
            JSONObject b = null;
            
            if(abc.length>1)
            {
                a = (JSONObject)SymbolTable.get(abc[1]);
                b = (JSONObject)availableOpcodes.get(abc[1]);
            }
            
            if(b==null) //Valid Operand Symbol
            {
                if(a==null)
                {
                    if(opcode.equals("INP")) //Symbol declaration
                    {
                        SymbolTable.put(abc[1],addSymbol("NULL","variable",countvars));
                        countvars++;    
                    }
                    
                    else if((opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ"))) //Branch to Symbol
                    {
                        SymbolTable.put(abc[1],addSymbol("NULL","label"));
                    }
                    
                    else //Declaration Error
                    {
                        System.out.println(abc[1] + " Not Declared"); //Declaration Error [Error]
                        System.exit(1);
                    }
                }
                
                else
                {
                    if((opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ")))
                    {
                        if(a.get("type").equals("variable"))
                        {
                            System.out.println("Invalid Operand at " + (lc+1)); //Invalid operand [Error]
                            System.exit(1);
                        }
                    }
                    
                    else
                    {
                        if(a.get("type").equals("label"))
                        {
                            System.out.println("Invalid Operand at " + (lc+1)); //Invalid operand [Error]
                            System.exit(1);
                        }
                    }
                }
            }
            
            else if(b!=null && abc.length>1)
            {
                System.out.println("Invalid Operand: Opcode " + abc[1] + " used as operand at " + (lc+1)); //Invalid operand [Error]
                System.exit(1);
            }
            
            fileWriter.write(SymbolTable.toString());
            
            fileWriter.close();
            
            return countvars;
        }
    	
        catch(Exception e)
        {
        	fileWriter.close();
            System.exit(1);
        }
        
        return countvars;

    }
    
    public static String removeLabel(String line) //To remove Label from the instruction, if any
    {
        int i = line.indexOf(":");
        
        if(i==-1)
            return line;
        
        else
            return line.substring(i+2);
    }
    
    public static String extractOpcode(String line) //To extract Opcode from the instruction
    {
        return ((removeLabel(line)).substring(0,3));	
    }

    public static String checkAndreturnOpcode (JSONObject temp, String code, int lc) throws Exception //To check validity of Opcode
    {
        String opcode = "";
        try
        {
            opcode = extractOpcode(code);
            JSONObject a = (JSONObject) temp.get(opcode);
            
            if(a!=null)
            {
                return opcode;
            }
            
            else
            {
                throw new NullPointerException();
            }
            
        }
        
        catch(Exception nullException)
        {
            System.out.println("Invalid Opcode " + opcode + " at " + (lc+1)); //Invalid opcode [Error]
            System.exit(1);
        }
        
        return "";
    }
    
    static int bintoint(String s) //To convert a Binary number to its Decimal equivalent
    {
        int a=0;
        
        for(int i=0;i<s.length();i++)
        {
            a+=Math.pow(2,i)*(s.charAt(s.length()-i-1)-'0');
        }
        
        return a;
    }
    
    static void updateSymbolTable(JSONObject s,int lc) throws IOException //To allocate addresses to operand symbols
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");
        Map mapSymbol=((Map)s);
        Iterator<Map.Entry> itr1 = mapSymbol.entrySet().iterator();
        
        while(itr1.hasNext())
        {
            Entry p = itr1.next();
            
            JSONObject temp=(JSONObject) s.get(p.getKey());
            
            if(temp.get("type").equals("variable"))
            {
                temp.put("address",binconvert((lc + (int)(temp.get("order")))));
                s.put((String)p.getKey(),temp);
            }
            
            else if(temp.get("type").equals("label"))
            {
                if(temp.get("address").equals("NULL"))
                {
                    System.out.println("Label " + p.getKey() + " Not Defined"); //Undefined Label [Error]
                    System.exit(1);
                }
            }
        }
        
        fileWriter.write(s.toString());
        fileWriter.close();
    }
    
    static void updateLiteralTable(JSONObject s,int lc) throws IOException //To allocate addresses to Literals
    {
        FileWriter fileWriter = new FileWriter("LiteralTable.json");
        Map mapSymbol=((Map)s);
        Iterator<Map.Entry> itr1 = mapSymbol.entrySet().iterator();
        
        while(itr1.hasNext())
        {
            Entry p = itr1.next();
            
            JSONObject temp = (JSONObject) s.get(p.getKey());
            
            if(temp.get("type").equals("constant"))
            {
                temp.put("address",binconvert((lc + (int)(temp.get("order")))));
                s.put((String)p.getKey(),temp);
            }            
        }
        
        fileWriter.write(s.toString());
        fileWriter.close();
    }
    
    static int removeConstant(JSONObject LiteralTable,JSONObject availableOpcodes,String opcode, String s,int lc,int countvars) throws Exception //To remove Literals, if any, from the instruction and enter them in the Literal Table
    {
        FileWriter fileWriter = new FileWriter("LiteralTable.json");       
        
        try
        {
            String[] abc = s.split(" ");
            
            Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
            long noOfOperands= (long)opcodeJSON.get("operands");
            int x=0;
            
            try
            {
                x = Integer.valueOf(abc[1]);
                
                if(x>127||x<-128)
                {
                    System.out.println("Overflow detected at " + (lc+1)); //Overflow Error [Error]
                    System.exit(1);
                }
            }
            
            catch(Exception NumberFormatException)
            {
                System.out.println("Invalid way to declare/use a variable at " + (lc+1)); //Declaration format invalid [Error]
                System.exit(1);

            }
            
            if(noOfOperands==1)
            {
                if(abc.length<2)
                {
                    System.out.println("Less Operands supplied at " + (lc+1)); //Operands less than required [Error]
                    throw new Exception();
                }
                
                else if(abc.length>2)
                {
                    System.out.println("Excess Operands supplied at " + (lc+1)); //Operands more than required [Error]
                    throw new Exception();    
                }
            }
            
            else
            {
                if(abc.length>1)
                {
                    System.out.println("Excess Operands supplied at " + (lc+1)); //Operands more than required [Error]
                    throw new Exception();     
                }
                
                else
                {
                    return countvars;
                }
            }
            
            JSONObject a = null;
            
            if(abc.length>1)
            {
                a = (JSONObject)LiteralTable.get(abc[1]);
            }
            
            if(a==null)
            {
            	if(opcode.equals("INP")|opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ")|opcode.equals("SAC"))
                {
            		System.out.println("Literals cannot be used with opcode " + opcode + " at " + (lc+1)); //Invalid format [Error]
            		System.exit(1);
                }
                
            	else
            	{
            		if(opcode.equals("DIV"))
            		{
            			if(x==0)
            			{
            				System.out.println("Zero Division Error at " + (lc+1)); //Zero Division Error [Error]
            				System.exit(1);
                        }
            		}
                        
            		LiteralTable.put(abc[1],addSymbol("NULL","constant",countvars));
            		countvars++;                         
            	}
            }
            
            fileWriter.write(LiteralTable.toString());
            fileWriter.close();
            
            return countvars;
        }
        
        catch(Exception e)
        {
        	fileWriter.close();
            System.exit(1);
        }
        
        return countvars;
    }

    static int checkConstantOrVariable(JSONObject LiteralTable, JSONObject SymbolTable, JSONObject availableOpcodes, String opcode, String s, int lc, int countvars) throws Exception //To check if the operand is a Symbol (Variable) or a Literal (Constant)
    {
        Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
        long noOfOperands= (long)opcodeJSON.get("operands");
        
        if(s.length()>=4)
        {
            if(Character.isDigit(s.charAt(4))||s.charAt(4)=='-')
            {
                return removeConstant(LiteralTable, availableOpcodes, opcode, s, lc, countvars);
            }
            
            else
            {
                return removeSymbol(SymbolTable, availableOpcodes, opcode, s, lc, countvars);
            }
        }
        
        else
        {
            if(noOfOperands==1)
            {
                System.out.println("Less Operands supplied at " + (lc+1)); //Operands less than required
                System.exit(1);
            }
            
            return countvars;
        }
    }
    
    public static void main(String[] args) throws IOException,ParseException,Exception //Main Function
    {
        File file = new File("input.txt"); 
        Scanner sc = new Scanner(file);
        FileWriter f2=new FileWriter("temp.txt");
        
        int lc=0;
        int countvars=0;
        
        JSONObject availableOpcodes= (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
        JSONObject SymbolTable = new JSONObject();
        JSONObject LiteralTable =new JSONObject();
        
        int flg=0;
        
        while(sc.hasNextLine()) 
        {
            String s = sc.nextLine();
            
            s = LineCommentRemoved(s);
            s = s.strip();
            
            if(s.length()<2) //Blank statements and statements with only comments handled
                continue;
            
            String opcode = checkAndreturnOpcode(availableOpcodes, s, lc);
            
            s = removeLabel(SymbolTable, availableOpcodes, s, lc);
            s = s.strip();
            
            countvars = checkConstantOrVariable(LiteralTable, SymbolTable, availableOpcodes, opcode, s, lc, countvars);
            
            f2.write(s);
            f2.write("\n");
            
            lc+=1;
            
            if(opcode.equals("STP")) //Check for End of Program
            {
                flg=1;
                break;
            }
       }
      
    if(flg==0)
        {
            System.out.println("End of Program Not Found"); //STP not found [Error]
            System.exit(1);
        }
    
    updateSymbolTable(SymbolTable, lc);
    updateLiteralTable(LiteralTable, lc);
    
    System.out.println();
    System.out.println("Pass One has been executed successfully.");
    System.out.println();
    
    f2.close();
    sc.close();        
    
    }
}
