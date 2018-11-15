package com.github.s262316.forx.box.relayouter;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.github.s262316.forx.box.relayouter.support.AllLayouterForTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LayoutUtils.class,LayableContainerMapping.class})
public class TestRegressionIntegration
{
	@Mock
	Box container;
	@Mock(name="l1")
	Layable l1;
	@Mock(name="l2")
	Layable l2;
	@Mock(name="l3")
	Layable l3;
	@Mock(name="l4")
	Layable l4;
	@Mock(name="l5")
	Layable l5;
	@Mock(name="l6")
	Layable l6;	
	
	AllLayouterForTest r1;
	AllLayouterForTest r2;

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
		
		r1=new AllLayouterForTest(Lists.newArrayList(l1, l2, l3, l4, l5, l6));
		r2=new AllLayouterForTest(Lists.newArrayList(l3, l4));
		
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
		when(container.calculate_position(l5)).thenReturn(new LayoutResult(false, Optional.of((Relayouter)r2)), new LayoutResult(true, Optional.<Relayouter>absent()));
		when(container.calculate_position(l6)).thenReturn(new LayoutResult(true, Optional.<Relayouter>absent()));
		
		// IDs
		when(l1.getId()).thenReturn(1);
		when(l2.getId()).thenReturn(2);
		when(l3.getId()).thenReturn(3);
		when(l4.getId()).thenReturn(4);
		when(l5.getId()).thenReturn(5);
		when(l6.getId()).thenReturn(6);

		
	}
	
	/**
	 * layout l1,l2,l3,l4,l5,l6
	 * regress at l5 back to l3,l4.
	 * test is that l6 is also positioned
	 * 
	 */
	@Test
	public void test123456_relayout_34()
	{
		RegressionLayoutStrategy strategy=new RegressionLayoutStrategy();
		ReflectionTestUtils.setField(strategy, "initial", r1);

		strategy.layout();
		
		InOrder inOrder = Mockito.inOrder(container);
		
		inOrder.verify(container).uncalculate_position(l6);
		inOrder.verify(container).uncalculate_position(l5);
		inOrder.verify(container).uncalculate_position(l4);
		inOrder.verify(container).uncalculate_position(l3);
		inOrder.verify(container).uncalculate_position(l2);
		inOrder.verify(container).uncalculate_position(l1);
		
		inOrder.verify(container).calculate_position(l1);
		inOrder.verify(container).calculate_position(l2);
		inOrder.verify(container).calculate_position(l3);
		inOrder.verify(container).calculate_position(l4);
		inOrder.verify(container).calculate_position(l5);
		
		inOrder.verify(container).uncalculate_position(l4);
		inOrder.verify(container).uncalculate_position(l3);
		
		inOrder.verify(container).calculate_position(l3);
		inOrder.verify(container).calculate_position(l4);
		inOrder.verify(container).calculate_position(l6);
	}
}




