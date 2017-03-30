package com.github.s262316.forx.common;

public class NumberRep
{
    public static String translate_alpha_upper(int decimal)
    {
        StringBuilder buffer=new StringBuilder();
        int count;
        int rem;

        count=(decimal / 26);
        while(count >= 0)
        {
            rem=decimal % 26;
            decimal=decimal / 26;
            buffer.append((char)(64 + rem));
            count--;
        }

        return buffer.toString();
    }
}
