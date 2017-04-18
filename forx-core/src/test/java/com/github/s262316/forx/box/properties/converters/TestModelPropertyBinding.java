package com.github.s262316.forx.box.properties.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.s262316.forx.box.properties.BorderDescriptor;
import com.github.s262316.forx.box.util.BorderStyle;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.visual.VElement;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

public class TestModelPropertyBinding
{
	static class ModelTest
	{
		String x;
		int y;
	}

	VElement mockVElement=mock(VElement.class);
	
	@Before
	public void setup()
	{
		reset(mockVElement);
		
		when(mockVElement.getPropertyValue(eq("test-property1"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("solid"));
		when(mockVElement.getPropertyValue(eq("test-property2"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(1, ""));
		when(mockVElement.getPropertyValue(eq("test-property3"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(null);
	}
	
	@Test
	public void test1When1Match()
	{
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.bind(mockVElement, "test-property1");
		
        assertEquals(BorderStyle.BS_SOLID, borderdesc.borderTopStyle);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNoMatchingWhen()
	{
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
		// there is only a when for NumericValue but v is an Identifier
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(NumericValue.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.bind(mockVElement, "test-property1");
        
        fail();
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void testConverterFails()
	{
		// converters may throw IllegalArgumentExceptions
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
		// there is only a when for NumericValue but v is an Identifier
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.bind(mockVElement, "test-property2");
        
        fail();
	}
	
	@Test
	public void test2Whens()
	{
		ModelTest model=new ModelTest();
		
		new ModelPropertyBinding<String>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, v -> "When1")
	        	.when(NumericValue.class, v -> "When2")
	        	.inject(mv -> model.x=mv)
	        	.bind(mockVElement, "test-property1");
		
        assertEquals("When1", model.x);
        
		new ModelPropertyBinding<String>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	    	.when(Identifier.class, v -> "When1")
	    	.when(NumericValue.class, v -> "When2")
	    	.inject(mv -> model.x=mv)
	    	.bind(mockVElement, "test-property2");

		assertEquals("When2", model.x);
	}	
	
	@Test
	public void testMapStrings()
	{
		ModelTest model=new ModelTest();
		
		Map<Value, Integer> map=ImmutableMap.of(
    			new Identifier("solid"), 77,
    			new Identifier("thick"), 88,
    			new Identifier("medium"), 44);

		Function<Value, Integer> f=Functions.forMap(map);

		new ModelPropertyBinding<Integer>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, f::apply)
	        	.inject(mv -> model.y=mv)
	        	.bind(mockVElement, "test-property1");

        assertEquals(77, model.y);
	}
	
	@Test
	public void testWithComputedValue()
	{
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.computedValue(ComputedValues::specifiedValue)
	        	.bind(mockVElement, "test-property1");
		
        assertEquals(BorderStyle.BS_SOLID, borderdesc.borderTopStyle);
		verify(mockVElement).computed_value("test-property1", new Identifier("solid"));
	}
	
	@Test
	public void testWithFallbackMatchFirst()
	{
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.computedValue(ComputedValues::specifiedValue)
	        	.bind(mockVElement, "test-property1", "test-property3");
		
        assertEquals(BorderStyle.BS_SOLID, borderdesc.borderTopStyle);
		verify(mockVElement).computed_value("test-property1", new Identifier("solid"));		
	}

	@Test
	public void testWithFallbackMatchSecond()
	{
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.computedValue(ComputedValues::specifiedValue)
	        	.bind(mockVElement, "test-property3", "test-property1");
		
        assertEquals(BorderStyle.BS_SOLID, borderdesc.borderTopStyle);
		verify(mockVElement).computed_value("test-property1", new Identifier("solid"));		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testWithFallbackNoMatches()
	{
		BorderDescriptor borderdesc=new BorderDescriptor();
		ModelValueInjector<BorderStyle> testInjector=bs -> borderdesc.borderTopStyle=bs;
		
        new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	        	.when(Identifier.class, new EnumConverter<>("BS_", true, BorderStyle.class))
	        	.inject(testInjector)
	        	.computedValue(ComputedValues::specifiedValue)
	        	.bind(mockVElement, "no1", "no2");
	}
}

