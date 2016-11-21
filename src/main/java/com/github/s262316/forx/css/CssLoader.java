package com.github.s262316.forx.css;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import com.google.common.net.MediaType;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.ArrayUtils;
import com.github.s262316.forx.net.Resource;
import com.github.s262316.forx.net.ResourceLoader;
import com.github.s262316.forx.tree.LinkingMechanism;
import com.github.s262316.forx.tree.ReferringDocument;

public class CssLoader implements ResourceLoader
{
	private CssCharset cssCharset=new CssCharset();

	@Override
	public Resource load(String url, ReferringDocument referrer, LinkingMechanism link) throws IOException
	{
		Charset charset;

		URL resourceToLoad=new URL(referrer.getLocation(), url);
		URLConnection connection=resourceToLoad.openConnection();
		BufferedInputStream bufferedStream=new BufferedInputStream(connection.getInputStream());

		bufferedStream.mark(160); // think 144 is the longest
		byte charsetBuffer[]=new byte[160];
		int bytesRead=bufferedStream.read(charsetBuffer);
		charset=calculateCharset(connection, ArrayUtils.subarray(charsetBuffer, 0, bytesRead), link, referrer);
		bufferedStream.reset();

		return new Resource(charset,
				new InputStreamReader(new BOMInputStream(bufferedStream), charset),
				resourceToLoad);
	}
	
	private Charset calculateCharset(URLConnection entity, byte buffer[], LinkingMechanism link, ReferringDocument referrer) throws IOException
	{
		Charset charset;

		try
		{
			// 1. An HTTP "charset" parameter in a "Content-Type" field (or similar parameters in other protocols)
			String contentTypeHeader=entity.getContentType();
			if(contentTypeHeader!=null)
			{
				MediaType mt= MediaType.parse(contentTypeHeader);
				if(mt.charset().isPresent())
					return mt.charset().get();
			}
		}
		catch(UnsupportedCharsetException e)
		{
		} // fallback to the next 
		
		try
		{
			// 2. BOM and/or @charset
			charset=cssCharset.sniffCharset(new BufferedInputStream(new ByteArrayInputStream(buffer)));
		}
		catch(BadCharsetException e)
		{} // fallback to the next 

		// 3. <link charset=""> or other metadata from the linking mechanism (if any)
		// 4. charset of referring style sheet or document (if any)
		// 5. Assume UTF-8
		charset=link.getCharset()
			.or(referrer.getCharset())
			.or(StandardCharsets.UTF_8);
		
		return charset;
	}
}
