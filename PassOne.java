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

public static void writeTempFile(String opcode, int operands, String line)
{
	try 
	{
        FileWriter writer = new FileWriter("TempFile.txt");
        writer.write(opcode + " " + (String)operands + " " line);
        writer.write("\n");
        writer.close();
    } 
    catch (IOException ex) 
    {
       System.out.println("IO Error");
    }
 
}
