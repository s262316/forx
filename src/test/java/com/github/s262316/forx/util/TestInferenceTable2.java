package com.github.s262316.forx.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.HashBasedTable;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.Value;

public class TestInferenceTable2
{
	/*
	 * each property has 1 possible match
	 * 
	      | width | colour | style
	red   |   0   |   1    |   0
	solid |   0   |   0    |   1
	thin  |   1   |   0    |   0
	*/	
	@Test
	public void test1PossibleMatch()
	{
		HashBasedTable<Value, String, Boolean> table=HashBasedTable.create();
		table.put(new Identifier("thin"), "border-top-width", true);
		table.put(new Identifier("red"), "border-top-width", false);
		table.put(new Identifier("solid"), "border-top-width", false);
		table.put(new Identifier("thin"), "border-top-style", false);
		table.put(new Identifier("red"), "border-top-style", false);
		table.put(new Identifier("solid"), "border-top-style", true);
		table.put(new Identifier("thin"), "border-top-color", false);
		table.put(new Identifier("red"), "border-top-color", true);
		table.put(new Identifier("solid"), "border-top-color", false);
		
		Map<String, Value> map=InferenceTable2.infer(table);
		assertEquals(3, map.size());
		assertEquals(new Identifier("thin"), map.get("border-top-width"));
		assertEquals(new Identifier("solid"), map.get("border-top-style"));
		assertEquals(new Identifier("red"), map.get("border-top-color"));
	}
	
	/*
	 * colour has 3 possible matches
	 * 
	      | width | colour | style
	red   |   0   |   1    |   0
	solid |   0   |   1    |   1
	thin  |   1   |   1    |   0
	*/
	@Test
	public void test3PossibleMatches()
	{
		// (made up inferences)
		HashBasedTable<Value, String, Boolean> table=HashBasedTable.create();
		table.put(new Identifier("thin"), "border-top-width", true);
		table.put(new Identifier("red"), "border-top-width", false);
		table.put(new Identifier("solid"), "border-top-width", false);
		table.put(new Identifier("thin"), "border-top-style", false);
		table.put(new Identifier("red"), "border-top-style", false);
		table.put(new Identifier("solid"), "border-top-style", true);
		table.put(new Identifier("thin"), "border-top-color", true);
		table.put(new Identifier("red"), "border-top-color", true);
		table.put(new Identifier("solid"), "border-top-color", true);

		Map<String, Value> map=InferenceTable2.infer(table);
		assertEquals(3, map.size());
		assertEquals(new Identifier("thin"), map.get("border-top-width"));
		assertEquals(new Identifier("solid"), map.get("border-top-style"));
		assertEquals(new Identifier("red"), map.get("border-top-color"));
	}
	
	@Test
	public void testNotEveryKeyHasAMatch()
	{
		// (made up inferences)
		HashBasedTable<Value, String, Boolean> table=HashBasedTable.create();
		table.put(new Identifier("thin"), "border-top-width", true);
		table.put(new Identifier("red"), "border-top-width", false);
		table.put(new Identifier("solid"), "border-top-width", false);
		table.put(new Identifier("thin"), "border-top-style", false);
		table.put(new Identifier("red"), "border-top-style", false);
		table.put(new Identifier("solid"), "border-top-style", true);
		table.put(new Identifier("thin"), "border-top-color", false);
		table.put(new Identifier("red"), "border-top-color", false);
		table.put(new Identifier("solid"), "border-top-color", false);

		Map<String, Value> map=InferenceTable2.infer(table);
		assertEquals(2, map.size());
		assertEquals(new Identifier("thin"), map.get("border-top-width"));
		assertEquals(new Identifier("solid"), map.get("border-top-style"));
	}
	
}
