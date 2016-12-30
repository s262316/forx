package com.github.s262316.forx.util;

import java.util.Map;

public class Maps2
{
	public static <K, V> void setAll(Map<K, V> map, V to)
	{
		for(Map.Entry<K, V> e : map.entrySet())
			e.setValue(to);
	}
	
}
