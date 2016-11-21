package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.s262316.forx.gui.CounterAction;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class TestCounterActions
{
	private Value counterPropertyValue;
	private int defaultAmount;
	private List<CounterAction> expectedResult;

	@Parameters
	public static Collection<Object[]> parameters()
	{
		return Lists.newArrayList(
				new Object[]{vl("mycounter", 1), 1, Lists.newArrayList(new CounterAction("mycounter", 1))},
				new Object[]{vl("mycounter", 1), 0, Lists.newArrayList(new CounterAction("mycounter", 1))},
				new Object[]{vl("mycounter"), 12, Lists.newArrayList(new CounterAction("mycounter", 12))},
				new Object[]{vl(), 12, Lists.newArrayList()},
				new Object[]{vl("mycounter", 1, "myothercounter", 2), 0, Lists.newArrayList(new CounterAction("mycounter", 1), (new CounterAction("myothercounter", 2)))},
				new Object[]{vl("mycounter", "myothercounter", 2), 14, Lists.newArrayList(new CounterAction("mycounter", 14), (new CounterAction("myothercounter", 2)))},
				new Object[]{vl("mycounter", 1, "myothercounter"), 14, Lists.newArrayList(new CounterAction("mycounter", 1), (new CounterAction("myothercounter", 14)))},
				new Object[]{vl("none"), 12, Lists.newArrayList()},
				new Object[]{vl("none", 1), 12, Lists.newArrayList()},
				new Object[]{vl(1, "none"), 12, Lists.newArrayList()}
				);
	}
	
	public static ValueList vl(String counterName, int defaultAmount)
	{
		ValueList vl=new ValueList();
		vl.members.add(new Identifier(counterName));
		vl.members.add(new NumericValue(defaultAmount, ""));
		return vl;
	}

	public static ValueList vl(int defaultAmount, String counterName)
	{
		ValueList vl=new ValueList();
		vl.members.add(new NumericValue(defaultAmount, ""));
		vl.members.add(new Identifier(counterName));
		return vl;
	}	
	
	public static ValueList vl(String counterName1, int defaultAmount1, String counterName2, int defaultAmount2)
	{
		ValueList vl=new ValueList();
		vl.members.add(new Identifier(counterName1));
		vl.members.add(new NumericValue(defaultAmount1, ""));
		vl.members.add(new Identifier(counterName2));
		vl.members.add(new NumericValue(defaultAmount2, ""));
		return vl;
	}	
	
	public static ValueList vl(String counterName1, int defaultAmount1, String counterName2)
	{
		ValueList vl=new ValueList();
		vl.members.add(new Identifier(counterName1));
		vl.members.add(new NumericValue(defaultAmount1, ""));
		vl.members.add(new Identifier(counterName2));
		return vl;
	}		
	
	public static ValueList vl(String counterName1, String counterName2, int defaultAmount2)
	{
		ValueList vl=new ValueList();
		vl.members.add(new Identifier(counterName1));
		vl.members.add(new Identifier(counterName2));
		vl.members.add(new NumericValue(defaultAmount2, ""));
		return vl;
	}		
	
	public static ValueList vl(String counterName)
	{
		ValueList vl=new ValueList();
		vl.members.add(new Identifier(counterName));
		return vl;
	}
	
	public static ValueList vl()
	{
		ValueList vl=new ValueList();
		return vl;
	}	
	
	public TestCounterActions(Value counterPropertyValue, int defaultAmount, List<CounterAction> expectedResult)
	{
		this.counterPropertyValue=counterPropertyValue;
		this.defaultAmount=defaultAmount;
		this.expectedResult=expectedResult;
	}
	
	@Test
	public void testCounterActions()
	{
		assertEquals(expectedResult, Counters.counterActions(counterPropertyValue, defaultAmount));
	}
	
	
}
