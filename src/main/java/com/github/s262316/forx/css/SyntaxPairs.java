package com.github.s262316.forx.css;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class SyntaxPairs
{
	private static final List<String> OPEN_PAIRS=ImmutableList.of("(", "[", "{");
	private static final List<String> CLOSE_PAIRS=ImmutableList.of("}", "]", ")");
	
	public List<String> openPairs()
	{
		return OPEN_PAIRS;
	}
	
	public List<String> closedPairs()
	{
		return CLOSE_PAIRS;
	}
	
	public String oppositeOf(String syntax)
	{
		switch(syntax)
		{
		case "(":
			return ")";
		case "[":
			return "]";
		case "{":
			return "}";
		case ")":
			return "(";
		case "]":
			return "[";
		case "}":
			return "{";
		}
		
		throw new IllegalArgumentException("not a syntax pair: "+syntax);
	}
	
}
