package com.github.s262316.forx.css;

public class CssParserUtils
{
	public static boolean isIdentifier(String text)
	{
		Tokenizer tokenizer=new Tokenizer(text);
		
		tokenizer.advance();
		if(tokenizer.curr.type!=TokenType.CR_IDENT)
			return false;
		
		tokenizer.advance();
		if(tokenizer.curr.type!=TokenType.CR_END)
			return false;
		
		return true;		
	}
}
