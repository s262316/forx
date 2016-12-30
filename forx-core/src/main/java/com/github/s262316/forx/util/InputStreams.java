package com.github.s262316.forx.util;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

public class InputStreams
{
	public static int indexOf(BufferedInputStream data, byte b) throws IOException
	{
		int BUFFER_SIZE=200;
		byte buffer[]=new byte[BUFFER_SIZE];
		int bytesRead;
		int pos;
		int bufferReadIteration=1;
		
		try
		{
			do
			{
				data.mark(BUFFER_SIZE*bufferReadIteration);
				IOUtils.skip(data, BUFFER_SIZE*(bufferReadIteration-1));
				bytesRead=data.read(buffer);
				
				if(bytesRead==-1)
					return -1;
				
				pos=ArrayUtils.indexOf(buffer, b);
				if(pos!=-1)
					return (BUFFER_SIZE*(bufferReadIteration-1))+pos;
				
				bufferReadIteration++;
				data.reset();
			}
			while(pos==-1);
	
			return -1;
		}
		finally
		{
			if(data!=null)
				data.reset();
		}
	}

}
