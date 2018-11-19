package com.github.s262316.forx.box.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.newbox.AnonReason;

@RunWith(MockitoJUnitRunner.class)
public class BoxesTest
{
	@Mock
	BlockBox subject;
	@Mock
	BlockBox childBlock;
	@Mock
	InlineBox anonInlineContainer;
	@Mock
	InlineBox inlineBox;
	@Mock
	Visual visual;
	
	@Test
	public void testGetLastFlowMemberAnAnonInlineContainerEmpty()
	{
		// empty
		assertFalse(
				Boxes.getLastFlowMemberAnAnonInlineContainer(subject).isPresent());
	}
	
	@Test
	public void testGetLastFlowMemberAnAnonInlineContainerEmptyWhenLastIsNotInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(childBlock));
		
		// last is not an anon inline container
		assertFalse(
				Boxes.getLastFlowMemberAnAnonInlineContainer(subject).isPresent());
	}
	
	@Test
	public void testGetLastFlowMemberAnAnonInlineContainerEmptyWhenLastIsInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(anonInlineContainer));
		when(anonInlineContainer.getVisual()).thenReturn(visual);
		when(visual.getAnonReason()).thenReturn(AnonReason.INLINE_CONTAINER);
		
		// last is anon inline container
		assertTrue(
				Boxes.getLastFlowMemberAnAnonInlineContainer(subject).isPresent());
	}
	
	@Test
	public void testGetLastFlowMemberAnAnonInlineContainerEmptyWhenLastIsInlineBox()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(anonInlineContainer));
		when(anonInlineContainer.getVisual()).thenReturn(visual);
		when(visual.getAnonReason()).thenReturn(null);
		
		// last is inlinebox (but not anon)
		assertFalse(
				Boxes.getLastFlowMemberAnAnonInlineContainer(subject).isPresent());
	}
}
