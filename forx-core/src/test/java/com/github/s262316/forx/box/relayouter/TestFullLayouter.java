package com.github.s262316.forx.box.relayouter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class TestFullLayouter
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

		// lists
		when(root.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_1, p_2, p_3));
		when(p_1.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_1_1, p_1_2));
		when(p_2.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_2_1, p_2_2, p_2_3));
		when(p_3.getMembersAll()).thenReturn(Lists.<Layable>newArrayList(p_3_1, p_3_2, p_3_3));
	}
	
	@Test
	public void testOrder()
	{
		FullLayouter full=new FullLayouter(root);
		ListIterator<Layable> it=full.getAffected().listIterator();

		assertSame(p_1, it.next());
		assertSame(p_1_1, it.next());
		assertSame(p_1_2, it.next());
		assertSame(p_2, it.next());
		assertSame(p_2_1, it.next());
		assertSame(p_2_2, it.next());
		assertSame(p_2_3, it.next());
		assertSame(p_3, it.next());
		assertSame(p_3_1, it.next());
		assertSame(p_3_2, it.next());
		assertSame(p_3_3, it.next());
		
		assertFalse(it.hasNext());
		
	}
}
