package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    public void testEofInUrlResultsInUrl()
    {
        Tokenizer tokenizer = new Tokenizer("url( 'test.html");
        tokenizer.advance();

        assertEquals(TokenType.CR_URI, tokenizer.curr.type);
        assertEquals("test.html", tokenizer.curr.syntax);
    }

    @Test
    public void testIdentInUrl()
    {
        Tokenizer tokenizer = new Tokenizer("url( test.html)");
        tokenizer.advance();

        assertEquals(TokenType.CR_URI, tokenizer.curr.type);
        assertEquals("test.html", tokenizer.curr.syntax);
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
    
    @Test
    public void advanceToLaterToken()
    {
        Tokenizer tokenizer = new Tokenizer("div { color : red ; font-size : 3px }");
        
        tokenizer.advance();
        tokenizer.advanceTo(TokenType.CR_PUNCT, ";");
        
        assertEquals(";", tokenizer.curr.syntax);
    }

    @Test
    public void advanceToTokenAlreadyOn()
    {
        Tokenizer tokenizer = new Tokenizer("; font-size : 3px }");
        
        tokenizer.advance();
        tokenizer.advanceTo(TokenType.CR_PUNCT, ";");
    	
        assertEquals(";", tokenizer.curr.syntax);        
    }
    
    @Test
    public void advancePastLaterToken()
    {
        Tokenizer tokenizer = new Tokenizer("div { color : red ; font-size : 3px }");
        
        tokenizer.advance();
        tokenizer.advancePast(TokenType.CR_PUNCT, ";");
        
        assertEquals("font-size", tokenizer.curr.syntax);
    }

    @Test
    public void advancePastTokenAlreadyOn()
    {
        Tokenizer tokenizer = new Tokenizer("; font-size : 3px }");
        
        tokenizer.advance();
        tokenizer.advancePast(TokenType.CR_PUNCT, ";");
    	
        assertEquals("font-size", tokenizer.curr.syntax);        
    }    

    @Test
    public void testTokenizeIdentifier1()
    {
        Tokenizer tokenizer = new Tokenizer("abc ");
        Token token=tokenizer.nextToken();
        assertEquals("abc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier2()
    {
        Tokenizer tokenizer = new Tokenizer("a_bc ");
        Token token=tokenizer.nextToken();
        assertEquals("a_bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier3()
    {
        Tokenizer tokenizer = new Tokenizer("a_bc ");
        Token token=tokenizer.nextToken();
        assertEquals("a_bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier4()
    {
        Tokenizer tokenizer = new Tokenizer("-abc ");
        Token token=tokenizer.nextToken();
        assertEquals("-abc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier5()
    {
        Tokenizer tokenizer = new Tokenizer("a•bc ");
        Token token=tokenizer.nextToken();
        assertEquals("a•bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier6()
    {
        Tokenizer tokenizer = new Tokenizer("•abc ");
        Token token=tokenizer.nextToken();
        assertEquals("•abc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier7()
    {
        Tokenizer tokenizer = new Tokenizer("a1bc ");
        Token token=tokenizer.nextToken();
        assertEquals("a1bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier8()
    {
        Tokenizer tokenizer = new Tokenizer("a-bc ");
        Token token=tokenizer.nextToken();
        assertEquals("a-bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifierWithEscapedChars()
    {
        Tokenizer tokenizer = new Tokenizer("a\\23bc ");
        Token token=tokenizer.nextToken();
        assertEquals("a\u23bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifierWithEscapedCharAtStart()
    {
        Tokenizer tokenizer = new Tokenizer("\\23bc ");
        Token token=tokenizer.nextToken();
        assertEquals("\u23bc", token.syntax);
    }

    @Test
    public void testTokenizeIdentifier11()
    {
        Tokenizer tokenizer = new Tokenizer("_abc ");
        Token token=tokenizer.nextToken();
        assertEquals("_abc", token.syntax);
    }

    @Test
    public void testEscapedIdentifier()
    {
        Tokenizer tokenizer = new Tokenizer("\\63\\6F\\6C\\6F\\72");
        // \63\6F\6C\6F\72
        // 012345678901234
        Token token=tokenizer.nextToken();
        assertEquals("color", token.syntax);
    }

    @Test
    public void testTokenizeIndentifierWithEscapedNewline()
    {
        Tokenizer tokenizer = new Tokenizer("my\\\r\nid");
        Token token;
        token=tokenizer.nextToken();

        assertEquals("my", token.syntax);
        assertEquals(TokenType.CR_IDENT, token.type);
    }

    @Test
    public void testTokenizeIdentifierWithLessThan6Produces2Tokens()
    {
        Tokenizer tokenizer = new Tokenizer("a\\23bc abc");
        Token token;
        token=tokenizer.nextToken();
        assertEquals("a\u23bcabc", token.syntax);
    }

    @Test
    public void testTokenizeStringDoubleQuotes()
    {
        Tokenizer tokenizer = new Tokenizer("\"hello\"");
        Token token;

        token = tokenizer.nextToken();
        assertEquals("hello", token.syntax);
        assertEquals(TokenType.CR_STRING, token.type);
    }

    @Test
    public void testTokenizeStringSingleQuotes()
    {
        Tokenizer tokenizer = new Tokenizer("\'hello\'");
        Token token;

        token = tokenizer.nextToken();
        assertEquals("hello", token.syntax);
        assertEquals(TokenType.CR_STRING, token.type);
    }

    @Test
    public void prematureEndOfStringResultsInInvalidToken()
    {
        Tokenizer tokenizer = new Tokenizer("\"hello\r\n div { next : one } ");
        Token token;

        try
        {
            token = tokenizer.nextToken();
            fail();
        }
        catch(TokenizationException te)
        {
            assertEquals("invalid string", te.getMessage());
        }
    }

    @Test
    public void afterrematureEndOfStringCursorIsOnEol()
    {
        Tokenizer tokenizer = new Tokenizer("\"hello\r\n div { next : one } ");
        Token token;

        try
        {
            token = tokenizer.nextToken();
        }
        catch(TokenizationException te)
        {}

        // next token should be div
        token = tokenizer.nextToken();
        assertEquals("\r\n ", token.syntax);
    }

    @Test
    public void tokenizeStringWithEscapedNewline()
    {
        Tokenizer tokenizer = new Tokenizer("\"my\\\r\nid\"");
        Token token;
        token=tokenizer.nextToken();
        assertEquals(TokenType.CR_STRING, token.type);
        assertEquals("myid", token.syntax);
    }

    @Test
    public void tokenizeEscapedNewlineInTheMiddleOfNowhere()
    {
        Tokenizer tokenizer = new Tokenizer("\\\r\nid");
        Token token;
        token=tokenizer.nextToken();

        assertEquals(TokenType.CR_IDENT, token.type);
        assertEquals("id", token.syntax);
    }

    @Test
    public void identOfAllEscapedHasNextToken()
    {
        Tokenizer tokenizer = new Tokenizer("\\64\\69\\76 { color : green } ");
        Token token;

        token=tokenizer.nextToken();
        assertEquals("div", token.syntax);
        assertEquals(TokenType.CR_IDENT, token.type);

        token=tokenizer.nextToken();
        assertEquals("{", token.syntax);
        assertEquals(TokenType.CR_PUNCT, token.type);
    }
}
