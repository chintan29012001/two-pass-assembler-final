package FirstPass;

import org.json.simple.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.json.simple.parser.*;

public class FirstPass
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
        if(type.equals("variable")|type.equals("constant"))
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
    static String removeLabel(JSONObject SymbolTable,JSONObject availableOpcodeTable,String s,int lc) throws IOException,Exception
    {
        FileWriter fileWriter = new FileWriter("SymbolTable.json");       
        int indexOfColon=s.indexOf(':');
        if(indexOfColon!=-1)
        {
            JSONObject a=(JSONObject) SymbolTable.get(s.substring(0,indexOfColon));
            JSONObject b=(JSONObject) availableOpcodeTable.get(s.substring(0,indexOfColon));
            if(a==null&&b==null)
                SymbolTable.put(s.substring(0,indexOfColon).strip(),addSymbol(binconvert(lc),"label"));
            else if(b!=null)
            {
                System.out.println("Opcode "+s.substring(0,indexOfColon)+" used at "+(lc+1));
                System.exit(1);
                
            }
            else
            {
                if(a.get("address").equals("NULL")&&a.get("type").equals("label"))
                {
                    SymbolTable.put(s.substring(0,indexOfColon).strip(),addSymbol(binconvert(lc),"label"));
                }
                else
                {
                    System.out.println(s.substring(0,indexOfColon)+" defined again at "+lc);
                    System.exit(1);
                }
            }
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
            String[] abc=s.split(" ");
            //System.out.println("2 "+indexOfspace2);
            Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
            long noOfOperands= (long)opcodeJSON.get("operands");
            if(noOfOperands==1)
            {
                if(abc.length<2)
                {
                    System.out.println("LESS OPERANDS SUPPLIED AT "+(lc+1));
                    throw new Exception();    
                    
                    
                }
                else if(abc.length>2)
                {
                    System.out.println("EXCESS OPERANDS AT "+(lc+1));
                    throw new Exception();
                    
                }
            }
            else
            {
                if(abc.length>1)
                {
                    System.out.println("MORE OPERANDS SUPPLIED AT "+(lc+1));
                    throw new Exception("MORE OPERANDS SUPPLIED AT "+(lc+1));                   
                     
                }
                else
                {
                    return countvars;
                }
            }
            JSONObject a=null;
            JSONObject b=null;
            if(abc.length>1)
            {
                a=(JSONObject)SymbolTable.get(abc[1]);
                b=(JSONObject)availableOpcodes.get(abc[1]);
            }
            //System.out.println(a);
            //System.out.println(b);
            if(b==null)
            {
                if(a==null)
                {
                    if(opcode.equals("INP"))
                    {
                        SymbolTable.put(abc[1],addSymbol("NULL","variable",countvars));
                        countvars++;    
                    }
                    else if((opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ")))
                    {
                        SymbolTable.put(abc[1],addSymbol("NULL","label"));

                    }
                    else
                    {
                        System.out.println(abc[1]+ "not declared");
                        System.exit(1);
                    }
                }
                else
                {
                    if((opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ")))
                    {
                        if(a.get("type").equals("variable"))
                        {
                            System.out.println("Not a valid operand at "+ (lc+1));
                            System.exit(1);
                        }
                    }
                    else
                    {
                        if(a.get("type").equals("label"))
                        {
                            System.out.println("Not a valid operand at "+ (lc+1));
                            System.exit(1);
                        }
                    }
                    
                }
            }
            else if(b!=null&&abc.length>1)
            {
                System.out.println("Opcode "+abc[1]+" used as operand found at lc: "+(lc+1));
                System.exit(1);
            }
            fileWriter.write(SymbolTable.toString());
            fileWriter.close();
            return countvars;
        }
        catch(Exception e)
        {
            
            
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
                temp.put("address",binconvert((lc + (int)(temp.get("order")))));
                s.put((String)p.getKey(),temp);
            }
            else if(temp.get("type").equals("label"))
            {
                if(temp.get("address").equals("NULL"))
                {
                    System.out.println("Label "+p.getKey()+" Not defined");
                }
            }
            
        }
        fileWriter.write(s.toString());
        fileWriter.close();
    }
    static void updateLiteralTable(JSONObject s,int lc) throws IOException
    {
        FileWriter fileWriter = new FileWriter("LiteralTable.json");
        Map mapSymbol=((Map)s);
        Iterator<Map.Entry> itr1=mapSymbol.entrySet().iterator();
        while(itr1.hasNext())
        {
            Entry p = itr1.next();
            JSONObject temp=(JSONObject) s.get(p.getKey());
            if(temp.get("type").equals("constant"))
            {
                temp.put("address",binconvert((lc + (int)(temp.get("order")))));
                s.put((String)p.getKey(),temp);
            }            
        }
        fileWriter.write(s.toString());
        fileWriter.close();
    }
    static int removeConstant(JSONObject LiteralTable,JSONObject availableOpcodes,String opcode, String s,int lc,int countvars) throws Exception
    {
        FileWriter fileWriter = new FileWriter("LiteralTable.json");       
        try
        {
            String[] abc=s.split(" ");
            //System.out.println("2 "+indexOfspace2);
            Map opcodeJSON = (Map) availableOpcodes.get(opcode); 
            long noOfOperands= (long)opcodeJSON.get("operands");
            int x=0;
            try
            {
                x=Integer.valueOf(abc[1]);
                if(x>127||x<-128)
                {
                    System.out.println("Overflow detected at "+(lc+1));
                    System.exit(1);
                }
            }
            catch(Exception NumberFormatException)
            {
                System.out.println("Invalid way to declare/use a variable at "+(lc+1));
                System.exit(1);

            }
            if(noOfOperands==1)
            {
                if(abc.length<2)
                {
                    System.out.println("LESS OPERANDS SUPPLIED AT "+(lc+1));
                    throw new Exception();    
                    
                    
                }
                else if(abc.length>2)
                {
                    System.out.println("EXCESS OPERANDS AT "+(lc+1));
                    throw new Exception();
                    
                }
            }
            else
            {
                if(abc.length>1)
                {
                    System.out.println("MORE OPERANDS SUPPLIED AT "+(lc+1));
                    throw new Exception("MORE OPERANDS SUPPLIED AT "+(lc+1));                   
                     
                }
                else
                {
                    return countvars;
                }
            }
            JSONObject a=null;
            if(abc.length>1)
            {
                a=(JSONObject)LiteralTable.get(abc[1]);
                //b=(JSONObject)availableOpcodes.get(abc[1]);
            }
            //System.out.println(a);
            //System.out.println(b);
            if(a==null)
                {
                    if(opcode.equals("INP")|opcode.equals("BRP")|opcode.equals("BRN")|opcode.equals("BRZ")|opcode.equals("SAC"))
                    {
                        System.out.println("Constants cannot be used with the opcode "+opcode+" at "+(lc+1));

                    }
                    else
                    {
                        if(opcode.equals("DIV"))
                        {
                            if(x==0)
                            {
                                System.out.println("Division by zero at "+(lc+1));
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
            
            
           // input.close();
           fileWriter.close();
            System.exit(1);
            //System.out.println("error mode");
        }
        
        return countvars;

    }

    static int checkConstantOrVariable(JSONObject LiteralTable,JSONObject SymbolTable,JSONObject availableOpcodes,String opcode, String s,int lc,int countvars) throws Exception
    {
        if(s.length()>4)
        {
            if(Character.isDigit(s.charAt(4)))
            {
                return removeConstant(LiteralTable, availableOpcodes, opcode, s, lc, countvars);
            }
            else
            {
                return removeSymbol(SymbolTable, availableOpcodes, opcode, s, lc, countvars);
            }
        }
        else
            return countvars;

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
        JSONObject LiteralTable =new JSONObject();
        int flg=0;
        while(sc.hasNextLine()) 
        {
            //System.out.println(lc);
            String s=sc.nextLine();
            s=LineCommentRemoved(s);
            s=s.strip();
            //System.out.println("in "+s);
            String opcode=checkAndreturnOpcode(availableOpcodes,s,lc);
            //System.out.println(opcode);
            s=removeLabel(SymbolTable,availableOpcodes,s, lc);
            s=s.strip();
            countvars=checkConstantOrVariable(LiteralTable,SymbolTable,availableOpcodes,opcode,s, lc,countvars);
            f2.write(s);
            f2.write("\n");
            lc+=1;
            if(opcode.equals("STP"))
            {
                flg=1;
                break;
            }
       }
    //    System.out.println(SymbolTable.toString());  
    if(flg==0)
        {
            System.out.println("STP not found");
            System.exit(1);
        }    
    updateSymbolTable(SymbolTable, lc);
    updateLiteralTable(LiteralTable, lc);
    f2.close();
    sc.close();        
    }
}
