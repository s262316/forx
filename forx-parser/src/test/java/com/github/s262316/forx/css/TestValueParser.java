package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.ValueList;

public class TestValueParser
{
	@Test
	public void singleValueIsParsedIntoValueList() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("1");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(new NumericValue(1, "px")));
	}
	
	@Test
	public void multiValueIsParsedIntoValueList() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("1 2 3 4");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new NumericValue(1, "px"),
				new NumericValue(2, "px"),
				new NumericValue(3, "px"),
				new NumericValue(4, "px")
		));
	}

	@Test
	public void tokenizerPointsToNextTokenAfterValue() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("1 ;");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		parser.parse();
		
		Assert.assertEquals(TokenType.CR_PUNCT, tokenizer.curr.type);
		Assert.assertEquals(";", tokenizer.curr.syntax);
	}	
	
	@Test(expected=BadValueException.class)
	public void onErrorAdvancesPastValues() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("1 2 url('' 3 4 ;");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		parser.parse();
	}

	@Test(expected=BadValueException.class)
	public void unexpectedEofInFunction() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("rgb(0, 1 , 128");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		parser.parse();
	}

}