import java.io.*;
import java.util.*;
import org.json.*;

class FirstPass
{
    static String LineCommentRemoved(String s)
    {
        int indexComment=s.indexOf("\\");
        if(indexComment==-1)
            return s;
        else
            return s.substring(0, indexComment);
    }
    static void removeSymbol() throws IOException
    {
        InputStream input = new FileInputStream("SymbolTable.json");
        try
        {
            JSONObject json = new JSONObject(new JSONTokener(credentialsStream));

        }
        catch(IOException | JSONException ex)
        {
            //do nothing
        }
        

    }

    public static void main(String[] args) throws IOException
    {
        File file = new File("input.txt"); 
        Scanner sc = new Scanner(file);
        int lc=0;
        while(sc.hasNextLine()) 
        {
            String s=sc.nextLine();
            s=LineCommentRemoved(s);
            
            lc++;
        }
        sc.close();        
    }
}