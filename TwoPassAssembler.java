import FirstPass.*;
import PassTwo.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

class TwoPassAssembler
{
    public static void main(String[] args) throws IOException,ParseException,Exception
    {
        
    	FirstPass f = new FirstPass();
        PassTwo s = new PassTwo();
        Scanner sc = new Scanner(System.in);
        
        int option = 1;
        
        while(option!=0)
        {
        	//Displaying Menu
        	
            System.out.println("Welcome to Two Pass Assembler");
            System.out.println("Press 0 to Exit");
            System.out.println("Press 1 to Intitiate First Pass");
            System.out.println("Press 2 to Intitiate Second Pass");
            
            option = sc.nextInt();
            
            switch(option)
            {
                case 1: f.main(args); //Pass One executed
                		break;
                
                case 2: s.main(args); //Pass Two executed
                		break;
                
                case 0: System.exit(0); //Exit
            }
        }
    }
}