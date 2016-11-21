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
            buffer.append(16 + '1' + rem);
            count--;
        }

        return buffer.toString();
    }

    public static String translate_alpha_lower(int decimal)
    {
        StringBuilder buffer=new StringBuilder();
        int count;
        int rem;

        count=(decimal - 1) / 26;
        while(count >= 0)
        {
            rem=(decimal - 1) % 26;
            decimal=decimal / 26;
            buffer.insert(0, 48 + '1' + rem);
            count--;
        }

        return buffer.toString();
    }
}
