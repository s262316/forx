package com.github.s262316.forx.css;

import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CssListsTest
{
    CssLists lists=new CssLists();

    @Test
    public void disc()
    {
        Value v=new ValueBuilder().identifier("disc").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void circle()
    {
        Value v=new ValueBuilder().identifier("circle").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void square()
    {
        Value v=new ValueBuilder().identifier("square").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void decimal()
    {
        Value v=new ValueBuilder().identifier("decimal").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void decimalLeadingZero()
    {
        Value v=new ValueBuilder().identifier("decimal-leading-zero").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void lowerRoman()
    {
        Value v=new ValueBuilder().identifier("lower-roman").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void upperRoman()
    {
        Value v=new ValueBuilder().identifier("upper-roman").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void lowerGreek()
    {
        Value v=new ValueBuilder().identifier("lower-greek").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void lowerLatin()
    {
        Value v=new ValueBuilder().identifier("lower-latin").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void upperLatin()
    {
        Value v=new ValueBuilder().identifier("upper-latin").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void armenian()
    {
        Value v=new ValueBuilder().identifier("armenian").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void georgian()
    {
        Value v=new ValueBuilder().identifier("georgian").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void lowerAlpha()
    {
        Value v=new ValueBuilder().identifier("lower-alpha").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void upperAlpha()
    {
        Value v=new ValueBuilder().identifier("upper-alpha").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void none()
    {
        Value v=new ValueBuilder().identifier("none").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void inherit()
    {
        Value v=new ValueBuilder().identifier("inherit").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void invalid()
    {
        Value v=new ValueBuilder().identifier("dfsgfdsgfdsg").build();
        boolean result=lists.validateListStyleProperty(v);
        Assert.assertFalse(result);
    }
}
