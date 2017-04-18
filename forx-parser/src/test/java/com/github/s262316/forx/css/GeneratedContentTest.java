package com.github.s262316.forx.css;

import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeneratedContentTest
{
    @Mock
    CssLists mockLists;
    @InjectMocks
    GeneratedContent content=new GeneratedContent();

    @Test
    public void normal()
    {
        Value v=new ValueBuilder().identifier("normal").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void none()
    {
        Value v=new ValueBuilder().identifier("none").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void string()
    {
        Value v=new ValueBuilder().string("str").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void uri() throws Exception
    {
        Value v=new ValueBuilder().uri("http://uri").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void counter()
    {
        Value v=new ValueBuilder().counter("countername").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void counterWithValidListStyle()
    {
        Mockito.when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("lower-roman").build())).thenReturn(true);

        Value v=new ValueBuilder().counter("countername", "lower-roman").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void counterWithInvalidListStyle()
    {
        Mockito.when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("aaaa").build())).thenReturn(false);

        Value v=new ValueBuilder().counter("countername", "aaaa").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void counterWithWrongNumberOfArgs()
    {
        Mockito.when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("aaaa").build())).thenReturn(false);

        Value invalidArg1=new ValueBuilder().identifier("invalid").build();
        Value invalidArg2=new ValueBuilder().identifier("invalid").build();
        Value invalidArg3=new ValueBuilder().identifier("invalid").build();

        Value v=new ValueBuilder().function("counter", invalidArg1, invalidArg2, invalidArg3).build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void counters()
    {
        Value v=new ValueBuilder().counters("countername", ".").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void countersWithValidListStyle()
    {
        Mockito.when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("lower-roman").build())).thenReturn(true);

        Value v=new ValueBuilder().counters("countername", ".", "lower-roman").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void countersWithInvalidListStyle()
    {
        Mockito.when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("aaaa").build())).thenReturn(false);

        Value v=new ValueBuilder().counters("countername", ".", "aaaa").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void countersWithWrongNumberOfArgs()
    {
        Value invalidArg1=new ValueBuilder().identifier("invalid").build();

        Value v=new ValueBuilder().function("counters", invalidArg1).build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void countersWithInvalidSeparator()
    {
        Mockito.when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("aaaa").build())).thenReturn(false);

        Value invalidArg1=new ValueBuilder().identifier("invalid").build();
        Value invalidArg2=new ValueBuilder().string("invalid").build();
        Value invalidArg3=new ValueBuilder().identifier("invalid").build();

        Value v=new ValueBuilder().function("counters", invalidArg1, invalidArg2, invalidArg3).build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void attr()
    {
        Value v=new ValueBuilder().attr("myattr").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void invalidAttrArgType()
    {
        Value invalidArg=new ValueBuilder().string("invalid").build();
        Value v=new ValueBuilder().function("myattr", invalidArg).build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void wrongNumberOfArgArgs()
    {
        Value invalidArg1=new ValueBuilder().string("invalid").build();
        Value invalidArg2=new ValueBuilder().string("invalid").build();
        Value v=new ValueBuilder().function("myattr", invalidArg1, invalidArg2).build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void openQuote()
    {
        Value v=new ValueBuilder().identifier("open-quote").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void closeQuote()
    {
        Value v=new ValueBuilder().identifier("close-quote").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void noOpenQuote()
    {
        Value v=new ValueBuilder().identifier("no-open-quote").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void noCloseQuote()
    {
        Value v=new ValueBuilder().identifier("no-close-quote").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void invalid()
    {
        Value v=new ValueBuilder().identifier("sdfffdgfdgf").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void test2Valid()
    {
        Value v=new ValueBuilder().string("str1").string("str2").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void test5Valid()
    {
        Value v=new ValueBuilder().string("str1").string("str2").string("str3").string("str4").string("str5").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertTrue(result);
    }

    @Test
    public void testInvalidMultiples()
    {
        Value v=new ValueBuilder().string("str1").identifier("rsdgfsdgfdsg").string("str3").string("str4").string("str5").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void inheritCantBeCombined()
    {
        Value v=new ValueBuilder().identifier("inherit").identifier("open-quote").build();
        boolean result=content.validateContentProperty(v);
        Assert.assertFalse(result);
    }

    @Test
    public void formatCounterAsDecimal()
    {
        Assert.assertEquals("35", GeneratedContent.formatCounterAsDecimal(35));
    }

    @Test
    public void formatCounterAsDecimalLeadingZeroSingleDigit()
    {
        Assert.assertEquals("04", GeneratedContent.formatCounterAsDecimalLeadingZero(4));
    }

    @Test
    public void formatCounterAsDecimalLeadingZero()
    {
        Assert.assertEquals("12", GeneratedContent.formatCounterAsDecimalLeadingZero(12));
    }

    @Test
    public void formatCounterAsLowerRoman()
    {
        Assert.assertEquals("i", GeneratedContent.formatCounterAsLowerRoman(1));
        Assert.assertEquals("iv", GeneratedContent.formatCounterAsLowerRoman(3));
        Assert.assertEquals("v", GeneratedContent.formatCounterAsLowerRoman(5));
        Assert.assertEquals("xii", GeneratedContent.formatCounterAsLowerRoman(12));
    }

    @Test
    public void formatCounterAsUpperRoman()
    {
        Assert.assertEquals("I", GeneratedContent.formatCounterAsLowerRoman(1));
        Assert.assertEquals("IV", GeneratedContent.formatCounterAsLowerRoman(3));
        Assert.assertEquals("V", GeneratedContent.formatCounterAsLowerRoman(5));
        Assert.assertEquals("XII", GeneratedContent.formatCounterAsLowerRoman(12));
    }

    @Test
    public void formatCounterAsLowerAlpha()
    {
        Assert.assertEquals("a", GeneratedContent.formatCounterAsLowerAlpha(1));
        Assert.assertEquals("k", GeneratedContent.formatCounterAsLowerAlpha(10));
        Assert.assertEquals("z", GeneratedContent.formatCounterAsLowerAlpha(26));
        Assert.assertEquals("aa", GeneratedContent.formatCounterAsLowerAlpha(27));
    }

    @Test
    public void formatCounterAsUpperAlpha()
    {
        Assert.assertEquals("A", GeneratedContent.formatCounterAsUpperAlpha(1));
        Assert.assertEquals("K", GeneratedContent.formatCounterAsUpperAlpha(11));
        Assert.assertEquals("Z", GeneratedContent.formatCounterAsUpperAlpha(26));
        Assert.assertEquals("AA", GeneratedContent.formatCounterAsUpperAlpha(27));
    }

    @Test
    @Ignore
    public void formatAsGeorgian()
    {
    }

    @Test
    @Ignore
    public void formatCounterAsArmenian()
    {
    }

    @Test
    @Ignore
    public void formatCounterAsLowerGreek()
    {
    }
}
