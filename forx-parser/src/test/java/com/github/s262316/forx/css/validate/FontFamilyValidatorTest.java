package com.github.s262316.forx.css.validate;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.github.s262316.forx.css.Tokenizer;
import com.github.s262316.forx.css.ValueParser;
import com.github.s262316.forx.style.ValueList;

public class FontFamilyValidatorTest
{
	@Test
	public void testSingleWithIdentifier() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("Arial");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		
		FontFamilyValidator ffv=new FontFamilyValidator();
		assertTrue(ffv.validate(list));
	}
	
	@Test
	public void testSingleWithString() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("\"Arial\"");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		
		FontFamilyValidator ffv=new FontFamilyValidator();
		assertTrue(ffv.validate(list));
	}

	@Test
	public void testList() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("\"Times New Roman\" , \"Arial\"");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		
		FontFamilyValidator ffv=new FontFamilyValidator();
		assertTrue(ffv.validate(list));
	}
	
	@Test
	public void testInvalid() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("2");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		
		FontFamilyValidator ffv=new FontFamilyValidator();
		assertFalse(ffv.validate(list));
	}
	
	@Test
	public void testInvalidList() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("\"Times New Roman\" , 2 , \"Arial\"");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		
		FontFamilyValidator ffv=new FontFamilyValidator();
		assertFalse(ffv.validate(list));
	}
}
