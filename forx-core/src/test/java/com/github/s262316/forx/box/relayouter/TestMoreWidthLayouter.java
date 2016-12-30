package com.github.s262316.forx.box.relayouter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class TestMoreWidthLayouter
{
	@Mock
	RootBox root;
	@Mock
	Box p_1;
	@Mock
	Box p_2;
	@Mock
	Box p_3;
	@Mock
	Layable p_1_1;
	@Mock
	Layable p_1_2;
	@Mock
	Layable p_2_1;
	@Mock
	Layable p_2_2;
	@Mock
	Layable p_2_3;
	@Mock
	Layable p_3_1;
	@Mock
	Layable p_3_2;
	@Mock
	Layable p_3_3;

	@Before
	public void setup()
	{
		// containers
		when(p_1.container()).thenReturn(root);
		when(p_2.container()).thenReturn(root);
		when(p_3.container()).thenReturn(root);
		when(p_1_1.container()).thenReturn(p_1);
		when(p_1_2.container()).thenReturn(p_1);
		when(p_2_1.container()).thenReturn(p_2);
		when(p_2_2.container()).thenReturn(p_2);
		when(p_2_3.container()).thenReturn(p_2);
		when(p_3_1.container()).thenReturn(p_3);
		when(p_3_2.container()).thenReturn(p_3);
		when(p_3_3.container()).thenReturn(p_3);
		when(p_2.root()).thenReturn(root);
		
		// id
		when(root.getId()).thenReturn(0);
		when(p_1.getId()).thenReturn(1);
		when(p_2.getId()).thenReturn(4);
		when(p_3.getId()).thenReturn(8);
		when(p_1_1.getId()).thenReturn(2);
		when(p_1_2.getId()).thenReturn(3);
		when(p_2_1.getId()).thenReturn(5);
		when(p_2_2.getId()).thenReturn(6);
		when(p_2_3.getId()).thenReturn(7);
		when(p_3_1.getId()).thenReturn(9);
		when(p_3_2.getId()).thenReturn(10);
		when(p_3_3.getId()).thenReturn(11);
		
		when(root.calculate_position(p_1)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(p_1.calculate_position(p_1_1)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(p_1.calculate_position(p_1_2)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(root.calculate_position(p_2)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(p_2.calculate_position(p_2_1)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(p_2.calculate_position(p_2_2)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));		
		when(p_2.calculate_position(p_2_3)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(root.calculate_position(p_3)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));		
		when(p_3.calculate_position(p_3_1)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));		
		when(p_3.calculate_position(p_3_2)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));
		when(p_3.calculate_position(p_3_3)).thenReturn(new LayoutResult(false, Optional.<Relayouter>absent()));		

		// lists
		when(root.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_1, p_2, p_3));
		when(p_1.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_1_1, p_1_2));
		when(p_2.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_2_1, p_2_2, p_2_3));
		when(p_3.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_3_1, p_3_2, p_3_3));
	}
	
	@Test
	public void testOrder()
	{
		MoreWidthLayouter layouter=new MoreWidthLayouter(p_2, p_2_2, 12);
		layouter.layout(new LayoutContext());
		
		ListIterator<Layable> it=layouter.getAffected().listIterator();

		assertSame(p_2, it.next());
		assertSame(p_2_1, it.next());
		assertSame(p_2_2, it.next());
		assertSame(p_2_3, it.next());
		assertSame(p_3, it.next());
		assertSame(p_3_1, it.next());
		assertSame(p_3_2, it.next());
		assertSame(p_3_3, it.next());
		assertFalse(it.hasNext());
		
		verify(p_2).setFutureWidth(12);
	}
}

