package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class TestCssCharset
{
	
	String css1="@charset \"US-ASCII\";p{color : red }";
	String css2="p{color : red }";
	
	@Test(expected=BadCharsetException.class)
	public void testUnknownCharset() throws Exception
	{
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(new BufferedInputStream(IOUtils.toInputStream("@charset \"test\";")));
		fail();
	}

	@Test
	public void testNoCharset() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(IOUtils.toInputStream(css2, "UTF-8"));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(null, charset);
		assertEquals('p', bis.read());
	}
	
	@Test
	public void testUtf8() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(IOUtils.toInputStream(css1, "UTF-8"));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.US_ASCII, charset);
		assertEquals('p', bis.read());
	}

	@Test
	public void testUtf8BomAtCharset() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(ArrayUtils.addAll(CssCharset.UTF8_BOM, css1.getBytes(StandardCharsets.UTF_8))));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.US_ASCII, charset);
		assertEquals('p', bis.read());
		
		bis.close();		
	}
	
	@Test
	public void testUtf16Be() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(IOUtils.toInputStream(css1, "UTF-16BE"));
		
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.US_ASCII, charset);
		assertEquals(0, bis.read());
		assertEquals('p', bis.read());
		
		bis.close();		
	}
	
	@Test
	public void testUtf16Le() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(IOUtils.toInputStream(css1, "UTF-16LE"));
		
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.US_ASCII, charset);
		assertEquals('p', bis.read());
		assertEquals(0, bis.read());
		
		bis.close();		
	}
	
	@Test
	public void testUtf16BeBom() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(ArrayUtils.addAll(CssCharset.UTF16BE_BOM, css1.getBytes(StandardCharsets.UTF_16BE))));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.US_ASCII, charset);
		assertEquals(0, bis.read());
		assertEquals('p', bis.read());
		
		bis.close();		
	}
	
	@Test
	public void testUtf16LeBom() throws Exception
	{
		byte b[]=ArrayUtils.addAll(CssCharset.UTF16LE_BOM, css1.getBytes(StandardCharsets.UTF_16LE));
		BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(b));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.US_ASCII, charset);
		
		for(int i=0; i<30; i++)
			System.out.print(bis.read()+" ");
		
//		assertEquals('p', bis.read());
//		assertEquals(0, bis.read());
		
		
		bis.close();		
	}	
	
	@Test
	public void testUtf16BeBomNoCharset() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(ArrayUtils.addAll(CssCharset.UTF16BE_BOM, css2.getBytes(StandardCharsets.UTF_16BE))));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.UTF_16BE, charset);
		assertEquals(0, bis.read());
		assertEquals('p', bis.read());
		
		bis.close();		
	}	
	
	@Test
	public void testUtf16LeBomNoCharset() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(ArrayUtils.addAll(CssCharset.UTF16LE_BOM, css2.getBytes(StandardCharsets.UTF_16LE))));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.UTF_16LE, charset);
		assertEquals('p', bis.read());
		assertEquals(0, bis.read());
		
		bis.close();		
	}	
	
	@Test
	public void testUtf8BomNoCharset() throws Exception
	{
		BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(ArrayUtils.addAll(CssCharset.UTF8_BOM, css2.getBytes(StandardCharsets.UTF_8))));
		CssCharset cssCharset=new CssCharset();
		Charset charset=cssCharset.sniffCharset(bis);
		assertEquals(StandardCharsets.UTF_8, charset);
		assertEquals('p', bis.read());
	}	

}
