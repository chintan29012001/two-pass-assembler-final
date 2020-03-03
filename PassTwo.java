import org.json.simple.*;
import java.io.*;
import java.util.*;
import org.json.simple.parser.*;

public class PassTwo {

	public static String binconvert(long x)
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
	
	public static void main(String[] args) throws IOException,ParseException{
		// TODO Auto-generated method stub
		
		File file = new File("temp.txt");
		Scanner in = new Scanner(file);
		FileWriter writer = new FileWriter("FinalCode.txt");
		JSONObject json1 = (JSONObject) new JSONParser().parse(new FileReader("SymbolTable.json"));
		JSONObject json2 = (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
		
		String line;
		long location = 0;
		
		while(in.hasNextLine())
		{
			line = in.nextLine();
			String opcode = line.substring(0, 3);
			String address = binconvert(location);
			String code = (String) ((JSONObject) json2.get(opcode)).get("opcode");
			long op = (long) ((JSONObject) json2.get(opcode)).get("operands");
			String opadd = "";
			if(op==1)
			{
				int indexOfspace=line.indexOf(' ');
				opadd = (String) ((JSONObject) json1.get(line.substring(indexOfspace+1))).get("address");
				//System.out.println("oppad "+opadd);
			}
			writer.write(code);
			writer.write(" ");
			if(op==1)
			{
				writer.write(opadd);
				writer.write(" ");
			}
			writer.write("\n");
			System.out.println("opcode "+code + " operand" + opadd);
			location+=12;
		}
		writer.close();
		in.close();
		
	}

}
