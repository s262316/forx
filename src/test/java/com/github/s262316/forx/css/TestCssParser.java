package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.s262316.forx.tree.style.Declaration;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.ImportRule;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.StyleRule;
import com.github.s262316.forx.tree.style.selectors.SelectorAttr;
import com.github.s262316.forx.tree.style.selectors.SelectorElement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class TestCssParser
{
	static Server server=null;
	
	static final String TEST_SERVER_URL="http://localhost:8080/"; 
	
	@BeforeClass
	public static void startup() throws Exception
	{
		server = new Server(8080);
		ResourceHandler staticFiles=new ResourceHandler();
		staticFiles.setBaseResource(Resource.newClassPathResource("com/github/s262316/forx/css"));
		staticFiles.setDirectoriesListed(true);
		server.setHandler(staticFiles);
		server.start();
	}
	
	@AfterClass
	public static void shutdown() throws Exception
	{
		if(server!=null)
			server.stop();
	}
	
	@Test
	public void testParseRuleset1() throws Exception
	{
		CSSParser parser=new CSSParser("p { color : red }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(1, sr.size());
		assertEquals(1, sr.get(0).getSelector().getParts().size());
		assertEquals("p", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(1, sr.get(0).declarations.size());
		assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("red"), false)), sr.get(0).declarations);
	}
	
	@Test
	public void testParseRulesetGroupedSelectors() throws Exception
	{
		CSSParser parser=new CSSParser("p , h4 { color : red }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(2, sr.size());
		
		assertEquals(1, sr.get(0).getSelector().getParts().size());
		assertEquals("p", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(1, sr.get(0).declarations.size());
		assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("red"), false)), sr.get(0).declarations);
		assertEquals(1, sr.get(1).getSelector().getParts().size());
		assertEquals("h4", ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).name);
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).attrs.size());
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(1, sr.get(1).declarations.size());
		assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("red"), false)), sr.get(1).declarations);
	}
	
	@Test
	public void testParseRulesetBadSelector() throws Exception
	{
		CSSParser parser=new CSSParser("p , h4 , { color : red }\r\ndiv { color : green }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(0, sr.size());
		
		assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void testParseRulesetBadSelector2() throws Exception
	{
		CSSParser parser=new CSSParser(".1 { color: red; }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(0, sr.size());
	}	

	@Test
	public void testIdInvalidSelector() throws Exception
	{
		CSSParser parser=new CSSParser("#1ident { color : red }\r\n div { color : green }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(0, sr.size());
		
		assertEquals("div", tokenizer.curr.syntax);		
	}
	
	@Test
	public void testParseRuleset2() throws Exception
	{
		CSSParser parser=new CSSParser(".one, .two, .three { color: green; }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(3, sr.size());		
		
		assertEquals(1, sr.get(0).getSelector().getParts().size());
		assertEquals("*", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		assertEquals(1, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		assertEquals(new SelectorAttr("class", "~=", "one"), ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.get(0));
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(1, sr.get(0).declarations.size());
		assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("green"), false)), sr.get(0).declarations);
		
		assertEquals(1, sr.get(1).getSelector().getParts().size());
		assertEquals("*", ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).name);
		assertEquals(1, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).attrs.size());
		assertEquals(new SelectorAttr("class", "~=", "two"), ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).attrs.get(0));
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(1, sr.get(1).declarations.size());
		assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("green"), false)), sr.get(1).declarations);
		
		assertEquals(1, sr.get(2).getSelector().getParts().size());
		assertEquals("*", ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).name);
		assertEquals(1, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).attrs.size());
		assertEquals(new SelectorAttr("class", "~=", "three"), ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).attrs.get(0));
		assertEquals(0, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(1, sr.get(1).declarations.size());
		assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("green"), false)), sr.get(2).declarations);
	}
	
	@Test
	public void testParseRuleset3() throws Exception
	{
		CSSParser parser=new CSSParser("* { color: white ! important; background: green ! important; }", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		assertEquals(1, sr.size());
		assertEquals(1, sr.get(0).getSelector().getParts().size());
		assertEquals("*", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		assertEquals(3, sr.get(0).declarations.size());
		assertEquals(
				ImmutableMap.of(
						"color", new Declaration("color", new Identifier("white"), true),
						"background", new Declaration("background", new Identifier("green"), true),
						"background-color", new Declaration("background-color", new Identifier("green"), true)),
						sr.get(0).declarations);
	}
	
	@Test
	public void testParseAttrib() throws Exception
	{
		CSSParser parser=new CSSParser("[id=\"foo\"]", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		SelectorAttr sa=ReflectionTestUtils.invokeMethod(parser, "parse_attrib");

		assertEquals("id", ReflectionTestUtils.getField(sa, "attr"));
		assertEquals("=", ReflectionTestUtils.getField(sa, "op"));
		assertEquals("foo", ReflectionTestUtils.getField(sa, "value"));
	}

	@Test
	public void testImportRulesAbsolute() throws Exception
	{
		CSSParser parser=new CSSParser(TEST_SERVER_URL+"simple.cass", null, null);

		Set<MediaType> media=Sets.newHashSet();
		ImportRule imr=new ImportRule(TEST_SERVER_URL+"simple2.css", media, null, null);

//	    Stylesheet ss=parser.importRules(imr);
//	    assertEquals(new Identifier("red"), Iterables.get(ss.getRuleset(), 0).getDeclarations().get("color").getValue());
		fail();
	}

	@Test
	public void testImportRulesRelative() throws Exception
	{
		CSSParser parser=new CSSParser(TEST_SERVER_URL+"myfile.html", null, null);

	    URL location=new URL("simple.css");
		Set<MediaType> media=Sets.newHashSet();
		ImportRule imr=new ImportRule(TEST_SERVER_URL+"myfile.html", media, null, null);

//	    Stylesheet ss=parser.importRules(imr);
//	    assertEquals(new Identifier("green"), Iterables.get(ss.getRuleset(), 0).getDeclarations().get("color").getValue());
		fail();
	}
	

}
