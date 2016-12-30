package com.github.s262316.forx.tree;

import java.net.URL;

public class ResourceResolver
{
	public static URL resolve(URL base, String loc) throws Exception
	{
		return new URL(base, loc);
	}
}
