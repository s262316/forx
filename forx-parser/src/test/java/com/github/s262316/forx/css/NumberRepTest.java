package com.github.s262316.forx.css;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberRepTest
{
	@Test
	public void blankIs0()
	{
		assertEquals("", NumberRep.toAlphabeticalCount(0));
	}

	@Test
	public void aIs1()
	{
		assertEquals("A", NumberRep.toAlphabeticalCount(1));
	}

	@Test
	public void minusAIsMinus1()
	{
		assertEquals("-A", NumberRep.toAlphabeticalCount(-1));
	}

	@Test
	public void lIs12()
	{
		assertEquals("L", NumberRep.toAlphabeticalCount(12));
	}

	@Test
	public void zIs26()
	{
		assertEquals("Z", NumberRep.toAlphabeticalCount(26));
	}

	@Test
	public void aaIs27()
	{
		assertEquals("AA", NumberRep.toAlphabeticalCount(27));
	}

	@Test
	public void alIs38()
	{
		assertEquals("AL", NumberRep.toAlphabeticalCount(38));
	}

	@Test
	public void bzIs78()
	{
		assertEquals("BZ", NumberRep.toAlphabeticalCount(78));
	}

	@Test
	public void azIs52()
	{
		assertEquals("AZ", NumberRep.toAlphabeticalCount(52));
	}

	@Test
	public void baIs53()
	{
		assertEquals("BA", NumberRep.toAlphabeticalCount(53));
	}

	@Test
	public void szIs520()
	{
		assertEquals("SZ", NumberRep.toAlphabeticalCount(520));
	}

	@Test
	public void cuzIs2600()
	{
		assertEquals("CUZ", NumberRep.toAlphabeticalCount(2600));
	}

	@Test
	public void ntpIs10000()
	{
		assertEquals("NTP", NumberRep.toAlphabeticalCount(10000));
	}

}
