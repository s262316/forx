package com.github.s262316.forx.box.properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.awt.Font;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.ReplacedInline;
import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.Clearance;
import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.box.util.Overflow;
import com.github.s262316.forx.box.util.TextAlign;
import com.github.s262316.forx.box.util.VerticalAlignment;


@RunWith(MockitoJUnitRunner.class)
public class TestPropertiesInjector
{
	@Mock
	BlockBox blockbox;
	@Mock
	InlineBox inlinebox;
	@Mock
	FloatBox floatbox;	
	@Mock
	ReplacedInline replacedInline;
	@Mock
	Visual v;
	
	@Test
	public void testBlock()
	{
		PropertiesInjector.inject(v, new PropertyBoxAdaptor(blockbox), blockbox, blockbox, blockbox, blockbox, blockbox, null, blockbox, blockbox, blockbox, blockbox, blockbox, blockbox);
		
		verify(blockbox).setWordSpacing(anyInt());
		verify(blockbox).setLetterSpacing(anyInt());
		verify(blockbox).setTextIndent(anyInt());
		verify(blockbox).setTextAlign(any(TextAlign.class));
		verify(blockbox).setLineHeight(anyInt());
		verify(blockbox).setVerticalAlign(any(VerticalAlignment.class));
		verify(blockbox).setFont(any(Font.class));
		verify(blockbox).setForegroundColour(any(Color.class));
		verify(blockbox).setBorders(any(Border[].class));
		verify(blockbox).setPaddings(anyInt(), anyInt(), anyInt(), anyInt());
		verify(blockbox).setClearance(any(Clearance.class));
		verify(blockbox).setOverflow(any(Overflow.class));
	}
	
	@Test
	public void testInlineBox()
	{
    	PropertiesInjector.inject(v, new PropertyBoxAdaptor(inlinebox), inlinebox, null, null, inlinebox, inlinebox, null, null, inlinebox, inlinebox, null, null, inlinebox);

		verify(inlinebox).setWordSpacing(anyInt());
		verify(inlinebox).setLetterSpacing(anyInt());
		verify(inlinebox).setLineHeight(anyInt());
		verify(inlinebox).setVerticalAlign(any(VerticalAlignment.class));
		verify(inlinebox).setFont(any(Font.class));
		verify(inlinebox).setForegroundColour(any(Color.class));
		verify(inlinebox).setBorders(any(Border[].class));
		verify(inlinebox).setPaddings(anyInt(), anyInt(), anyInt(), anyInt());
	}

	@Test
	public void testFloatBox()
	{
		PropertiesInjector.inject(v, new PropertyBoxAdaptor(floatbox), floatbox, null, floatbox, floatbox, floatbox, floatbox, null, floatbox, floatbox, null, floatbox, floatbox);
		
		verify(floatbox).setWordSpacing(anyInt());
		verify(floatbox).setLetterSpacing(anyInt());
		verify(floatbox).setTextIndent(anyInt());
		verify(floatbox).setTextAlign(any(TextAlign.class));
		verify(floatbox).setLineHeight(anyInt());
		verify(floatbox).setVerticalAlign(any(VerticalAlignment.class));
		verify(floatbox).setFont(any(Font.class));
		verify(floatbox).setForegroundColour(any(Color.class));
		verify(floatbox).setBorders(any(Border[].class));
		verify(floatbox).setPaddings(anyInt(), anyInt(), anyInt(), anyInt());
		verify(floatbox).setClearance(any(Clearance.class));
		verify(floatbox).setOverflow(any(Overflow.class));
		verify(floatbox).setFloatPosition(any(FloatPosition.class));
	}
	
	@Test
	public void testReplacedInline()
	{
    	PropertiesInjector.inject(v, new PropertyAtomicInlineAdaptor(replacedInline), replacedInline, null, null, null, null, null, null, replacedInline, replacedInline, null, null, null);

		verify(replacedInline).setLineHeight(anyInt());
		verify(replacedInline).setVerticalAlign(any(VerticalAlignment.class));
		verify(replacedInline).setFont(any(Font.class));
	}
}




