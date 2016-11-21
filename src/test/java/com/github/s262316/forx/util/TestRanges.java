package com.github.s262316.forx.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class TestRanges
{
	@Test
	public void testFirstNonConnected1()
	{
		TreeRangeMap<Integer, Integer> rs1=TreeRangeMap.create();
		rs1.put(Range.<Integer>closedOpen(0, 2), 0);
		rs1.put(Range.<Integer>closedOpen(2, 4), 0);
		rs1.put(Range.<Integer>closedOpen(4, 6), 0);
		rs1.put(Range.<Integer>closedOpen(6, 8), 0);
		rs1.put(Range.<Integer>closedOpen(8, 10), 0);
		
		assertEquals(null, Ranges.firstNotConnected(rs1));
	}
	
	public void testFirstNonConnected2()
	{
		TreeRangeMap<Integer, Integer> rs1=TreeRangeMap.create();
		rs1.put(Range.<Integer>closedOpen(2, 4), 0);
		rs1.put(Range.<Integer>closedOpen(8, 10), 0);
		rs1.put(Range.<Integer>closedOpen(4, 6), 0);
		rs1.put(Range.<Integer>closedOpen(0, 2), 0);
		rs1.put(Range.<Integer>closedOpen(6, 8), 0);
		
		assertEquals(null, Ranges.firstNotConnected(rs1));
	}	

	@Test
	public void testFirstNonConnected3()
	{
		TreeRangeMap<Integer, Integer> rs1=TreeRangeMap.create();
		rs1.put(Range.<Integer>closedOpen(0, 2), 0);
		rs1.put(Range.<Integer>closedOpen(2, 4), 0);
		rs1.put(Range.<Integer>closedOpen(4, 6), 0);
		rs1.put(Range.<Integer>closedOpen(8, 10), 0);
		
		assertEquals(Range.<Integer>closedOpen(8, 10), Ranges.firstNotConnected(rs1));
	}
	
	@Test
	public void testFirstNonConnected4()
	{
		TreeRangeMap<Integer, Integer> rs1=TreeRangeMap.create();
		rs1.put(Range.<Integer>closedOpen(8, 10), 0);
		rs1.put(Range.<Integer>closedOpen(0, 2), 0);
		rs1.put(Range.<Integer>closedOpen(2, 4), 0);
		rs1.put(Range.<Integer>closedOpen(4, 6), 0);
		
		assertEquals(Range.<Integer>closedOpen(8, 10), Ranges.firstNotConnected(rs1));
	}	
	
	@Test
	public void testIntersection()
	{
		List<Range<Integer>> rs1=Lists.newArrayList(
				Range.<Integer>closedOpen(0, 2),
				Range.<Integer>closedOpen(2, 3));
		
		assertEquals(true, Ranges.intersection(rs1).isEmpty());
		
		List<Range<Integer>> rs2=Lists.newArrayList(
				Range.<Integer>closedOpen(0, 10),
				Range.<Integer>closedOpen(2, 8));
		
		assertEquals(Range.closedOpen(2, 8), Ranges.intersection(rs2));
		
		List<Range<Integer>> rs3=Lists.newArrayList(
				Range.<Integer>closedOpen(0, 9),
				Range.<Integer>closedOpen(2, 20));
		
		assertEquals(Range.closedOpen(2, 9), Ranges.intersection(rs3));
		
		List<Range<Integer>> rs4=Lists.newArrayList(
				Range.<Integer>closedOpen(5, 9),
				Range.<Integer>closedOpen(2, 7));
		
		assertEquals(Range.closedOpen(5, 7), Ranges.intersection(rs4));		
	}
	
	@Test
	public void testSizeClosed()
	{
		Range<Integer> r1=Range.<Integer>closedOpen(0, 2); // a <= x <= b
		assertEquals(2, Ranges.size(r1));		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSizeOpen()
	{
		Range<Integer> r1=Range.<Integer>open(0, 1);
		Ranges.size(r1);
	}
	
	@Test
	public void testEmptyRange()
	{
		assertEquals(true, Ranges.emptyRange().isEmpty());
	}
	
	@Test
	public void testEncloses1()
	{
		RangeMap<Integer, Boolean> map=ImmutableRangeMap.<Integer, Boolean>builder()
			.put(Range.closedOpen(0, 10), true)
			.put(Range.atLeast(10), true)
			.build();
		
		assertEquals(true, Ranges.encloses(map, Range.closedOpen(5, 6)));
	}

	@Test
	public void testEncloses2()
	{
		RangeMap<Integer, Boolean> map=ImmutableRangeMap.<Integer, Boolean>builder()
			.put(Range.closedOpen(0, 10), true)
			.put(Range.atLeast(11), true)
			.build();
		
		assertEquals(false, Ranges.encloses(map, Range.closedOpen(8, 20)));
	}
	
	
}
