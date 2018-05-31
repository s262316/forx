package com.github.s262316.forx.css;

import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.ValueBuilder;
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
		Assert.assertThat(list.members, Matchers.contains(new NumericValue(1, "")));
	}
	
	@Test
	public void multiValueIsParsedIntoValueList() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("1 2 3 4");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new NumericValue(1, ""),
				new NumericValue(2, ""),
				new NumericValue(3, ""),
				new NumericValue(4, "")
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
	
	@Test
	public void commaSeparatedValuesiosCombinedIntoASublist1() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("A B,C D");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new Identifier("A"),
				new ValueBuilder().identifier("B").identifier("C").buildAsList(),
				new Identifier("D")
		));
	}
	
	@Test
	public void commaSeparatedValuesiosCombinedIntoASublist2() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("A,B C D");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().identifier("A").identifier("B").buildAsList(),
				new Identifier("C"),
				new Identifier("D")
		));
	}
	
	@Test
	public void commaSeparatedValuesiosCombinedIntoASublist3() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("A B C,D");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new Identifier("A"),
				new Identifier("B"),
				new ValueBuilder().identifier("C").identifier("D").buildAsList()
		));
	}
	
	@Test
	public void commaSeparatedValuesiosCombinedIntoASublist4() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("A,B,C,D");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().identifier("A").identifier("B").identifier("C").identifier("D").buildAsList()
		));
	}
	
	@Test
	public void commaSeparatedValuesiosCombinedIntoASublist5() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("A B C , D");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new Identifier("A"),
				new Identifier("B"),
				new ValueBuilder().identifier("C").identifier("D").buildAsList()
		));
	}
	
	@Test
	public void slashSeparatedValuesiosCombinedIntoASublist4() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("A/B/C/D");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().identifier("A").identifier("B").identifier("C").identifier("D").buildAsList()
		));
	}

	@Test
	public void slashSeparatedValuesiosCombinedIntoASublist5() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100px/1 Ahem");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "px").identifier("1").buildAsList(),
						new Identifier("Ahem")
		));
	}

	@Test
	public void testExtractDimensionEm() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100em");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "em").build()));
	}

	@Test
	public void testExtractDimensionEx() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100ex");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "ex").build()));
	}

	@Test
	public void testExtractDimensionIn() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100in");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "in").build()));
	}

	@Test
	public void testExtractDimensionCm() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100cm");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "cm").build()));
	}

	@Test
	public void testExtractDimensionMm() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100mm");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "mm").build()));
	}

	@Test
	public void testExtractDimensionPt() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100pt");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "pt").build()));
	}

	@Test
	public void testExtractDimensionPc() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100pc");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "pc").build()));
	}

	@Test
	public void testExtractDimensionPx() throws Exception
	{
		Tokenizer tokenizer=new Tokenizer("100px");
		ValueParser parser=new ValueParser(tokenizer);

		tokenizer.advance();
		ValueList list=parser.parse();
		Assert.assertThat(list.members, Matchers.contains(
				new ValueBuilder().length(100, "px").build()));
	}


}
