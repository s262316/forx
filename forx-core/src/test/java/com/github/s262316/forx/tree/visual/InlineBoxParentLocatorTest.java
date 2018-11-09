package com.github.s262316.forx.tree.visual;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.properties.Visual;
import com.google.common.collect.Lists;

/**
<div>
	<p>
	    <b>
	        <-- insert newbox here
	    </b>
	</p>
</div>

transforms to this:

<div>
	<p>
	    <dummydiv>
	        <b>
	        </b>
	            <generateddiv> <newbox/>  </generateddiv>
	        <b>
	        </b>
	    </dummydiv>
	</p>
</div>
 */
@RunWith(MockitoJUnitRunner.class)
public class InlineBoxParentLocatorTest
{
	@Mock
	RootBox root;
	@Mock
	BlockBox div, p, newChild, dummyContainer;
	@Mock
	InlineBox b, bPostSplit;
	@Mock
	Visual rootVis, divVis, pVis, bVis;
	@Mock
	Visual bPostSplitVis;
	
	@Before
	public void setup()
	{
		when(root.getMembersAll()).thenReturn(Lists.newArrayList(div));
		when(div.getMembersAll()).thenReturn(Lists.newArrayList(p));
		when(p.getMembersAll()).thenReturn(Lists.newArrayList(b));
		
		when(b.container()).thenReturn(p);
		when(p.container()).thenReturn(div);
		when(div.container()).thenReturn(root);
		
		when(b.getContainer()).thenReturn(p);
		when(p.getContainer()).thenReturn(div);
		when(div.getContainer()).thenReturn(root);
	
		when(root.getVisual()).thenReturn(rootVis);
		when(div.getVisual()).thenReturn(divVis);
		when(p.getVisual()).thenReturn(pVis);
		when(b.getVisual()).thenReturn(bVis);
		
		when(pVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(bPostSplit);
		when(pVis.createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER)).thenReturn(dummyContainer);
	}

	@Test
	public void parentIsNewDummyContainer()
	{
		InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
		assertEquals(dummyContainer, locator.locate(newChild));
	}
	
	@Test
	public void structureMatches()
	{
		InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
		locator.locate(newChild);
		
		verify(p).flow_back(dummyContainer);
		verify(dummyContainer).flow_back((Box)b);
		verify(dummyContainer).flow_back((Box)bPostSplit);
		verify(p).remove(b);
		
		// post-split
	}
	
}

