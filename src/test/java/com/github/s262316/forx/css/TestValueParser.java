package com.github.s262316.forx.css;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestValueParser
{
	@Test
	public void onErrorAdvancesPastValues() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("1 2 url('' 3 4 ;");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();

		try
		{
			parser.parse();
			// shouldn't get here
			fail();
		}
		catch(BadValueException bve)
		{}
		
		assertEquals(";", tokenizer.curr.syntax);
	}
}
