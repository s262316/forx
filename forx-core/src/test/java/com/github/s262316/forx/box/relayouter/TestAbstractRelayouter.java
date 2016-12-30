package com.github.s262316.forx.box.relayouter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.support.AllLayouterForTest;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LayoutUtils.class,LayableContainerMapping.class})
public class TestAbstractRelayouter
{
	@Mock
	Box container;
	@Mock
	Layable l1;
	@Mock
	Layable l2;
	@Mock
	Layable l3;
	@Mock
	Layable l4;
	@Mock
	Layable l5;
	@Mock
	Layable l6;
	@Mock
	Relayouter dummyRelayouter;

	@Before
	public void setup()
	{
		PowerMockito.mockStatic(LayoutUtils.class);				
		PowerMockito.mockStatic(LayableContainerMapping.class,
				new Answer<LayableContainer>()
				{
					@Override
					public LayableContainer answer(InvocationOnMock invocation)
				     {
				         Object[] args = invocation.getArguments();
				         Object mock = invocation.getMock();
		
				         return new DefaultLayableContainer((Layable)args[0]);
				     }
				}
			);		
		
		// containers
		when(l1.container()).thenReturn(container);
		when(l2.container()).thenReturn(container);
		when(l3.container()).thenReturn(container);
		when(l4.container()).thenReturn(container);
		when(l5.container()).thenReturn(container);
		when(l6.container()).thenReturn(container);

		when(container.calculate_position(l1)).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));
		when(container.calculate_position(l2)).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));
		when(container.calculate_position(l3)).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));
		when(container.calculate_position(l4)).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));
		when(container.calculate_position(l5)).thenReturn(new LayoutResult(false, Optional.of(dummyRelayouter)));
		when(container.calculate_position(l6)).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));
		
		// IDs
		when(l1.getId()).thenReturn(1);
		when(l2.getId()).thenReturn(2);
		when(l3.getId()).thenReturn(3);
		when(l4.getId()).thenReturn(4);
		when(l5.getId()).thenReturn(5);
		when(l6.getId()).thenReturn(6);
		

	}

	@Test
	public void testAllPositionsSuccessful()
	{
		LayoutContext context=new LayoutContext();
		AllLayouterForTest layouter=new AllLayouterForTest(Lists.newArrayList(l1, l2, l3));
	
		layouter.unlayout(context);

		assertEquals(1, Iterables.get(context.getDirties(), 0).getId());
		assertEquals(2, Iterables.get(context.getDirties(), 1).getId());
		assertEquals(3, Iterables.get(context.getDirties(), 2).getId());
		
		LayoutResult re=layouter.layout(context);
		
		assertFalse(re.getRelayouter().isPresent());
		assertTrue(context.getDirties().isEmpty());
		
		InOrder inOrder = inOrder(container);
		inOrder.verify(container).uncalculate_position(l3);
		inOrder.verify(container).uncalculate_position(l2);
		inOrder.verify(container).uncalculate_position(l1);
		inOrder.verify(container).calculate_position(l1);
		inOrder.verify(container).calculate_position(l2);
		inOrder.verify(container).calculate_position(l3);
	}
	
	@Test
	public void testRegression()
	{
		LayoutContext context=new LayoutContext();
		AllLayouterForTest layouter=new AllLayouterForTest(Lists.newArrayList(l4, l5, l6));

		layouter.unlayout(context);
		
		assertEquals(4, Iterables.get(context.getDirties(), 0).getId());
		assertEquals(5, Iterables.get(context.getDirties(), 1).getId());
		assertEquals(6, Iterables.get(context.getDirties(), 2).getId());		
		
		LayoutResult re=layouter.layout(context);

		InOrder inOrder = inOrder(container);		
		
		inOrder.verify(container).uncalculate_position(l6);
		inOrder.verify(container).uncalculate_position(l5);
		inOrder.verify(container).uncalculate_position(l4);
		
		inOrder.verify(container).calculate_position(l4);
		inOrder.verify(container).calculate_position(l5);
		inOrder.verify(container, never()).calculate_position(l6);
		
		assertSame(re.getRelayouter().get(), dummyRelayouter);
		
		assertEquals(2, context.getDirties().size());
		assertEquals(5, Iterables.get(context.getDirties(), 0).getId());
		assertEquals(6, Iterables.get(context.getDirties(), 1).getId());
	}
}


