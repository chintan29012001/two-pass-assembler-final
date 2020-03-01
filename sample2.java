class sample2
{
    public static void main(String[] args) {
        String x="x 12 x bc' ' x";
        int i1,i2,i3;
        i1=x.indexOf('x');
        i2=x.indexOf('x',i1+1);
        i3=x.indexOf('x',i2+1);
        System.out.println(i1+" "+i2+" "+i3);
    }
}