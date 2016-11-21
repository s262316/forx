package com.github.s262316.forx.css;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.util.InputStreams;

/*
 * BOMs: 
 * UTF-8:      EF BB BF    | 239 187 191
 * UTF-16 BE:  FE FF	   | 254 255
 * UTF-16 LE:  FF FE	   | 255 254
 * UTF-32 (BE) 00 00 FE FF | 0 0 254 255 	
 * UTF-32 (LE) FF FE 00 00 | 255 254 0 0
 * 
 */
public class CssCharset
{
	private final static Logger logger=LoggerFactory.getLogger(CssCharset.class);
	
	public static final byte CHARSETRULE_UTF8[]="@charset \"".getBytes(StandardCharsets.UTF_8);
	public static final byte CHARSETRULE_UTF16BE[]="@charset \"".getBytes(StandardCharsets.UTF_16BE);
	public static final byte CHARSETRULE_UTF16LE[]="@charset \"".getBytes(StandardCharsets.UTF_16LE);

	public static final byte UTF8_BOM[]=new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};
	public static final byte UTF16BE_BOM[]=new byte[]{(byte)0xFE, (byte)0xFF};
	public static final byte UTF16LE_BOM[]=new byte[]{(byte)0xFF, (byte)0xFE};	

	public static final byte CHARSETRULE_UTF8_BOM[]=ArrayUtils.addAll(UTF8_BOM, "@charset \"".getBytes(StandardCharsets.UTF_8));
	public static final byte CHARSETRULE_UTF16BE_BOM[]=ArrayUtils.addAll(UTF16BE_BOM, "@charset \"".getBytes(StandardCharsets.UTF_16BE));
	public static final byte CHARSETRULE_UTF16LE_BOM[]=ArrayUtils.addAll(UTF16LE_BOM, "@charset \"".getBytes(StandardCharsets.UTF_16LE));

	public Reader getReader()
	{
		// 1. An HTTP "charset" parameter in a "Content-Type" field (or similar parameters in other protocols)
		// 2. BOM and/or @charset (see below)
		// 3. <link charset=""> or other metadata from the linking mechanism (if any)
		// 4. charset of referring style sheet or document (if any)
		// 5. Assume UTF-8
		
		return null;
	}

	public Charset sniffCharset(BufferedInputStream data) throws BadCharsetException
	{
		int pos;
		byte charsetRule[];
		
		try
		{
			pos=InputStreams.indexOf(data, (byte)';');	
			if(pos==-1)
				pos=2;
			
			data.mark(pos+1);
			
			charsetRule=new byte[pos+1];
			IOUtils.read(data, charsetRule);
			
			if(Arrays.equals(CHARSETRULE_UTF8_BOM, ArrayUtils.subarray(charsetRule, 0, CHARSETRULE_UTF8_BOM.length)))
			{
				// 0  1  2  3  4  5  6  7  8  9  10 11 12
				// EF BB BF 40 63 68 61 72 73 65 74 20 22 (XX)* 22 3B as specified
				// [BOM]@charset "

				// lift out the bit between the quotes
				int firstQuote=ArrayUtils.indexOf(charsetRule, (byte)'\"');
				int lastQuote=ArrayUtils.lastIndexOf(charsetRule, (byte)'\"');
				
				if(firstQuote!=lastQuote && firstQuote!=-1 && lastQuote!=-1)
				{
					byte charsetNameBytes[]=ArrayUtils.subarray(charsetRule, firstQuote+1, lastQuote);
					String charsetName=new String(charsetNameBytes, StandardCharsets.UTF_8);
					return Charset.forName(charsetName);
				}
			}
			else if(Arrays.equals(UTF8_BOM, ArrayUtils.subarray(charsetRule, 0, UTF8_BOM.length)))
			{
				// EF BB BF
				// [BOM]
				return StandardCharsets.UTF_8;
			}
			else if(Arrays.equals(CHARSETRULE_UTF8, ArrayUtils.subarray(charsetRule, 0, CHARSETRULE_UTF8.length)))
			{
				// 0  1  2  3  4  5  6  7  8  9 
				// 40 63 68 61 72 73 65 74 20 22 (XX)* 22 3B
				// @charset "
				
				int firstQuote=ArrayUtils.indexOf(charsetRule, (byte)'\"');
				int lastQuote=ArrayUtils.lastIndexOf(charsetRule, (byte)'\"');
				
				if(firstQuote!=lastQuote && firstQuote!=-1 && lastQuote!=-1)
				{
					byte charsetNameBytes[]=ArrayUtils.subarray(charsetRule, firstQuote+1, lastQuote);
					String charsetName=new String(charsetNameBytes, StandardCharsets.UTF_8);
					return Charset.forName(charsetName);
				}
			}
			else if(Arrays.equals(CHARSETRULE_UTF16BE_BOM, ArrayUtils.subarray(charsetRule, 0, CHARSETRULE_UTF16BE_BOM.length)))
			{
				// FE FF 00 40 00 63 00 68 00 61 00 72 00 73 00 65 00 74 00 20 00 22 (00 XX)* 00 22 00 3B
				// [BOM]@charset " UTF-16-BE
				
				int firstQuote=ArrayUtils.indexOf(charsetRule, (byte)'\"');
				int lastQuote=ArrayUtils.lastIndexOf(charsetRule, (byte)'\"');
				
				if(firstQuote!=lastQuote && firstQuote!=-1 && lastQuote!=-1)
				{
					byte charsetNameBytes[]=ArrayUtils.subarray(charsetRule, firstQuote+1, lastQuote-1);
					String charsetName=new String(charsetNameBytes, StandardCharsets.UTF_16BE);
					return Charset.forName(charsetName);
				}
			}
			else if(Arrays.equals(CHARSETRULE_UTF16BE, ArrayUtils.subarray(charsetRule, 0, CHARSETRULE_UTF16BE.length)))
			{
				// 00 40 00 63 00 68 00 61 00 72 00 73 00 65 00 74 00 20 00 22 (00 XX)* 00 22 00 3B
				// @charset "UTF-16-BE				
				
				int firstQuote=ArrayUtils.indexOf(charsetRule, (byte)'\"');
				int lastQuote=ArrayUtils.lastIndexOf(charsetRule, (byte)'\"');
				
				if(firstQuote!=lastQuote && firstQuote!=-1 && lastQuote!=-1)
				{
					byte charsetNameBytes[]=ArrayUtils.subarray(charsetRule, firstQuote+1, lastQuote-1);
					String charsetName=new String(charsetNameBytes, StandardCharsets.UTF_16BE);
					return Charset.forName(charsetName);
				}
			}
			else if(Arrays.equals(CHARSETRULE_UTF16LE_BOM, ArrayUtils.subarray(charsetRule, 0, CHARSETRULE_UTF16LE_BOM.length)))
			{
				// FF FE 40 00 63 00 68 00 61 00 72 00 73 00 65 00 74 00 20 00 22 00 (XX 00)* 22 00 3B 00
				// [BOM]@charset " UTF-16-LE
				
				// read 1 more byte which is the remainder of the last byte
				data.read();
				
				int firstQuote=ArrayUtils.indexOf(charsetRule, (byte)'\"');
				int lastQuote=ArrayUtils.lastIndexOf(charsetRule, (byte)'\"');
				
				if(firstQuote!=lastQuote && firstQuote!=-1 && lastQuote!=-1)
				{
					byte charsetNameBytes[]=ArrayUtils.subarray(charsetRule, firstQuote+2, lastQuote);
					String charsetName=new String(charsetNameBytes, StandardCharsets.UTF_16LE);
					return Charset.forName(charsetName);
				}
				
			}
			else if(Arrays.equals(CHARSETRULE_UTF16LE, ArrayUtils.subarray(charsetRule, 0, CHARSETRULE_UTF16LE.length)))
			{
				// 40 00 63 00 68 00 61 00 72 00 73 00 65 00 74 00 20 00 22 00 (XX 00)* 22 00 3B 00
				// UTF-16-LE
				
				// read 1 more byte which is the remainder of the last byte
				data.read();				
				
				int firstQuote=ArrayUtils.indexOf(charsetRule, (byte)'\"');
				int lastQuote=ArrayUtils.lastIndexOf(charsetRule, (byte)'\"');
				
				if(firstQuote!=lastQuote && firstQuote!=-1 && lastQuote!=-1)
				{
					byte charsetNameBytes[]=ArrayUtils.subarray(charsetRule, firstQuote+2, lastQuote);
					String charsetName=new String(charsetNameBytes, StandardCharsets.UTF_16LE);
					return Charset.forName(charsetName);
				}
			}
			else if(Arrays.equals(UTF16BE_BOM, ArrayUtils.subarray(charsetRule, 0, UTF16BE_BOM.length)))
			{
				// position marker after the bom
				data.reset();
				data.read();
				data.read();
				
				// FE FF
				// UTF-16 BE
				return StandardCharsets.UTF_16BE;
			}
			else if(Arrays.equals(UTF16LE_BOM, ArrayUtils.subarray(charsetRule, 0, UTF16LE_BOM.length)))
			{
				// position marker after the bom
				data.reset();
				data.read();
				data.read();
				
				// FF FE
				// UFT-16 LE
				return StandardCharsets.UTF_16LE;
			}
			
			data.reset();			
		}
		catch(UnsupportedCharsetException | IllegalCharsetNameException | IOException e)
		{
			logger.error("", e);
			throw new BadCharsetException(e);
		}

		
		return null;
	}
}
