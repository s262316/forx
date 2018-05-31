package com.github.s262316.forx.box;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.TextAlign;

@RunWith(MockitoJUnitRunner.class)
public class LineTest
{
	@Mock
	Flowspace flowspace;
	
	@Test
	public void leftAdjustment()
	{
		Line line=new Line(12, 25, 30, 15, flowspace, TextAlign.TA_LEFT);

		Text text=new Text("text1", 0, SpaceFlag.SF_NOT_SPACE, 0);
		Mockito.when(flowspace.back_atomic(line)).thenReturn(text);
		
		int adjustment=line.alignmentAdjustment(null);
		assertEquals(0, adjustment);
	}

	@Test
	public void rightAdjustment()
	{
		Line line=new Line(12, 25, 30, 15, flowspace, TextAlign.TA_RIGHT);

		Text text=new Text("text1", 0, SpaceFlag.SF_NOT_SPACE, 0);
		text.set_dimensions(5, 5);
		text.set_position(5, 5);
		Mockito.when(flowspace.back_atomic(line)).thenReturn(text);
		
		int adjustment=line.alignmentAdjustment(null);
		assertEquals(21, adjustment);
	}
	
	@Test
	public void centreAdjustment()
	{
		Line line=new Line(12, 25, 30, 15, flowspace, TextAlign.TA_CENTER);

		Text text=new Text("text1", 0, SpaceFlag.SF_NOT_SPACE, 0);
		text.set_dimensions(5, 5);
		text.set_position(5, 5);
		
		Mockito.when(flowspace.back_atomic(line)).thenReturn(text);
		
		int adjustment=line.alignmentAdjustment(null);
		assertEquals(10, adjustment);
	}
}
