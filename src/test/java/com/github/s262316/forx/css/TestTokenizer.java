package com.github.s262316.forx.css;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        
        assertEquals(TokenType.CR_END, tokenizer.curr.type);
    }
 
    @Test
    public void testAdvanceUntil2TokensToAdvance()
    {
        Tokenizer tokenizer = new Tokenizer("a b c");
        tokenizer.advance();

        tokenizer.advanceUntil(v -> v.curr.syntax.equals("b")?false:
        							v.curr.syntax.equals("c")?true:false);

        assertEquals(TokenType.CR_END, tokenizer.curr.type);
    }
}

