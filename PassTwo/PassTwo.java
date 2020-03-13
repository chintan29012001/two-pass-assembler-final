package PassTwo;

import org.json.simple.*;
import java.io.*;
import java.util.*;
import org.json.simple.parser.*;

public class PassTwo {

	public static void main(String[] args) throws IOException,ParseException //Main Function
	{
		// TODO Auto-generated method stub
		
		File file = new File("temp.txt");
		Scanner in = new Scanner(file);
		FileWriter writer = new FileWriter("FinalCode.txt");
		
		JSONObject json1 = (JSONObject) new JSONParser().parse(new FileReader("SymbolTable.json"));
		JSONObject json2 = (JSONObject) new JSONParser().parse(new FileReader("availableOpcodes.json"));
		JSONObject json3 = (JSONObject) new JSONParser().parse(new FileReader("LiteralTable.json"));
		
		String line;
		
	    System.out.println();
	    System.out.println("Pass Two has been executed successfully.");
	    System.out.println();
		System.out.println("Translated Machine Code: ");
		System.out.println();
		
		while(in.hasNextLine())
		{
			line = in.nextLine();
			
			String opcode = line.substring(0,3);
			
			String code = (String) ((JSONObject) json2.get(opcode)).get("opcode"); //Extracts Binary Opcode
			
			long op = (long) ((JSONObject) json2.get(opcode)).get("operands");
			String opadd = "";
			
			if(op==1)
			{
				int indexOfspace = line.indexOf(' ');
				
				//Extracts Operand Address, if any
				
				if(Character.isDigit(line.substring(indexOfspace+1).charAt(0))||line.substring(indexOfspace+1).charAt(0)=='-') //Constant Operand
					opadd = (String) ((JSONObject) json3.get(line.substring(indexOfspace+1))).get("address");
				
				else //Variable Operand
					opadd = (String) ((JSONObject) json1.get(line.substring(indexOfspace+1))).get("address");
				
			}
			
			writer.write(code);
			writer.write(" ");
			
			if(op==1)
			{
				writer.write(opadd);
				writer.write(" ");
			}
			
			writer.write("\n");
			
			System.out.println(code + " " + opadd); //Prints output onto the console
		}
		
		System.out.println();
		
		writer.close();
		in.close();		
	}
}
