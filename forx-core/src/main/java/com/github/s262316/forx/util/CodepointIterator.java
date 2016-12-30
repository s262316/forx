package com.github.s262316.forx.util;

import com.google.common.collect.AbstractIterator;

public class CodepointIterator extends AbstractIterator<Integer>
{
	private String text;
	private int currentPosition=0;
	
	public CodepointIterator(String text)
	{
		this.text=text;
	}

	@Override
	protected Integer computeNext()
	{
		if(currentPosition>=text.length())
			return endOfData();
		
		int codepoint=text.codePointAt(currentPosition);
		
		currentPosition+=Character.charCount(codepoint);
		
		return codepoint;
	}
}
