package com.github.s262316.forx.style;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

import com.github.s262316.forx.style.selectors.util.Selectors;
import com.github.s262316.forx.style.util.StylesheetBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.style.selectors.PseudoElementType;

@RunWith(MockitoJUnitRunner.class)
public class TestStylesheet
{
	ImmutableMap<String, Declaration> decs1=ImmutableMap.of(
			"d1", new Declaration("d1", new DummyValue(), false),
			"d2", new Declaration("d2", new DummyValue(), true));
	
	ImmutableMap<String, Declaration> decs2=ImmutableMap.of(
			"d3", new Declaration("d3", new DummyValue(), false),
			"d4", new Declaration("d4", new DummyValue(), true));	

	ImmutableMap<String, Declaration> decs3=ImmutableMap.of(
			"d5", new Declaration("d5", new DummyValue(), false),
			"d6", new Declaration("d6", new DummyValue(), false),
			"d7", new Declaration("d7", new DummyValue(), true));	
	
	ImmutableMap<String, Declaration> decs4=ImmutableMap.of(
			"d1", new Declaration("d1", new DummyValue(), false),
			"d2", new Declaration("d2", new DummyValue(), false),
			"d3", new Declaration("d3", new DummyValue(), false));		
	
	@Mock
	XmlElement pElement;
	
	@Test
	public void testSameSpecificityOrdering()
	{
		Stylesheet ss=new Stylesheet(null, null), toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.trueSelector(), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs2, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);
		
		ss.mergeNormalRules(toMerge);
		
		TreeSet<StyleRule> rules=ss.getRuleset();
		assertEquals(2, rules.size());
		assertEquals(1, Iterables.get(rules, 0).declarations.size());
		assertTrue(Iterables.get(rules, 0).declarations.containsKey("d3"));
		assertEquals(1, Iterables.get(rules, 1).declarations.size());
		assertTrue(Iterables.get(rules, 1).declarations.containsKey("d1"));
	}
	
	@Test
	public void testSpecificityOrdering()
	{
		Stylesheet ss=new Stylesheet(null, null), toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs2, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);
		
		ss.mergeNormalRules(toMerge);
		
		TreeSet<StyleRule> rules=ss.getRuleset();
		assertEquals(2, rules.size());
		assertEquals(1, Iterables.get(rules, 0).declarations.size());
		assertTrue(Iterables.get(rules, 0).declarations.containsKey("d1"));
		assertEquals(1, Iterables.get(rules, 1).declarations.size());
		assertTrue(Iterables.get(rules, 1).declarations.containsKey("d3"));
	}
	
	@Test
	public void testNormalMerge()
	{
		Stylesheet ss=new Stylesheet(null, null), toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.trueSelector(), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs2, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);
		
		ss.mergeNormalRules(toMerge);

		TreeSet<StyleRule> rules=ss.getRuleset();
		assertEquals(2, rules.size());
		assertEquals(1, Iterables.get(rules, 0).declarations.size());
		assertTrue(Iterables.get(rules, 0).declarations.containsKey("d3"));
		assertEquals(1, Iterables.get(rules, 1).declarations.size());
		assertTrue(Iterables.get(rules, 1).declarations.containsKey("d1"));
	}
	
	@Test
	public void testImportantMerge()
	{
		Stylesheet ss=new Stylesheet(null, null), toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.trueSelector(), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs2, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);
		
		ss.mergeImportantRules(toMerge);

		TreeSet<StyleRule> rules=ss.getRuleset();
		assertEquals(2, rules.size());
		assertEquals(1, Iterables.get(rules, 0).declarations.size());
		assertTrue(Iterables.get(rules, 0).declarations.containsKey("d4"));
		assertEquals(1, Iterables.get(rules, 1).declarations.size());
		assertTrue(Iterables.get(rules, 1).declarations.containsKey("d2"));		
	}
	
	@Test
	public void testFindDeclarationFind0Of1()
	{
		when(pElement.getName()).thenReturn("div");
		
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.falseSelector(), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs4, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);

		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_ALL);
		assertNull(d1);
	}

	@Test
	public void testFindDeclarationFind0Of0()
	{
		Stylesheet toMerge=new Stylesheet(null, null);
		
		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_ALL);
		assertEquals(null, d1);
	}	

	@Test
	public void testFindDeclarationFind1()
	{
		when(pElement.getName()).thenReturn("p");
		
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.trueSelector(), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs4, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);

		// sr2 more specific. takes priority
		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_ALL);
		assertSame(decs4.get("d1"), d1);
		
		Declaration d2=toMerge.findDeclaration(pElement, "d2", MediaType.MT_ALL);
		assertSame(decs4.get("d2"), d2);

		Declaration d3=toMerge.findDeclaration(pElement, "d3", MediaType.MT_ALL);
		assertSame(decs4.get("d3"), d3);
	}

	// same as above but stylerules swapped around
	@Test
	public void testFindDeclarationFind2()
	{
		when(pElement.getName()).thenReturn("p");
		
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.trueSelector(), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs4, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr2);
		toMerge.getRuleset().add(sr1);

		// sr2 more specific. takes priority
		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_ALL);
		assertSame(decs4.get("d1"), d1);
		
		Declaration d2=toMerge.findDeclaration(pElement, "d2", MediaType.MT_ALL);
		assertSame(decs4.get("d2"), d2);

		Declaration d3=toMerge.findDeclaration(pElement, "d3", MediaType.MT_ALL);
		assertSame(decs4.get("d3"), d3);
	}	
	
	// same as 2 above but selectors swapped around
	@Test
	public void testFindDeclarationFind3()
	{
		when(pElement.getName()).thenReturn("p");
		
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs4, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);

		// sr2 more specific. takes priority
		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_ALL);
		assertSame(decs1.get("d1"), d1);
		
		Declaration d2=toMerge.findDeclaration(pElement, "d2", MediaType.MT_ALL);
		assertSame(decs1.get("d2"), d2);

		// no d3 in decs1
		Declaration d3=toMerge.findDeclaration(pElement, "d3", MediaType.MT_ALL);
		assertSame(decs4.get("d3"), d3);
	}	
	
	@Test
	public void testMediaTypes()
	{
		when(pElement.getName()).thenReturn("p");
		
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs1, EnumSet.of(MediaType.MT_TV), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs4, EnumSet.of(MediaType.MT_TV), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);

		// sr2 more specific. takes priority
		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_TV);
		assertSame(decs1.get("d1"), d1);
		
		Declaration d2=toMerge.findDeclaration(pElement, "d2", MediaType.MT_TV);
		assertSame(decs1.get("d2"), d2);

		// no d3 in decs1
		Declaration d3=toMerge.findDeclaration(pElement, "d3", MediaType.MT_TV);
		assertSame(decs4.get("d3"), d3);		
	}
	
	
	@Test
	public void testMediaTypesNoMatch()
	{
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs1, EnumSet.of(MediaType.MT_PRINT), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs4, EnumSet.of(MediaType.MT_PRINT), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);

		// sr2 more specific. takes priority
		Declaration d1=toMerge.findDeclaration(pElement, "d1", MediaType.MT_TV);
		assertNull(d1);
		
		Declaration d2=toMerge.findDeclaration(pElement, "d2", MediaType.MT_TV);
		assertNull(d2);

		// no d3 in decs1
		Declaration d3=toMerge.findDeclaration(pElement, "d3", MediaType.MT_TV);
		assertNull(d3);		
	}	
	
	@Test
	public void testFindAllDeclarations()
	{
		when(pElement.getName()).thenReturn("p");
		
		Stylesheet toMerge=new Stylesheet(null, null);
		
		StyleRule sr1=new StyleRule(Selectors.createSimpleElementNameSelector("p"), decs1, EnumSet.of(MediaType.MT_ALL), 0);
		StyleRule sr2=new StyleRule(Selectors.trueSelector(), decs4, EnumSet.of(MediaType.MT_ALL), 1);

		toMerge.getRuleset().add(sr1);
		toMerge.getRuleset().add(sr2);

		Collection<StyleRule> rules=toMerge.findAllRules(pElement, MediaType.MT_ALL, PseudoElementType.PE_NOT_PSEUDO);

		assertEquals(
			Lists.newArrayList(sr1, sr2),
			rules);
	}
	
	@Test
	@Ignore // what is this trying to test???
	public void testFindAllDeclarationsFirstLetter() throws Exception
	{
//		when(pElement.getName()).thenReturn("p");
//
//		StringReader styles=new StringReader(
//				"p {border: thin solid orange}\r\n"+
//				"p {border: thin solid blue}\r\n"+
//				"span {border: thin solid fuchsia}\r\n"
//				);
//
//		Stylesheet ss=new StylesheetBuilder()
//				.withStylerule(Selectors.createSimpleElementNameSelector("p", ""))
//				.withStylerule(Selectors.createSimpleElementNameSelector("p", ))
//				.withStylerule(Selectors.createSimpleElementNameSelector("p", ))
//				.build();
//
//		List<StyleRule> sr1=ss.findAllRules(pElement, MediaType.MT_ALL, PseudoElementType.PE_FIRST_LETTER);
//		assertEquals(0, sr1.size());
//		List<StyleRule> sr2=ss.findAllRules(pElement, MediaType.MT_ALL, PseudoElementType.PE_NOT_PSEUDO);
//		assertEquals(2, sr2.size()); // border/top/bottom/style/thickness/color/etc for 2 selectors + the original 2 border decs = 26
	}
}
