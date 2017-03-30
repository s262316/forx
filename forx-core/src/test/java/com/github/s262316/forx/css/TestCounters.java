package com.github.s262316.forx.css;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
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
		when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		when(velement.find_counter(anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		verify(velement, times(1)).reset_counter("mycounter", 0);
		verify(velement, times(1)).inc_counter("mycounter", 1);
	}

	@Test
	public void testHandleCounters2()
	{
		ValueList resetValue=TestCounterActions.vl("mycounter", 1, "mycounter2", 2);
		Value incrementValue=TestCounterActions.vl("mycounter", 1, "mycounter2", 2);
		when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		when(velement.find_counter(anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		verify(velement, times(1)).reset_counter("mycounter", 1);
		verify(velement, times(1)).reset_counter("mycounter2", 2);
		verify(velement, times(1)).inc_counter("mycounter", 1);
		verify(velement, times(1)).inc_counter("mycounter2", 2);
	}

	@Test
	public void testHandleCounters3()
	{
		ValueList resetValue=TestCounterActions.vl("mycounter", 1, "mycounter2");
		Value incrementValue=TestCounterActions.vl("mycounter", "mycounter2", 2);
		when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		when(velement.find_counter(anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		verify(velement, times(1)).reset_counter("mycounter", 1);
		verify(velement, times(1)).reset_counter("mycounter2", 0);
		verify(velement, times(1)).inc_counter("mycounter", 1);
		verify(velement, times(1)).inc_counter("mycounter2", 2);
	}
	
	@Test
	public void testHandleCountersNone()
	{
		Value resetValue=new Identifier("none");
		Value incrementValue=new Identifier("none");
		when(velement.getPropertyValue("counter-reset", MediaType.MT_SCREEN)).thenReturn(resetValue);
		when(velement.getPropertyValue("counter-increment", MediaType.MT_SCREEN)).thenReturn(incrementValue);
		when(velement.find_counter(anyString())).thenReturn(velement);
		
		Counters.handleCounters(velement);
		
		verify(velement, never()).reset_counter(anyString(), anyInt());
		verify(velement, never()).inc_counter(anyString(), anyInt());
	}
}

