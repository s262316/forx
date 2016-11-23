package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class TestTokenizer
{
    @Test
    public void testReplaceUnicodeSequences()
    {
        Tokenizer tokenizer=new Tokenizer("");

        assertEquals("charset",
                tokenizer.replaceEscapes(new StringBuilder("\\63\\68\\61\\72\\73\\65\\74")));
    }

    @Test
    public void replaceUnicodeSequenceSpaceIndicatesEndOfSequence()
    {
        Tokenizer tokenizer=new Tokenizer("");

        assertEquals("charset",
                tokenizer.replaceEscapes(new StringBuilder("\\63 \\68 \\61 \\72 \\73 \\65 \\74")));
    }

    @Test
    public void replaceUnicodeSequenceDoubleSpaceResultsIn1Space()
    {
        Tokenizer tokenizer=new Tokenizer("");

        assertEquals("c h a r s e t",
                tokenizer.replaceEscapes(new StringBuilder("\\63  \\68  \\61  \\72  \\73  \\65  \\74")));
    }

    @Test
    public void replaceUnicodeSequenceTrailingSingleSpace()
    {
        Tokenizer tokenizer = new Tokenizer("");

        assertEquals("c h a r s e t",
                tokenizer.replaceEscapes(new StringBuilder("\\63  \\68  \\61  \\72  \\73  \\65  \\74 ")));
    }

    @Test
    public void replaceUnicodeSequenceTrailingDoubleSpace()
    {
        Tokenizer tokenizer=new Tokenizer("");

        assertEquals("c h a r s e t ",
                tokenizer.replaceEscapes(new StringBuilder("\\63  \\68  \\61  \\72  \\73  \\65  \\74  ")));
    }

    @Test
    public void replacePunctuation()
    {
        Tokenizer tokenizer = new Tokenizer("");

        assertEquals("{",
                tokenizer.replaceEscapes(new StringBuilder("\\{")));
    }
    
    @Test
    public void testAdvanceUntilNoTokensToAdvance()
    {
        Tokenizer tokenizer = new Tokenizer("div");
        tokenizer.advance();
        tokenizer.advanceUntil(v -> false);
        
        assertEquals(TokenType.CR_END, tokenizer.curr.type);
    }
    
    @Test
    public void testAdvanceUntil1TokenToAdvance()
    {
        Tokenizer tokenizer = new Tokenizer("a b");
        tokenizer.advance();
        
        tokenizer.advanceUntil(v -> v.curr.syntax.equals("b")?true:false);
        
        assertEquals("b", tokenizer.curr.syntax);
    }
 
    @Test
    public void testAdvanceUntil2TokensToAdvance()
    {
        Tokenizer tokenizer = new Tokenizer("a b c");
        tokenizer.advance();

        tokenizer.advanceUntil(v -> v.curr.syntax.equals("b")?false:
        							v.curr.syntax.equals("c")?true:false);

        assertEquals("c", tokenizer.curr.syntax);
    }

    @Test
    public void testSimpleUrl()
    {
        Tokenizer tokenizer = new Tokenizer("url('test.html')");
        tokenizer.advance();

        assertEquals(TokenType.CR_URI, tokenizer.curr.type);
        assertEquals("test.html", tokenizer.curr.syntax);
    }

    @Test
    public void testUrlWhitespace1()
    {
        Tokenizer tokenizer = new Tokenizer("url( 'test.html')");
        tokenizer.advance();

        assertEquals(TokenType.CR_URI, tokenizer.curr.type);
        assertEquals("test.html", tokenizer.curr.syntax);
    }

    @Test
    public void testUrlWhitespace2()
    {
        Tokenizer tokenizer = new Tokenizer("url( 'test.html' )");
        tokenizer.advance();

        assertEquals(TokenType.CR_URI, tokenizer.curr.type);
        assertEquals("test.html", tokenizer.curr.syntax);
    }

    @Test
    public void testMalformedUrl1()
    {
        Tokenizer tokenizer = new Tokenizer("url ('test.html')");
        tokenizer.advance();

        assertEquals(TokenType.CR_IDENT, tokenizer.curr.type);
        assertEquals("url", tokenizer.curr.syntax);

        tokenizer.advance();
        assertEquals(TokenType.CR_PUNCT, tokenizer.curr.type);
        assertEquals("(", tokenizer.curr.syntax);

        tokenizer.advance();
        assertEquals(TokenType.CR_STRING, tokenizer.curr.type);
        assertEquals("test.html", tokenizer.curr.syntax);

        tokenizer.advance();
        assertEquals(TokenType.CR_PUNCT, tokenizer.curr.type);
        assertEquals(")", tokenizer.curr.syntax);
    }

    @Test
    public void testMalformedUrl2()
    {
        Tokenizer tokenizer = new Tokenizer("url('test.html' ");
        tokenizer.advance();
        assertEquals(TokenType.CR_ERROR, tokenizer.curr.type);
    }

    @Test
    public void numberIdentSpaceSeparatedIs2Tokens()
    {
        Tokenizer tokenizer = new Tokenizer("1 em");

        tokenizer.advance();
        assertEquals(TokenType.CR_NUMBER, tokenizer.curr.type);
        assertEquals("1", tokenizer.curr.syntax);

        tokenizer.advance();
        assertEquals(TokenType.CR_IDENT, tokenizer.curr.type);
        assertEquals("em", tokenizer.curr.syntax);
    }

    @Test
    public void numberPercentSpaceSeparatedIs2Tokens()
    {
        Tokenizer tokenizer = new Tokenizer("1 %");

        tokenizer.advance();
        assertEquals(TokenType.CR_NUMBER, tokenizer.curr.type);
        assertEquals("1", tokenizer.curr.syntax);

        tokenizer.advance();
        assertEquals(TokenType.CR_PUNCT, tokenizer.curr.type);
        assertEquals("%", tokenizer.curr.syntax);
    }

    @Test
    public void tokenizeNumber1()
    {
        Tokenizer tokenizer = new Tokenizer("1");

        tokenizer.advance();
        assertEquals(TokenType.CR_NUMBER, tokenizer.curr.type);
        assertEquals("1", tokenizer.curr.syntax);
    }

    @Test
    public void tokenizeNumber2()
    {
        Tokenizer tokenizer = new Tokenizer("12");

        tokenizer.advance();
        assertEquals(TokenType.CR_NUMBER, tokenizer.curr.type);
        assertEquals("12", tokenizer.curr.syntax);
    }

    @Test
    public void tokenizeNumber3()
    {
        Tokenizer tokenizer = new Tokenizer("12.3");

        tokenizer.advance();
        assertEquals(TokenType.CR_NUMBER, tokenizer.curr.type);
        assertEquals("12.3", tokenizer.curr.syntax);
    }

    @Test
    public void tokenizeNumber4()
    {
        Tokenizer tokenizer = new Tokenizer("12.34");

        tokenizer.advance();
        assertEquals(TokenType.CR_NUMBER, tokenizer.curr.type);
        assertEquals("12.34", tokenizer.curr.syntax);
    }

    @Test
    public void tokenizeDimension()
    {
        Tokenizer tokenizer = new Tokenizer("1em");

        tokenizer.advance();
        assertEquals(TokenType.CR_DIMENSION, tokenizer.curr.type);
        assertEquals("1em", tokenizer.curr.syntax);
    }

    @Test
    public void tokenizePercent()
    {
        Tokenizer tokenizer = new Tokenizer("1%");

        tokenizer.advance();
        assertEquals(TokenType.CR_PERCENT, tokenizer.curr.type);
        assertEquals("1", tokenizer.curr.syntax);
    }

    @Test
    public void whitespaceIsSkipped()
    {
        Tokenizer tokenizer = new Tokenizer("1 2 3");

        tokenizer.advance();
        assertEquals("1", tokenizer.curr.syntax);
        tokenizer.advance();
        assertEquals("2", tokenizer.curr.syntax);
        tokenizer.advance();
        assertEquals("3", tokenizer.curr.syntax);
    }

    @Test
    public void skippedWhitespaceIsRecorded()
    {
        Tokenizer tokenizer = new Tokenizer("1 2 3");

        tokenizer.advance();
        assertEquals(false, tokenizer.curr.precedingWhitespaceSkipped);
        tokenizer.advance();
        assertEquals(true, tokenizer.curr.precedingWhitespaceSkipped);
        tokenizer.advance();
        assertEquals(true, tokenizer.curr.precedingWhitespaceSkipped);
    }

    @Test
    public void testHashNumeric()
    {
        Tokenizer tokenizer = new Tokenizer("#1");

        tokenizer.advance();
        assertEquals(TokenType.CR_HASH, tokenizer.curr.type);
        assertEquals("1", tokenizer.curr.syntax);
    }

    @Test
    public void testHashAlpha()
    {
        Tokenizer tokenizer = new Tokenizer("#a");

        tokenizer.advance();
        assertEquals(TokenType.CR_HASH, tokenizer.curr.type);
        assertEquals("a", tokenizer.curr.syntax);
    }

    @Test
    public void testNotHash()
    {
        Tokenizer tokenizer = new Tokenizer("# a");

        tokenizer.advance();
        assertEquals(TokenType.CR_HASH, tokenizer.curr.type);
        assertEquals("", tokenizer.curr.syntax);

        tokenizer.advance();
        assertEquals(TokenType.CR_IDENT, tokenizer.curr.type);
        assertEquals("a", tokenizer.curr.syntax);
    }
}

