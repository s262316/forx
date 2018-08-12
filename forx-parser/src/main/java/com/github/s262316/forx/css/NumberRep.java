package com.github.s262316.forx.css;

/*
1=A 1%27 = 1
2=B 1%27 = 2
..
25=Y 25%27=25
26=Z 26%27=26
27=AA, 27%27=0, 1%27=1
 */
public class NumberRep
{
    public static String toAlphabeticalCount(int decimal)
    {
        StringBuilder buffer=new StringBuilder();
        int rem;
        String negative="";

        if(decimal<0)
        {
            negative = "-";
            decimal = Math.abs(decimal);
        }

        while(decimal > 0)
        {
            rem = (decimal-1) % 26;
            decimal = (decimal-1) / 26;
            buffer.insert(0, (char) (65 + rem));
        }

        return negative+buffer.toString();
    }
}
