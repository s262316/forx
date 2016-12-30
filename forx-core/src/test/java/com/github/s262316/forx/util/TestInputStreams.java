package com.github.s262316.forx.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class TestInputStreams
{
	static final byte SEMICOLON=59;
	
	@Test
	public void testAtEndOfFirstBufferFilled() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("A"+RandomStringUtils.randomAlphabetic(198)+";"));
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(199, pos);
		assertEquals('A', inputstream.read());
	}	
	
	@Test
	public void testAtEndOfFirstBufferShort() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("ABCDEFGHI;"));
		//                                             0123456789
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(9, pos);
		assertEquals('A', inputstream.read());
	}
	
	@Test
	public void testAtStartOfFirstBuffer() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream(";ABCDEFGHI"));
		//                                             0123456789
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(0, pos);
		assertEquals(';', inputstream.read());
	}

	@Test
	public void testStartOfSecondBuffer() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("ABCDEFGHIJ"+";KLMNOPQR"));
		//                                             0123456789   012345678
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(10, pos);
		assertEquals('A', inputstream.read());
	}
	
	@Test
	public void testEndOfSecondBufferShort() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("ABCDEFGHIJ"+"KLMNOPQR;"));
		//                                             0123456789   012345678
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(18, pos);
		assertEquals('A', inputstream.read());
	}	
	
	@Test
	public void testEndOfSecondBufferFilled() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("A"+RandomStringUtils.randomAlphabetic(398)+";"));
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(399, pos);
		assertEquals('A', inputstream.read());
	}

	@Test
	public void testNotFound1Buffer() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("A"+RandomStringUtils.randomAlphabetic(199)));
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(-1, pos);
		assertEquals('A', inputstream.read());
	}
	
	@Test
	public void testNotFound2Buffers() throws Exception
	{
		BufferedInputStream inputstream=new BufferedInputStream(IOUtils.toInputStream("A"+RandomStringUtils.randomAlphabetic(399)));
		int pos=InputStreams.indexOf(inputstream, SEMICOLON);
		assertEquals(-1, pos);
		assertEquals('A', inputstream.read());
	}
}
