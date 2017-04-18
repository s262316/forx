package com.github.s262316.forx.css;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InInsideNestedBlockTest
{
    @Test
    public void notInNestedBlock()
    {
        Tokenizer tokenizer = new Tokenizer("div");
        tokenizer.advance();

        IsInsideNestedBlock skipper = new IsInsideNestedBlock();

        Assert.assertFalse(skipper.test(tokenizer));
    }

    @Test
    public void skipANestedBlock()
    {
        Tokenizer tokenizer = new Tokenizer("{ skip : me } div { color : red }");
        tokenizer.advance();

        IsInsideNestedBlock skipper = new IsInsideNestedBlock();

        Assert.assertEquals("{", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("skip", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals(":", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("me", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("}", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("div", tokenizer.curr.syntax);
        Assert.assertFalse(skipper.test(tokenizer));
    }

    @Test
    public void doubleNestedBlock()
    {
        Tokenizer tokenizer = new Tokenizer("{ skip : me { skip2 : me2 } } div { color : red }");
        tokenizer.advance();

        IsInsideNestedBlock skipper = new IsInsideNestedBlock();

        Assert.assertEquals("{", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("skip", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals(":", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("me", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("{", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("skip2", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals(":", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("me2", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("}", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("}", tokenizer.curr.syntax);
        Assert.assertTrue(skipper.test(tokenizer));

        tokenizer.advance();
        Assert.assertEquals("div", tokenizer.curr.syntax);
        Assert.assertFalse(skipper.test(tokenizer));
    }

    @Test
    public void integrateWithAdvanceUntil()
    {
        Tokenizer tokenizer = new Tokenizer("{ skip : me { skip2 : me2 } } div { color : red }");
        tokenizer.advance();

        Assert.assertEquals("{", tokenizer.curr.syntax);

        tokenizer.advanceUntil(new IsInsideNestedBlock().negate());

        Assert.assertEquals("div", tokenizer.curr.syntax);
    }
}
