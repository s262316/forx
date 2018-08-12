package com.github.s262316.forx.tree.visual;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.tree.visual.Counters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;
import com.github.s262316.forx.tree.visual.VElement;

@RunWith(MockitoJUnitRunner.class)
public class TestCounters
{
	@Mock
	VElement velement;
	
	@Test
	public void testHandleCounters1()
	{
		Value resetValue=new Identifier("mycounter");
		Value incrementValue=new Identifier("mycounter");
		Mockito.when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		Mockito.when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		Mockito.when(velement.find_counter(Matchers.anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		Mockito.verify(velement, Mockito.times(1)).reset_counter("mycounter", 0);
		Mockito.verify(velement, Mockito.times(1)).inc_counter("mycounter", 1);
	}

	@Test
	public void testHandleCounters2()
	{
		ValueList resetValue=vl("mycounter", 1, "mycounter2", 2);
		Value incrementValue=vl("mycounter", 1, "mycounter2", 2);
		Mockito.when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		Mockito.when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		Mockito.when(velement.find_counter(Matchers.anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		Mockito.verify(velement, Mockito.times(1)).reset_counter("mycounter", 1);
		Mockito.verify(velement, Mockito.times(1)).reset_counter("mycounter2", 2);
		Mockito.verify(velement, Mockito.times(1)).inc_counter("mycounter", 1);
		Mockito.verify(velement, Mockito.times(1)).inc_counter("mycounter2", 2);
	}

	@Test
	public void testHandleCounters3()
	{
		ValueList resetValue=vl("mycounter", 1, "mycounter2");
		Value incrementValue=vl("mycounter", "mycounter2", 2);
		Mockito.when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		Mockito.when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		Mockito.when(velement.find_counter(Matchers.anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		Mockito.verify(velement, Mockito.times(1)).reset_counter("mycounter", 1);
		Mockito.verify(velement, Mockito.times(1)).reset_counter("mycounter2", 0);
		Mockito.verify(velement, Mockito.times(1)).inc_counter("mycounter", 1);
		Mockito.verify(velement, Mockito.times(1)).inc_counter("mycounter2", 2);
	}
	
	@Test
	public void testHandleCountersNone()
	{
		Value resetValue=new Identifier("none");
		Value incrementValue=new Identifier("none");
		Mockito.when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		Mockito.when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		Mockito.when(velement.find_counter(Matchers.anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		Mockito.verify(velement, Mockito.never()).reset_counter(Matchers.anyString(), Matchers.anyInt());
		Mockito.verify(velement, Mockito.never()).inc_counter(Matchers.anyString(), Matchers.anyInt());
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

}



