package com.github.s262316.forx.css;

import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
        assertTrue(result);
    }

    @Test
    public void none()
    {
        Value v=new ValueBuilder().identifier("none").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void string()
    {
        Value v=new ValueBuilder().string("str").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void uri() throws Exception
    {
        Value v=new ValueBuilder().uri("http://uri").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void counter()
    {
        Value v=new ValueBuilder().counter("countername").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void counterWithValidListStyle()
    {
        when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("lower-roman").build())).thenReturn(true);

        Value v=new ValueBuilder().counter("countername", "lower-roman").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void counterWithInvalidListStyle()
    {
        when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("aaaa").build())).thenReturn(false);

        Value v=new ValueBuilder().counter("countername", "aaaa").build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }

    @Test
    public void counterWithWrongNumberOfArgs()
    {
        when(mockLists.validateListStyleProperty(new ValueBuilder().identifier("aaaa").build())).thenReturn(false);

        Value invalidArg1=new ValueBuilder().identifier("invalid").build();
        Value invalidArg2=new ValueBuilder().identifier("invalid").build();
        Value invalidArg3=new ValueBuilder().identifier("invalid").build();

        Value v=new ValueBuilder().function("counter", invalidArg1, invalidArg2, invalidArg3).build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }

    @Test
    public void attr()
    {
        Value v=new ValueBuilder().attr("myattr").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void invalidAttrArgType()
    {
        Value invalidArg=new ValueBuilder().string("invalid").build();
        Value v=new ValueBuilder().function("myattr", invalidArg).build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }

    @Test
    public void wrongNumberOfArgArgs()
    {
        Value invalidArg1=new ValueBuilder().string("invalid").build();
        Value invalidArg2=new ValueBuilder().string("invalid").build();
        Value v=new ValueBuilder().function("myattr", invalidArg1, invalidArg2).build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }

    @Test
    public void openQuote()
    {
        Value v=new ValueBuilder().identifier("open-quote").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void closeQuote()
    {
        Value v=new ValueBuilder().identifier("close-quote").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void noOpenQuote()
    {
        Value v=new ValueBuilder().identifier("no-open-quote").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void noCloseQuote()
    {
        Value v=new ValueBuilder().identifier("no-close-quote").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void invalid()
    {
        Value v=new ValueBuilder().identifier("sdfffdgfdgf").build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }

    @Test
    public void test2Valid()
    {
        Value v=new ValueBuilder().string("str1").string("str2").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void test5Valid()
    {
        Value v=new ValueBuilder().string("str1").string("str2").string("str3").string("str4").string("str5").build();
        boolean result=content.validateContentProperty(v);
        assertTrue(result);
    }

    @Test
    public void testInvalidMultiples()
    {
        Value v=new ValueBuilder().string("str1").identifier("rsdgfsdgfdsg").string("str3").string("str4").string("str5").build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }

    @Test
    public void inheritCantBeCombined()
    {
        Value v=new ValueBuilder().identifier("inherit").identifier("open-quote").build();
        boolean result=content.validateContentProperty(v);
        assertFalse(result);
    }
}
