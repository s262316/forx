package com.github.s262316.forx.util;

import java.util.HashMap;
import java.util.Map;

import com.github.s262316.forx.tree.style.Value;

import com.google.common.base.Predicates;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class InferenceTable2
{
	public static Map<String, Value> infer(HashBasedTable<Value, String, Boolean> table)
	{
		Map<String, Value> propertyToValue=new HashMap<>();
		int matchedThisPass;
		int passes=0;
		
		do
		{
			matchedThisPass=0;
			
			for(String propertyName : table.columnKeySet())
			{
				Map<Value, Boolean> col=table.column(propertyName);
				if(Iterables.frequency(col.values(), true)==1)
				{
					Map<Value, Boolean> filtered=Maps.filterValues(col, Predicates.equalTo(true));
					Value matchedValue=Iterables.getOnlyElement(filtered.entrySet()).getKey();
					
					propertyToValue.put(propertyName, matchedValue);
					
					// set all values to false in the column
					Maps2.setAll(col, false);
					// set all values to false in the row
					Maps2.setAll(table.row(matchedValue), false);
					
					matchedThisPass++;
				}
			}
			
			passes++;
		}
		while(matchedThisPass!=0 && Iterables.frequency(table.values(), true)!=0);

		return propertyToValue;
	}
}

