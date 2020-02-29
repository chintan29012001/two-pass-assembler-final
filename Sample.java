class Sample
{
    public static void main(String[] args) {
        String s="CLA 90 //abca";
        int x=s.indexOf("//");
        if(x==-1)
            System.out.println(s);
        else
            System.out.println(s.substring(0, x));
    }
}