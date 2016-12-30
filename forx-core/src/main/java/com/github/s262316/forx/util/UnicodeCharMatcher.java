package com.github.s262316.forx.util;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.Ints;

public class UnicodeCharMatcher extends CharMatcher
{
	private int categories[];
	
	public UnicodeCharMatcher(int... categories)
	{
		this.categories=categories;
	}
	
	@Override
	public boolean matches(char c)
	{
		int category=Character.getType(c);
		return Ints.contains(categories, category);
	}
}
