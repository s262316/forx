package com.github.s262316.forx.tree.visual;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.box.AtomicInline;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.properties.Visual;

@RunWith(MockitoJUnitRunner.class)
public class BlockBoxParentLocatorTest
{
	@Mock
	BlockBox subject;
	@Mock
	BlockBox childBlock;
	@Mock
	InlineBox childInlineBox;
	@Mock
	InlineBox anonInlineContainer;
	@Mock
	AtomicInline atomicInline;
	@Mock
	Visual visual1;
	@Mock
	Visual visual2;
	
	@Test
	public void locateWhenAddingBlockBoxReturnsSubject()
	{
		BlockBoxParentLocator locator=new BlockBoxParentLocator(subject, visual1);
		assertEquals(subject, locator.locate(childBlock));
	}
	
	@Test
	public void locateWhenAddingInlineBoxWhereLastMemberIsAnonInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(anonInlineContainer));
		when(anonInlineContainer.getVisual()).thenReturn(visual2);
		when(visual2.getAnonReason()).thenReturn(AnonReason.INLINE_CONTAINER);

		BlockBoxParentLocator locator=new BlockBoxParentLocator(subject, visual1);
		assertEquals(anonInlineContainer, locator.locate((Box)childInlineBox));
	}
	
	@Test
	public void locateWhenAddingInlineBoxWhenSubjectIsEmptyReturnsNewBox()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList());
		when(visual1.createAnonInlineBox(AnonReason.INLINE_CONTAINER)).thenReturn(anonInlineContainer);
		
		BlockBoxParentLocator locator=new BlockBoxParentLocator(subject, visual1);
		assertEquals(anonInlineContainer, locator.locate((Box)childInlineBox));
		verify(subject).flow_back((Box)anonInlineContainer);
	}
	
	@Test
	public void locateWhenAddingAtomicInlineWhereLastMemberIsAnonInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(anonInlineContainer));
		when(anonInlineContainer.getVisual()).thenReturn(visual2);
		when(visual2.getAnonReason()).thenReturn(AnonReason.INLINE_CONTAINER);

		BlockBoxParentLocator locator=new BlockBoxParentLocator(subject, visual1);
		assertEquals(anonInlineContainer, locator.locate(atomicInline));
	}	
	
	@Test
	public void locateWhenAddingAtomicInlineReturnsNewAnonInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList());
		when(visual1.createAnonInlineBox(AnonReason.INLINE_CONTAINER)).thenReturn(anonInlineContainer);
		
		BlockBoxParentLocator locator=new BlockBoxParentLocator(subject, visual1);
		assertEquals(anonInlineContainer, locator.locate(atomicInline));
		verify(subject).flow_back((Box)anonInlineContainer);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void locateWhenAddingInlineBoxThrowsException()
	{
		BlockBoxParentLocator locator=new BlockBoxParentLocator(subject, visual1);
		locator.locate((Inline)childInlineBox);
	}
}

