import FirstPass.*;
import PassTwo.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
class TwoPassAssembler
{
    public static void main(String[] args) throws IOException,ParseException,Exception{
        FirstPass f=new FirstPass();
        PassTwo s=new PassTwo();
        Scanner sc=new Scanner(System.in);
        int option=1;
        while(option!=0)
        {
            System.out.println("Welcome to Two Pass Assembler");
            System.out.println("Press 0 to exit");
            System.out.println("Press 1 to intitiate first pass");
            System.out.println("Press 2 to intitiate second pass:");
            option=sc.nextInt();
            switch(option)
            {
                case 1:f.main(args);break;
                case 2:s.main(args);break;
                case 0: System.exit(0);
            }
        }
    }
}