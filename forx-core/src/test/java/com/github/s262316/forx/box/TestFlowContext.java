package com.github.s262316.forx.box;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Rectangle;
import java.util.Map;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.s262316.forx.box.util.FloatPosition;

import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;

public class TestFlowContext
{
	// 1 flowArea
	@Test
	public void testGetMetricsNoClear1()
	{
		FlowContext fc=new FlowContext();
		fc.reset(0, 99, 0);

		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)
		
		// in the middle, space at all sides
		assertEquals(new Rectangle(5, 5, 21, 10), fc.metricsNoClear(5, 25, 5, 10, 10));
		// left edge
		assertEquals(new Rectangle(0, 5, 26, 10), fc.metricsNoClear(0, 25, 5, 10, 10));
		// top edge
		assertEquals(new Rectangle(5, 0, 21, 10), fc.metricsNoClear(5, 25, 0, 10, 10));
		// top left edge
		assertEquals(new Rectangle(0, 0, 26, 10), fc.metricsNoClear(0, 25, 0, 10, 10));
		// right edge
		assertEquals(new Rectangle(90, 5, 10, 10), fc.metricsNoClear(90, 99, 5, 10, 10));
		// top right edge
		assertEquals(new Rectangle(90, 0, 10, 10), fc.metricsNoClear(90, 99, 0, 10, 10));
	}
	
	// atLeastWidth/atLeastX/atMostX tests
	@Test
	public void testGetMetricsNoClear2()
	{
		FlowContext fc=new FlowContext();
		fc.reset(0, 99, 0);		
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)		
		
		// atLeastWidth 1 pixel smaller than atLeastX -> atMostX
		assertEquals(new Rectangle(5, 5, 21, 10), fc.metricsNoClear(5, 25, 5, 20, 10));
		
		// atLeastWidth same size as atLeastX -> atMostX
		assertEquals(new Rectangle(5, 5, 21, 10), fc.metricsNoClear(5, 25, 5, 21, 10));
	}
	
	// 2 flowspaces of same size
	@Test
	public void testGetMetricsNoClear3()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 50), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(50), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)

		// in first flow
		assertEquals(new Rectangle(10, 10, 41, 10), fc.metricsNoClear(10, 50, 10, 10, 10));
		// in 2nd flow
		assertEquals(new Rectangle(10, 70, 41, 10), fc.metricsNoClear(10, 50, 70, 10, 10));
		// cross 1st and 2nd flow
		assertEquals(new Rectangle(10, 40, 41, 30), fc.metricsNoClear(10, 50, 40, 10, 30));
		// bottom of first flow
		assertEquals(new Rectangle(10, 40, 41, 10), fc.metricsNoClear(10, 50, 40, 10, 10));
	}
	
	// 1 flowspace starting halfway down
	@Test
	public void testGetMetricsNoClear4()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.atLeast(50), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)

		// ask for space in the void
		assertEquals(new Rectangle(10, 50, 41, 10), fc.metricsNoClear(10, 50, 10, 10, 10));

	}
	
	// 2 flowspaces. disconnected
	@Test
	public void testGetMetricsNoClear5()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 50), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)		
		
		// in 2nd flowspace
		assertEquals(new Rectangle(10, 70, 41, 10), fc.metricsNoClear(10, 50, 70, 10, 10));
		// ask for space from 1st flowspace dropping into void
		assertEquals(new Rectangle(10, 60, 41, 15), fc.metricsNoClear(10, 50, 40, 10, 15));
		// ask for space that overlaps 1st, void 2nd
		assertEquals(new Rectangle(10, 60, 41, 50), fc.metricsNoClear(10, 50, 40, 10, 50));
	}
	
	// following 3 tests are a left, right, both float
	
	// 2 flowspaces. top has narrower width (left float)
	@Test
	public void testGetMetricsNoClear6()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 60), Range.closedOpen(30, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder	

		// top left of area (in the void)
		assertEquals(new Rectangle(30, 0, 70, 10), fc.metricsNoClear(0, 99, 0, 10, 10));
		// top left of top flowspace
		assertEquals(new Rectangle(30, 0, 70, 10), fc.metricsNoClear(30, 99, 0, 10, 10));
		// down to bottom of top flowspace
		assertEquals(new Rectangle(50, 40, 50, 20), fc.metricsNoClear(50, 99, 40, 10, 20));
		// overlaps top and bottom flowspaces. left of void
		assertEquals(new Rectangle(30, 40, 70, 40), fc.metricsNoClear(10, 99, 40, 10, 40));
		// overlaps top and bottom flowspaces. left of top flowspace
		assertEquals(new Rectangle(30, 40, 70, 40), fc.metricsNoClear(30, 99, 40, 10, 40));
		// overlaps top and bottom flowspaces. middle-horiz of top flowspace
		assertEquals(new Rectangle(30, 40, 70, 40), fc.metricsNoClear(20, 99, 40, 10, 40));
	}
	
	// same as above but for a right float
	@Test
	public void testGetMetricsNoClear7()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 60), Range.closedOpen(0, 70));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder	

		// top right of area (in the void). narrow allowed X
		assertEquals(new Rectangle(90, 60, 10, 10), fc.metricsNoClear(90, 99, 0, 10, 10));
		// top right of area (in the void). max allowed X
		assertEquals(new Rectangle(0, 0, 70, 10), fc.metricsNoClear(0, 99, 0, 10, 10));
		// top right of top flowspace
		assertEquals(new Rectangle(60, 0, 10, 10), fc.metricsNoClear(60, 99, 0, 10, 10));
		// down to bottom of top flowspace
		assertEquals(new Rectangle(50, 40, 20, 20), fc.metricsNoClear(50, 99, 40, 10, 20));
		// overlaps top and bottom flowspaces. right of void
		assertEquals(new Rectangle(80, 60, 20, 40), fc.metricsNoClear(80, 99, 40, 10, 40));
		// overlaps top and bottom flowspaces. right of top flowspace
		assertEquals(new Rectangle(30, 40, 40, 40), fc.metricsNoClear(30, 69, 40, 10, 40));
		// overlaps top and bottom flowspaces. middle-horiz of top flowspace
		assertEquals(new Rectangle(20, 40, 50, 40), fc.metricsNoClear(20, 99, 40, 10, 40));
	}
	
	// same as above but for a both float
	@Test
	public void testGetMetricsNoClear8()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 60), Range.closedOpen(30, 70));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder	

		// middle of top flow
		assertEquals(new Rectangle(40, 0, 21, 10), fc.metricsNoClear(40, 60, 0, 10, 10));
		// middle of bottom flow
		assertEquals(new Rectangle(40, 70, 21, 10), fc.metricsNoClear(40, 60, 70, 10, 10));
		// overlapping top+bottom flow. no void asked for
		assertEquals(new Rectangle(40, 0, 21, 100), fc.metricsNoClear(40, 60, 0, 10, 100));
		// ask for big space covering all flows. answer=pushed down to infinite flow
		assertEquals(new Rectangle(10, 60, 81, 100), fc.metricsNoClear(10, 90, 10, 80, 100));
		// ask for small space covering all flows. answer=fits in the top+bottom flows
		assertEquals(new Rectangle(40, 10, 21, 100), fc.metricsNoClear(40, 60, 10, 10, 100));
	}

	// 2 flowspaces. top is narrower. float on left. void space between the two flowspaces
	@Test
	public void testGetMetricsNoClear9()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 60), Range.closedOpen(30, 100));
		flowAreas.put(Range.atLeast(70), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder	

		// all in top-left void area
		assertEquals(new Rectangle(0, 70, 31, 10), fc.metricsNoClear(0, 30, 0, 10, 10));
		// spans top. left in void area. any space acceptable
		assertEquals(new Rectangle(30, 0, 70, 10), fc.metricsNoClear(0, 99, 0, 10, 10));
		// spans top. left in void area. big space only acceptable
		assertEquals(new Rectangle(0, 70, 100, 10), fc.metricsNoClear(0, 99, 0, 90, 10));
		// spans top+bottom. horiz not in top flow
		assertEquals(new Rectangle(0, 70, 36, 10), fc.metricsNoClear(0, 35, 0, 10, 10));
		// spans top+bottom. horiz in top flow and void
		assertEquals(new Rectangle(0, 70, 91, 100), fc.metricsNoClear(0, 90, 0, 10, 100));
		// spans top+bottom. horiz in top flow
		assertEquals(new Rectangle(40, 0, 60, 10), fc.metricsNoClear(40, 99, 0, 10, 10));
		// in bottom only
		assertEquals(new Rectangle(0, 100, 100, 10), fc.metricsNoClear(0, 99, 100, 10, 10));
	}
	
	// 2 flowspaces. top=full width, bottom=half-width
	// right float
	@Test
	public void testGetMetricsNoClear10()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 60), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(60, 80), Range.closedOpen(0, 40));
		flowAreas.put(Range.atLeast(80), Range.closedOpen(0, 100));
		
		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder	
		
		// max-width range, small minWith, adjoining top 2 ranges
		assertEquals(new Rectangle(0, 55, 40, 20), fc.metricsNoClear(0, 99, 55, 20, 20));
		// max-range range, large minWidth, adjoining top 2 ranges
		assertEquals(new Rectangle(0, 80, 100, 20), fc.metricsNoClear(0, 99, 55, 80, 20));
		// small-width range, small minWith, adjoining top 2 ranges
		assertEquals(new Rectangle(30, 80, 21, 20), fc.metricsNoClear(30, 50, 55, 20, 20));
	}
	
	// test horiz-overlapping left and rights with max-width flow between
	@Test
	public void testGetMetricsNoClear11()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 100);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(80, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 20));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));

		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder	

		// overlap top void, top flow with full flow
		assertEquals(new Rectangle(70, 20, 21, 10), fc.metricsNoClear(70, 90, 18, 20, 10));
		// overlap full flow, bottom flow, bottom void
		assertEquals(new Rectangle(18, 60, 11, 10), fc.metricsNoClear(18, 28, 38, 10, 10));
		// overlap top, middle, bottom flows+voids
		assertEquals(new Rectangle(10, 60, 81, 100), fc.metricsNoClear(10, 90, 5, 80, 100));
	}

	@Test
	public void testGetMetricsNoClear12()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_right", 381);
		flowAreas.put(Range.atLeast(0), Range.closedOpen(0, 382));

		//getMetricsNoClear(int atLeastX, int atMostX, int atLeastY, int atLeastWidth, int height)	signature reminder
		try
		{

			 fc.metricsNoClear(16, 21, 34, 38, 23);
		}
		catch(IllegalArgumentException iae)
		{
			assertEquals("requested width (38) does not fit into least/most (16-21) constraints", iae.getMessage());
		}
	}
	
	@Test
	public void testGetMetricsNoClearInvalidArgs()
	{
		FlowContext fc=new FlowContext();
		ReflectionTestUtils.setField(fc, "_right", 100);

		try
		{
			fc.metricsNoClear(10, 0, 0, 10, 20);
			fail();
		}
		catch(IllegalArgumentException iae)
		{
			assertEquals("atLeastX (10) value is greater than atMostX value (0)", iae.getMessage());
		}

		try
		{
			fc.metricsNoClear(0, 10, 0, -10, 20);
			fail();
		}
		catch(IllegalArgumentException iae)
		{
			assertEquals("atLeastWidth value (-10) is 0 or less", iae.getMessage());
		}

		try
		{
			fc.metricsNoClear(0, 10, 0, 10, -20);
			fail();
		}
		catch(IllegalArgumentException iae)
		{
			assertEquals("height value (-20) is 0 or less", iae.getMessage());
		}
	}
	
	// 4 flows all at the left
	@Test
	public void testHighestFlowLeftClear1()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_left_clear(0));
		assertEquals(19, fc.highest_flow_left_clear(19));
		assertEquals(20, fc.highest_flow_left_clear(20));
	}

	// 4 flows, 1 not at left
	@Test
	public void testHighestFlowLeftClear2()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(50, 100));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_left_clear(0));
		assertEquals(19, fc.highest_flow_left_clear(19));
		assertEquals(40, fc.highest_flow_left_clear(20));
		assertEquals(40, fc.highest_flow_left_clear(39));
		assertEquals(40, fc.highest_flow_left_clear(40));
	}

	// 4 flows, 2 not at left
	@Test
	public void testHighestFlowLeftClear3()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(50, 100));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(50, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_left_clear(0));
		assertEquals(19, fc.highest_flow_left_clear(19));
		assertEquals(60, fc.highest_flow_left_clear(20));
		assertEquals(60, fc.highest_flow_left_clear(39));
		assertEquals(60, fc.highest_flow_left_clear(40));
		assertEquals(60, fc.highest_flow_left_clear(59));
		assertEquals(60, fc.highest_flow_left_clear(60));
	}

	// 4 flows all at the right
	@Test
	public void testHighestFlowRightClear1()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_right_clear(0));
		assertEquals(19, fc.highest_flow_right_clear(19));
		assertEquals(20, fc.highest_flow_right_clear(20));
	}

	// 4 flows, 1 not at right
	@Test
	public void testHighestFlowRightClear2()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 50));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_right_clear(0));
		assertEquals(19, fc.highest_flow_right_clear(19));
		assertEquals(40, fc.highest_flow_right_clear(20));
		assertEquals(40, fc.highest_flow_right_clear(39));
		assertEquals(40, fc.highest_flow_right_clear(40));
	}

	// 4 flows, 2 not at right
	@Test
	public void testHighestFlowRightClear3()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 50));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 50));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_right_clear(0));
		assertEquals(19, fc.highest_flow_right_clear(19));
		assertEquals(60, fc.highest_flow_right_clear(20));
		assertEquals(60, fc.highest_flow_right_clear(39));
		assertEquals(60, fc.highest_flow_right_clear(40));
		assertEquals(60, fc.highest_flow_right_clear(59));
		assertEquals(60, fc.highest_flow_right_clear(60));
	}
	
	// 4 flows all both cleared
	@Test
	public void testHighestFlowBothClear1()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		
		
		assertEquals(0, fc.highest_flow_both_clear(0));
		assertEquals(19, fc.highest_flow_both_clear(19));
		assertEquals(20, fc.highest_flow_both_clear(20));		
	}
	
	// 4 flows left or right cleared
	@Test
	public void testHighestFlowBothClear2()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 50));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(50, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		

		assertEquals(0, fc.highest_flow_both_clear(0));
		assertEquals(19, fc.highest_flow_both_clear(19));
		assertEquals(60, fc.highest_flow_both_clear(20));			
		assertEquals(60, fc.highest_flow_both_clear(39));			
		assertEquals(60, fc.highest_flow_both_clear(40));			
		assertEquals(60, fc.highest_flow_both_clear(59));
		assertEquals(60, fc.highest_flow_both_clear(60));
	}
	
	// 4 flows left or right or both cleared
	@Test
	public void testHighestFlowBothClear3()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(20, 80));
		flowAreas.put(Range.closedOpen(20, 40), Range.closedOpen(0, 50));
		flowAreas.put(Range.closedOpen(40, 60), Range.closedOpen(50, 100));
		flowAreas.put(Range.atLeast(60), Range.closedOpen(0, 100));		

		assertEquals(60, fc.highest_flow_both_clear(0));
		assertEquals(60, fc.highest_flow_both_clear(19));
		assertEquals(60, fc.highest_flow_both_clear(20));			
		assertEquals(60, fc.highest_flow_both_clear(39));			
		assertEquals(60, fc.highest_flow_both_clear(40));			
		assertEquals(60, fc.highest_flow_both_clear(59));
		assertEquals(60, fc.highest_flow_both_clear(60));
	}	
	
	@Test
	public void testNoResetCalled()
	{
		FlowContext fc=new FlowContext();
		
		try
		{
			fc.highest_flow_both_clear(0);
			fail();
		}
		catch(IllegalStateException ise)
		{
			assertEquals("reset() must be called before using FlowContext", ise.getMessage());
		}
		try
		{
			fc.highest_flow_left_clear(0);
			fail();
		}
		catch(IllegalStateException ise)
		{
			assertEquals("reset() must be called before using FlowContext", ise.getMessage());
		}
		try
		{
			fc.highest_flow_right_clear(0);
			fail();
		}
		catch(IllegalStateException ise)
		{
			assertEquals("reset() must be called before using FlowContext", ise.getMessage());
		}		
		try
		{
			fc.metricsNoClear(0, 0, 0, 0, 0);
			fail();
		}
		catch(IllegalStateException ise)
		{
			assertEquals("reset() must be called before using FlowContext", ise.getMessage());
		}		
		try
		{		
			fc.takeout_flow_area(0, 0, 0, FloatPosition.LEFT);
			fail();
		}
		catch(IllegalStateException ise)
		{
			assertEquals("reset() must be called before using FlowContext", ise.getMessage());
		}		
	}
	
	@Test
	public void testTakeoutFlowAreaFullWidthLeftAlign()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(0, 19, 99, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs1=flowAreas.asMapOfRanges();
		assertEquals(1, rs1.size());
		assertEquals(Range.atLeast(20), Iterables.get(rs1.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs1.entrySet(), 0).getValue());
	}
	
	@Test
	public void testTakeoutFlowAreaFullWidthRightAlign()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(0, 19, 0, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs1=flowAreas.asMapOfRanges();
		assertEquals(1, rs1.size());
		assertEquals(Range.atLeast(20), Iterables.get(rs1.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs1.entrySet(), 0).getValue());
	}
	
	@Test
	public void testTakeoutFlowAreaTopFullWidthLeftAlign()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(0, 9, 99, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(10, 20), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}
	
	@Test
	public void testTakeoutFlowAreaTopFullWidthRightAlign()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(0, 9, 0, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(10, 20), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}	

	@Test
	public void testTakeoutFlowAreaBottomWidthLeftAlign()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(10, 19, 99, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(0, 10), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}
	
	@Test
	public void testTakeoutFlowAreaBottomWidthRightAlign()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(10, 19, 0, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(0, 10), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}	
	
	@Test
	public void testTakeoutFlowAreaFullLeft()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(0, 19, 5, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(0, 20), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(6, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}

	@Test
	public void testTakeoutFlowAreaFullRight()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));	
		
		fc.takeout_flow_area(0, 19, 15, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(0, 20), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 15), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}

	@Test
	public void testTakeoutFlowAreaTopLeft()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));

		fc.takeout_flow_area(0, 10, 15, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(3, rs.size());
		assertEquals(Range.closedOpen(0, 11), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(16, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(11, 20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());
	}

	@Test
	public void testTakeoutFlowAreaTopLeftInfinite()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.atLeast(0), Range.closedOpen(0, 100));

		fc.takeout_flow_area(0, 19, 15, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(0, 20), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(16, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
	}
	
	
	@Test
	public void testTakeoutFlowAreaTopRight()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));

		fc.takeout_flow_area(0, 10, 15, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(3, rs.size());
		assertEquals(Range.closedOpen(0, 11), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 15), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(11, 20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());
	}	
	
	@Test
	public void testTakeoutFlowAreaTopRightInfinite()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.atLeast(0), Range.closedOpen(0, 100));

		fc.takeout_flow_area(0, 19, 15, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(2, rs.size());
		assertEquals(Range.closedOpen(0, 20), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 15), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 1).getValue());		
	}		

	@Test
	public void testTakeoutFlowAreaBottomLeft()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));

		fc.takeout_flow_area(10, 19, 15, FloatPosition.LEFT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(3, rs.size());
		assertEquals(Range.closedOpen(0, 10), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(10, 20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(16, 100), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());		
	}	
	
	@Test
	public void testTakeoutFlowAreaBottomRight()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));

		fc.takeout_flow_area(10, 19, 15, FloatPosition.RIGHT);
		
		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(3, rs.size());
		assertEquals(Range.closedOpen(0, 10), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(10, 20), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 15), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());			
	}	

	@Test
	public void testTakeoutFlowAreaMiddleLeft()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));

		fc.takeout_flow_area(5, 15, 15, FloatPosition.LEFT);

		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(4, rs.size());
		assertEquals(Range.closedOpen(0, 5), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(5, 16), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(16, 100), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.closedOpen(16, 20), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 3).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 3).getValue());
	}

	@Test
	public void testTakeoutFlowAreaMiddleLeftInfinity()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.atLeast(0), Range.closedOpen(0, 100));

		fc.takeout_flow_area(30, 50, 25, FloatPosition.LEFT);

		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(3, rs.size());
		assertEquals(Range.closedOpen(0, 30), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(30, 51), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(26, 100), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.atLeast(51), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());		
	}	
	
	@Test
	public void testTakeoutFlowAreaMiddleRight()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.closedOpen(0, 20), Range.closedOpen(0, 100));
		flowAreas.put(Range.atLeast(20), Range.closedOpen(0, 100));

		fc.takeout_flow_area(5, 15, 15, FloatPosition.RIGHT);

		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(4, rs.size());
		assertEquals(Range.closedOpen(0, 5), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(5, 16), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 15), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.closedOpen(16, 20), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());
		assertEquals(Range.atLeast(20), Iterables.get(rs.entrySet(), 3).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 3).getValue());				
	}

	@Test
	public void testTakeoutFlowAreaMiddleRightInfinity()
	{
		FlowContext fc=new FlowContext();
		RangeMap<Integer, Range<Integer>> flowAreas=(RangeMap)ReflectionTestUtils.getField(fc, "flowAreas");
		ReflectionTestUtils.setField(fc, "_left", 0);
		ReflectionTestUtils.setField(fc, "_right", 99);
		flowAreas.put(Range.atLeast(0), Range.closedOpen(0, 100));

		fc.takeout_flow_area(30, 50, 25, FloatPosition.RIGHT);

		Map<Range<Integer>, Range<Integer>> rs=flowAreas.asMapOfRanges();
		assertEquals(3, rs.size());
		assertEquals(Range.closedOpen(0, 30), Iterables.get(rs.entrySet(), 0).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 0).getValue());
		assertEquals(Range.closedOpen(30, 51), Iterables.get(rs.entrySet(), 1).getKey());
		assertEquals(Range.closedOpen(0, 25), Iterables.get(rs.entrySet(), 1).getValue());
		assertEquals(Range.atLeast(51), Iterables.get(rs.entrySet(), 2).getKey());
		assertEquals(Range.closedOpen(0, 100), Iterables.get(rs.entrySet(), 2).getValue());			
	}
	
}





