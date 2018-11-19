package com.github.s262316.forx.box.adders;

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
import com.github.s262316.forx.newbox.AnonReason;

@RunWith(MockitoJUnitRunner.class)
public class BlockBoxParentAdderTest
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
		BlockBoxParentAdder locator=new BlockBoxParentAdder(subject);
		locator.add(childBlock);
		verify(subject).flow_back(childBlock);
	}
	
	@Test
	public void locateWhenAddingInlineBoxWhereLastMemberIsAnonInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(anonInlineContainer));
		when(anonInlineContainer.getVisual()).thenReturn(visual2);
		when(visual2.getAnonReason()).thenReturn(AnonReason.INLINE_CONTAINER);

		BlockBoxParentAdder locator=new BlockBoxParentAdder(subject);
		locator.add((Box)childInlineBox);
		verify(anonInlineContainer).flow_back((Box)childInlineBox);
	}
	
	@Test
	public void locateWhenAddingInlineBoxWhenSubjectIsEmptyReturnsNewBox()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList());
		when(subject.getVisual()).thenReturn(visual1);
		when(visual1.createAnonInlineBox(AnonReason.INLINE_CONTAINER)).thenReturn(anonInlineContainer);
		
		BlockBoxParentAdder locator=new BlockBoxParentAdder(subject);
		locator.add((Box)childInlineBox);
		verify(subject).flow_back((Box)anonInlineContainer);
		verify(anonInlineContainer).flow_back((Box)childInlineBox);
	}
	
	@Test
	public void locateWhenAddingAtomicInlineWhereLastMemberIsAnonInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList(anonInlineContainer));
		when(anonInlineContainer.getVisual()).thenReturn(visual2);
		when(visual2.getAnonReason()).thenReturn(AnonReason.INLINE_CONTAINER);

		BlockBoxParentAdder locator=new BlockBoxParentAdder(subject);
		locator.add(atomicInline);
		verify(anonInlineContainer).flow_back((Inline)atomicInline);
	}	
	
	@Test
	public void locateWhenAddingAtomicInlineReturnsNewAnonInlineContainer()
	{
		when(subject.getMembersFlowing()).thenReturn(Lists.newArrayList());
		when(subject.getVisual()).thenReturn(visual1);
		when(visual1.createAnonInlineBox(AnonReason.INLINE_CONTAINER)).thenReturn(anonInlineContainer);
		
		BlockBoxParentAdder locator=new BlockBoxParentAdder(subject);
		locator.add(atomicInline);
		verify(subject).flow_back((Box)anonInlineContainer);
		verify(anonInlineContainer).flow_back((Inline)atomicInline);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void locateWhenAddingInlineBoxThrowsException()
	{
		BlockBoxParentAdder locator=new BlockBoxParentAdder(subject);
		locator.add((Inline)childInlineBox);
	}
}

