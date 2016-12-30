package com.github.s262316.forx.util;

import java.util.LinkedHashSet;

public class Sets2
{
	public static <E> LinkedHashSet<E> newLinkedHashSet(E element)
	{
		LinkedHashSet<E> set=new LinkedHashSet<>();
		set.add(element);
		return set;
	}
}
