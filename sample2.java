import java.util.Iterator;
import java.util.Map.*;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
class sample2
{
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
    static int bintoint(String s)
    {
        int a=0;
        for(int i=0;i<s.length();i++)
        {
            a+=Math.pow(2,i)*(s.charAt(s.length()-i-1)-'0');
        }
        return a;
    }
    public static void main(String[] args) throws ParseException{
    System.out.println(bintoint("111"));
    }
}