package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.Operator;
import com.github.s262316.forx.style.selectors.Selector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.StyleRule;
import com.github.s262316.forx.style.selectors.SelectorAttr;
import com.github.s262316.forx.style.selectors.SelectorElement;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class TestCssParser
{
	static Server server=null;
	
	static final String TEST_SERVER_URL="http://localhost:8081/";

	@Mock
	CSSPropertiesReference cssPropertiesReference;

	@Rule
	public ExpectedException thrown=ExpectedException.none();

	@Before
	public void setup()
	{
		Mockito.when(cssPropertiesReference.validate(Matchers.any(Declaration.class))).thenReturn(true);
		Mockito.when(cssPropertiesReference.validate(Matchers.anyString(), Matchers.any(Value.class))).thenReturn(true);
	}

	@BeforeClass
	public static void startup() throws Exception
	{
		server = new Server(8081);
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
	public void parseSimpleRuleset() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("p { color : red }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(1, sr.get(0).getSelector().getParts().size());
		Assert.assertEquals("p", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(0).declarations.size());
		Assert.assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("red"), false)), sr.get(0).declarations);
	}
	
	@Test
	public void testParseRulesetGroupedSelectors() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("p , h4 { color : red }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(2, sr.size());
		
		Assert.assertEquals(1, sr.get(0).getSelector().getParts().size());
		Assert.assertEquals("p", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(0).declarations.size());
		Assert.assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("red"), false)), sr.get(0).declarations);
		Assert.assertEquals(1, sr.get(1).getSelector().getParts().size());
		Assert.assertEquals("h4", ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).name);
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(1).declarations.size());
		Assert.assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("red"), false)), sr.get(1).declarations);
	}
	
	@Test
	public void testParseRulesetBadSelector() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("p , h4 , { color : red }\r\ndiv { color : green }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(0, sr.size());
		
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void testParseRulesetBadSelector2() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser(".1 { color: red; }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(0, sr.size());
	}	

	@Test
	public void parseRulesetWithBadDeclarationRecovers1() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("div { color: red; background-color}", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(1, sr.get(0).getDeclarations().size());
		Assert.assertEquals(new Identifier("red"), sr.get(0).getDeclarations().get("color").getValue());
	}
	
	@Test
	public void parseRulesetWithBadDeclarationRecovers2() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("div { background-color: ; color : red }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(1, sr.get(0).getDeclarations().size());
		Assert.assertEquals(new Identifier("red"), sr.get(0).getDeclarations().get("color").getValue());
	}
	
	@Test
	public void testParseRulesetDuplicateProperties() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("div { color: red; color : blue}", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(new Identifier("blue"), sr.get(0).declarations.get("color").getValue());
	}
	
	@Test
	public void testIdInvalidSelector() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("#1ident { color : red }\r\n div { color : green }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(0, sr.size());
		
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void parseRulesetWithMultipleSelectors() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser(".one, .two, .three { color: green; }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(3, sr.size());
		
		Assert.assertEquals(1, sr.get(0).getSelector().getParts().size());
		Assert.assertEquals("*", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		Assert.assertEquals(1, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(new SelectorAttr("class", "~=", "one"), ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.get(0));
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(0).declarations.size());
		Assert.assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("green"), false)), sr.get(0).declarations);
		
		Assert.assertEquals(1, sr.get(1).getSelector().getParts().size());
		Assert.assertEquals("*", ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).name);
		Assert.assertEquals(1, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(new SelectorAttr("class", "~=", "two"), ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).attrs.get(0));
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(1).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(1).declarations.size());
		Assert.assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("green"), false)), sr.get(1).declarations);
		
		Assert.assertEquals(1, sr.get(2).getSelector().getParts().size());
		Assert.assertEquals("*", ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).name);
		Assert.assertEquals(1, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(new SelectorAttr("class", "~=", "three"), ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).attrs.get(0));
		Assert.assertEquals(0, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(2).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(1).declarations.size());
		Assert.assertEquals(ImmutableMap.of("color", new Declaration("color", new Identifier("green"), false)), sr.get(2).declarations);
	}
	
	@Test
	public void parseRulesetWithUniversalSelector() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("* { color: white  }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(1, sr.get(0).getSelector().getParts().size());
		Assert.assertEquals("*", ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).name);
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).attrs.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).ids.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoClasses.size());
		Assert.assertEquals(0, ((SelectorElement)sr.get(0).getSelector().getParts().get(0)).pseudoElements.size());
		Assert.assertEquals(1, sr.get(0).declarations.size());
		Assert.assertEquals(
				ImmutableMap.of(
						"color", new Declaration("color", new Identifier("white"), false)),
						sr.get(0).declarations);
	}
	
	@Test
	public void parseRulesetWithMultipleDeclarations() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("* { color: white ; background-color: green ; }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(2, sr.get(0).declarations.size());
		Assert.assertEquals(
				ImmutableMap.of(
						"color", new Declaration("color", new Identifier("white"), false),
						"background-color", new Declaration("background-color", new Identifier("green"), false)),
						sr.get(0).declarations);
	}	
	
	@Test
	public void parseRulesetWithMultipleImportants() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("* { color: white ! important; background-color: green ! important; }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(2, sr.get(0).declarations.size());
		Assert.assertEquals(
				ImmutableMap.of(
						"color", new Declaration("color", new Identifier("white"), true),
						"background-color", new Declaration("background-color", new Identifier("green"), true)),
						sr.get(0).declarations);
	}

	@Test
	public void testParseAttrib() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();		
		CSSParser parser=new CSSParser("[id=\"foo\"]", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();				
		
		SelectorAttr sa=parser.parse_attrib();

		Assert.assertEquals("id", ReflectionTestUtils.getField(sa, "attr"));
		Assert.assertEquals("=", ReflectionTestUtils.getField(sa, "op"));
		Assert.assertEquals("foo", ReflectionTestUtils.getField(sa, "value"));
	}

	@Test
	public void testImportRulesAbsolute() throws Exception
	{
		CSSParser parser=new CSSParser(TEST_SERVER_URL+"simple.cass", null, cssPropertiesReference);

		Set<MediaType> media=Sets.newHashSet();
		ImportRule imr=new ImportRule(TEST_SERVER_URL+"simple2.com.github.s262316.forx.css", media, null, null, cssPropertiesReference);

//	    Stylesheet ss=parser.importRules(imr);
//	    assertEquals(new Identifier("red"), Iterables.get(ss.getRuleset(), 0).getDeclarations().get("color").getValue());
		Assert.fail();
	}

	@Test
	public void testImportRulesRelative() throws Exception
	{
		CSSParser parser=new CSSParser(TEST_SERVER_URL+"myfile.html", null, cssPropertiesReference);

	    URL location=new URL("simple.com.github.s262316.forx.css");
		Set<MediaType> media=Sets.newHashSet();
		ImportRule imr=new ImportRule(TEST_SERVER_URL+"myfile.html", media, null, null, cssPropertiesReference);

//	    Stylesheet ss=parser.importRules(imr);
//	    assertEquals(new Identifier("green"), Iterables.get(ss.getRuleset(), 0).getDeclarations().get("color").getValue());
		Assert.fail();
	}
	
	@Test
	public void cssParserIgnoresCharsetAndAdvancesPast() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("@charset \"something\"; div ", referrer, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		parser.parse_statement(null);
		
		Assert.assertEquals(TokenType.CR_IDENT, tokenizer.curr.type);
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test(expected=BadDeclarationException.class)
	public void errorInValueResultsInNoDeclaration() throws Exception
	{
		ValueParser valueParser=Mockito.mock(ValueParser.class);
		Mockito.when(valueParser.parse()).thenThrow(BadValueException.class);
		
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("color : redddd !important", referrer, cssPropertiesReference);
		ReflectionTestUtils.setField(parser, "valueParser", valueParser);
	
		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		parser.parse_declaration();		
	}

	@Test
	public void invalidPriorityResultsInBadDeclaration() throws Exception
	{
		thrown.expect(BadDeclarationException.class);
		thrown.expectMessage("expected priority value, found fffff");

		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("color : redddd ! fffff", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		parser.parse_declaration();
	}
	
	@Test
	public void validDeclarationParses() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("color : red", referrer, cssPropertiesReference);
	
		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		List<Declaration> decs=parser.parse_declaration();
		Assert.assertEquals(1, decs.size());
		Assert.assertEquals("color", decs.get(0).getProperty());
		Assert.assertEquals(new Identifier("red"), decs.get(0).getValue());
	}

	@Test
	public void propertyIsLowerCased() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("COLOR : red", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		List<Declaration> decs=parser.parse_declaration();
		Assert.assertEquals("color", decs.get(0).getProperty());
	}

	@Test(expected=BadDeclarationException.class)
	public void declarationWithoutColonOrValueFails() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("color", referrer, cssPropertiesReference);
	
		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		parser.parse_declaration();
	}
	
	@Test(expected=BadDeclarationException.class)
	public void declarationWithoutValueFails() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("color : ", referrer, cssPropertiesReference);
	
		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		parser.parse_declaration();
	}

	@Test(expected=BadDeclarationException.class)
	public void declarationWithoutPropertyFails() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser(" : red ", referrer, cssPropertiesReference);
	
		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();
		
		parser.parse_declaration();
	}

	@Test
	public void rulesetWithNestedBlockIsRecovered1() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("div { color:green; color{;color:maroon}}", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(1, sr.get(0).getDeclarations().size());
		Assert.assertEquals(new Identifier("green"), sr.get(0).getDeclarations().get("color").getValue());
	}

	@Test
	public void rulesetWithNestedBlockIsRecovered2() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("div{color:red;   color{;color:maroon}; background-color:green}", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(2, sr.get(0).getDeclarations().size());
		Assert.assertEquals(new Identifier("green"), sr.get(0).getDeclarations().get("background-color").getValue());
		Assert.assertEquals(new Identifier("red"), sr.get(0).getDeclarations().get("color").getValue());
	}

	@Test
	public void testSkipSemicolonWithinBlockOnMalformedDeclaration() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("p { span { color: yellow; background: red; } .test { color: maroon; background: yellow; } ; display : block }", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		List<StyleRule> sr=parser.parse_ruleset(EnumSet.of(MediaType.MT_ALL));
		Assert.assertEquals(1, sr.size());
		Assert.assertEquals(1, sr.get(0).getDeclarations().size());
		Assert.assertEquals(new Identifier("block"), sr.get(0).getDeclarations().get("display").getValue());
	}

	@Test
	public void prematureEndOfRuleset() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("selector", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		parser.parse_ruleset(EnumSet.allOf(MediaType.class));
	}

	@Test
	public void selectorContainsEscapedPunct() throws Exception
	{
		TestReferringDocument referrer=new TestReferringDocument();
		CSSParser parser=new CSSParser("p.class#id \\{ background", referrer, cssPropertiesReference);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		Selector selector=parser.parse_selector();
		Assert.assertEquals(5, selector.getParts().size());
		Assert.assertEquals(SelectorElement.class, selector.getParts().get(0).getClass());
		Assert.assertEquals(Operator.class, selector.getParts().get(1).getClass());
		Assert.assertEquals(SelectorElement.class, selector.getParts().get(2).getClass());
		Assert.assertEquals(Operator.class, selector.getParts().get(3).getClass());
		Assert.assertEquals(SelectorElement.class, selector.getParts().get(4).getClass());

		Assert.assertEquals("{", ((SelectorElement)selector.getParts().get(2)).name);
	}

	@Test(expected=BadSelectorException.class)
	public void badPseudoSelector() throws Exception
	{
		TestReferringDocument referrer = new TestReferringDocument();
		CSSParser parser = new CSSParser("background: red", referrer, cssPropertiesReference);

		Tokenizer tokenizer = (Tokenizer) ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		parser.parse_selector();
	}
}
