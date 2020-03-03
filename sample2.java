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
    public static void main(String[] args) throws ParseException{
    String s="{\"flg\":{\"address\":\"00001100\",\"type\":\"label\"},\"x\":{\"address\":\"NULL\",\"type\":\"variable\",\"order\":0}}";
    JSONParser parser = new JSONParser();
    JSONObject xyz = (JSONObject) parser.parse(s);
    Map json=((Map)xyz);   
    Iterator<Map.Entry> itr1=json.entrySet().iterator();
    int lc=12;
    while(itr1.hasNext())
    {
        Entry p = itr1.next();
        JSONObject temp=(JSONObject) xyz.get(p.getKey());
        if(temp.get("type").equals("variable"))
            {
                temp.put("address",binconvert(lc + 12*(Long)(temp.get("order"))));
            }
        xyz.put((String)p.getKey(),temp);
    }
    System.out.println(json.toString());
    }
}