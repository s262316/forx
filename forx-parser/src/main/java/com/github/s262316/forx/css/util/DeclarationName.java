package com.github.s262316.forx.css.util;

import com.github.s262316.forx.style.Declaration;

import com.google.common.base.Function;

public class DeclarationName implements Function<Declaration, String>
{
	@Override
	public String apply(Declaration input)
	{
		return input.getProperty();
	}
}
