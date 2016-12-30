package com.github.s262316.forx.tree.style;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.s262316.forx.tree.style.selectors.Selector;
import com.github.s262316.forx.tree.style.selectors.Specificity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class TestStyleRuleComparator
{
	@Mock
	Selector highSelector;
	@Mock
	Selector lowSelector;
	
	StyleRuleComparator comparator=new StyleRuleComparator();
	
	@Before
	public void setup()
	{
		when(highSelector.getSpecificity()).thenReturn(new Specificity(10, 0, 0, 0));
		when(lowSelector.getSpecificity()).thenReturn(new Specificity(0, 0, 0, 10));
	}
	
	@Test
	public void testLeftSelectorMore()
	{
		StyleRule sr1=Mockito.spy(new StyleRule(highSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 0));
		StyleRule sr2=Mockito.spy(new StyleRule(lowSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 0));
		
		int result=comparator.compare(sr1, sr2);
		assertEquals(1, result);		
		
		verify(sr1, never()).getOrder();
		verify(sr2, never()).getOrder();			
		
	}
	
	@Test
	public void testRightSelectorMore()
	{
		StyleRule sr1=Mockito.spy(new StyleRule(highSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 0));
		StyleRule sr2=Mockito.spy(new StyleRule(lowSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 0));
		
		int result=comparator.compare(sr2, sr1);
		assertEquals(-1, result);	
		
		verify(sr1, never()).getOrder();
		verify(sr2, never()).getOrder();			
	}
	
	@Test
	public void testSameSelectorLeftOrderMore()
	{
		StyleRule sr1=Mockito.spy(new StyleRule(highSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 10));
		StyleRule sr2=Mockito.spy(new StyleRule(highSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 0));
		
		int result=comparator.compare(sr1, sr2);
		assertEquals(1, result);			
		
		verify(sr1).getOrder();
		verify(sr2).getOrder();			
	}

	@Test
	public void testSameSelectorRightOrderMore()
	{
		StyleRule sr1=Mockito.spy(new StyleRule(highSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 0));
		StyleRule sr2=Mockito.spy(new StyleRule(highSelector, ImmutableMap.<String, Declaration>of(), EnumSet.of(MediaType.MT_ALL), 10));
		
		int result=comparator.compare(sr1, sr2);
		assertEquals(-1, result);			

		verify(sr1).getOrder();
		verify(sr2).getOrder();			
	}

}
