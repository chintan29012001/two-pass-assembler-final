import java.io.*;
import org.json.*;


class opcodeTableBuilder
{
    static JSONObject valueadd(String opcode,int noOfOperands)  throws FileNotFoundException
    {
        JSONObject obj = new JSONObject();
        obj.put("opcode", opcode);
        obj.put("operands",noOfOperands);
        return obj;
    }   
     public static void main(String[] args) throws IOException {
        FileWriter fileWriter = new FileWriter("availableOpcodes.json"); 
        JSONObject OpcodeTable = new JSONObject();
        String[] AssemblyCode={"CLA","LAC","SAC","ADD","SUB","BRZ","BRN","BRP","INP","DSP","MUL","DIV","STP"};
        String[] Opcodes={"0000","0001","0010","0011","0100","0101","0110","0111","1000","1001","1010","1011","1100"};
        int[] noOfOperands={0,1,1,1,1,1,1,1,1,1,1,2,0};
        for(int i=0;i<AssemblyCode.length;i++)
        {
            OpcodeTable.put(AssemblyCode[i],valueadd(Opcodes[i],noOfOperands[i]));

        }
        //System.out.println(OpcodeTable.toString(4));
        fileWriter.write(OpcodeTable.toString(4));
        fileWriter.close();
        
    }
}