package com.github.s262316.forx.css;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.css.util.DeclarationName;
import com.github.s262316.forx.net.Resource;
import com.github.s262316.forx.net.ResourceLoader;
import com.github.s262316.forx.tree.ReferringDocument;
import com.github.s262316.forx.tree.style.Declaration;
import com.github.s262316.forx.tree.style.FunctionValue;
import com.github.s262316.forx.tree.style.HashValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.ImportRule;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.PageRule;
import com.github.s262316.forx.tree.style.PseudoPageType;
import com.github.s262316.forx.tree.style.Shorthands;
import com.github.s262316.forx.tree.style.StringValue;
import com.github.s262316.forx.tree.style.StyleRule;
import com.github.s262316.forx.tree.style.Stylesheet;
import com.github.s262316.forx.tree.style.UrlValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;
import com.github.s262316.forx.tree.style.selectors.Operator;
import com.github.s262316.forx.tree.style.selectors.PseudoClass;
import com.github.s262316.forx.tree.style.selectors.PseudoClassType;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.selectors.Selector;
import com.github.s262316.forx.tree.style.selectors.SelectorAttr;
import com.github.s262316.forx.tree.style.selectors.SelectorElement;
import com.github.s262316.forx.tree.style.selectors.SelectorPart;
import com.github.s262316.forx.tree.style.util.Selectors;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CSSParser
{
	private final static Logger logger=LoggerFactory.getLogger(CSSParser.class);

    private Tokenizer tok=null;
    private int order=0;
    private CSSPropertiesReference propertiesReference=new CSSPropertiesReference();
    private URL url;
    private Charset charset;

    public CSSParser(String cssUrl, ReferringDocument refereringDocument, ResourceLoader resourceLoader)
    {
        try
        {
            Resource resource=resourceLoader.load(cssUrl, refereringDocument, () -> Optional.of(StandardCharsets.UTF_8));
            String data=IOUtils.toString(resource.getReader());
            this.url=resource.getUrl();
            this.charset=resource.getCharset();
            tok = new Tokenizer(data);
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    public CSSParser(String inlineCss, ReferringDocument referringDocument)
    {
        this.url=referringDocument.getLocation();
        this.charset=referringDocument.getCharset().get();
        tok = new Tokenizer(inlineCss);
    }

	// [ CDO | CDC | S | statement ]*;
	public Stylesheet parse_stylesheet() throws CSSParserException, MalformedURLException, IOException
	{
			Stylesheet ss;
			List<StyleRule> rs;

			ss=new Stylesheet(charset, url);

			tok.advance();
			while(tok.curr.type!=TokenType.CR_END)
			{
					if(tok.curr.type==TokenType.CR_CD)
					{
							tok.advance();
					}
					else
					{
							rs=parse_statement(ss);
							ss.getRuleset().addAll(rs);
					}
			}

			logger.debug("stylesheet created {}", ss.toString());
			
			return ss;
	}
    
    //term
    //: unary_operator?
    //	[ NUMBER S* | PERCENTAGE S* | LENGTH S* | EMS S* | EXS S* | ANGLE S* |
    //	TIME S* | FREQ S* ]
    //| STRING S* | IDENT S* | URI S* | hexcolor | function
    public Value parse_term() throws MalformedURLException, IOException
    {
            Value v=null, any;
            Number n;

            switch(tok.curr.type)
            {
                    case CR_IDENT:
                            v=new Identifier(tok.curr.syntax);
                            break;
                    case CR_NUMBER:
                            v=new NumericValue(extractDouble(tok.curr.syntax), "px");
                            break;
                    case CR_PERCENT:
                            v=extractPercentage(tok.curr.syntax);
                            break;
                    case CR_DIMENSION:
                            v=extractDimension(tok.curr.syntax);
                            break;
                    case CR_STRING:
                            v=new StringValue(tok.curr.syntax);
                            break;
                    case CR_URI:
                            v=new UrlValue(new URL(tok.curr.syntax));
                            break;
                    case CR_HASH:
                            v=new HashValue(tok.curr.syntax);
                            break;
                    case CR_FUNCTION:
                    {
                            // urls come out of the tokenizer as TokenType.CR_URIs not TokenType.CR_FUNCTION
                            // (function S* >> (any ((comma | slash) S* any)* >> rparen)(TokenType.CR_ANY)),
                            FunctionValue fv;

                            fv=new FunctionValue();
                            fv.name=tok.curr.syntax;
                            fv.values=new ValueList();

                            tok.advance();
                            while(!tok.curr.syntax.equals(")"))
                            {
                                    any=parse_term();
                                    fv.values.members.add(any);

                                    tok.advance();
                                    if(tok.curr.syntax.equals(",") || tok.curr.syntax.equals("/"))
                                            tok.advance();
                            }

                            v=fv;

                            break;
                    }
            }
            return v;
    }

    //operator  : '/' S* | COMMA S* | /* empty */
    //expr  : term [ operator term ]*
    public Value parse_value() throws CSSParserException, MalformedURLException, IOException
    {
            ValueList value=new ValueList();
            Value v1;

            v1=parse_term();
            tok.advance();
            while(v1!=null)
            {
                    value.members.add(v1);

                    if(tok.curr.syntax.equals("/") || tok.curr.syntax.equals(","))
                            tok.advance();

                    v1=parse_term();
                    if(v1!=null)
                    {
                            tok.advance();

                    }
            }

            return value;
    }

    //declaration  : property ':' S* expr prio? | /* empty */
    private List<Declaration> parse_declaration() throws CSSParserException, MalformedURLException, IOException
    {
        Declaration dec=null;
        String name;
        ValueList vl=null;
        boolean imp=false;
        List<Declaration> decs;

        logger.debug("parse_declaration()");
        
        if(tok.curr.type!=TokenType.CR_IDENT)
        	throw new CSSParserException(CSSParserException.Type.DECLARATION_BAD_SYNTAX, tok.curr.syntax);

        name=tok.curr.syntax;

        tok.advance();
        if(!tok.curr.syntax.equals(":"))
                throw new CSSParserException(CSSParserException.Type.DECLARATION_BAD_SYNTAX, tok.curr.syntax);

        tok.advance();
        vl=(ValueList)parse_value();

        if(tok.curr.syntax.equals("!important"))
        {
                imp=true;
                tok.advance();
        }

        if(vl.members.size()==1)
                dec=new Declaration(name, vl.members.get(0), imp);
        else
                dec=new Declaration(name, vl, imp);

        if(propertiesReference.isShorthand(name))
        {
        	decs=Shorthands.expandShorthandProperty(dec);
        	if(decs.isEmpty())
        		decs=propertiesReference.expandShorthand(dec);
        }
        else
        {
        	if(propertiesReference.validate(dec));
        		decs=Lists.newArrayList(dec);
        }
        
        return decs;
    }

    //attrib
    //  '[' S* IDENT S* [ [ '=' | INCLUDES | DASHMATCH ] S* [ IDENT | STRING ] S* ]? ']'
    private SelectorAttr parse_attrib() throws CSSParserException, BadSelectorException
    {
    	String attrName, op="", value="";
    	
            tok.skipping(true);

            if(!tok.curr.syntax.equals("["))
            	throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);

            tok.advance();
            if(tok.curr.type!=TokenType.CR_IDENT)
            	throw new BadSelectorException();

            attrName=tok.curr.syntax;

            tok.advance();
            if(!tok.curr.syntax.equals("]"))
            {
                    if(!tok.curr.syntax.equals("=") && !tok.curr.syntax.equals("|=") && !tok.curr.syntax.equals("~="))
                            throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);

                    op=tok.curr.syntax;

                    tok.advance();
                    switch(tok.curr.type)
                    {
                            case CR_IDENT:
                            case CR_STRING:
                                    value=tok.curr.syntax;
                                    break;
                            default:
                                    throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);
                    }

                    tok.advance();
            }

            if(!tok.curr.syntax.equals("]"))
                    throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);

            tok.skipping(false);

            return new SelectorAttr(attrName, op, value);
    }

    //simple_selector
    //  : element_name [ HASH | class | attrib | pseudo ]*
    //  | [ HASH | class | attrib | pseudo ]+
    SelectorElement parse_simple_selector() throws CSSParserException, BadSelectorException
    {
    	String elementName=null;
    	List<String> ids=Lists.newArrayList();
    	List<SelectorAttr> attrs=Lists.newArrayList();
    	List<PseudoClass> pseudoClasses=Lists.newArrayList();
    	List<PseudoElementType> pseudoElements=Lists.newArrayList();
        boolean cont=true;

            if(tok.curr.type==TokenType.CR_IDENT || tok.curr.syntax.equals("*"))
            {
            	elementName=tok.curr.syntax;
                tok.advance();
            }

            while(cont)
            {
                    if(tok.curr.type==TokenType.CR_HASH)
                    {
                    	if(!CssParserUtils.isIdentifier(tok.curr.syntax))
                            throw new BadSelectorException();
                    		
                    	ids.add(tok.curr.syntax);
                    }
                    else if(tok.curr.syntax.equals("."))
                    {
                            SelectorAttr sa;

                            tok.advance();
                            if(tok.curr.type!=TokenType.CR_IDENT)
                                    throw new BadSelectorException();

                            sa=new SelectorAttr("class", "~=", tok.curr.syntax);

                            attrs.add(sa);
                    }
                    else if(tok.curr.syntax.equals("["))
                    {
                            SelectorAttr sa;

                            sa=parse_attrib();
                            attrs.add(sa);
                    }
                    else if(tok.curr.syntax.equals(":"))
                    {
                            String pseudo_class;

                            tok.advance();
                            if(tok.curr.type==TokenType.CR_IDENT)
                            {
                                    pseudo_class=tok.curr.syntax;

                                    if(pseudo_class.equals("first-child"))
                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_FIRST_CHILD));
                                    else if(pseudo_class.equals("link"))
                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_LINK));
                                    else if(pseudo_class.equals("visited"))
                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_VISITED));
                                    else if(pseudo_class.equals("hover"))
                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_HOVER));
                                    else if(pseudo_class.equals("active"))
                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_ACTIVE));
                                    else if(pseudo_class.equals("focus"))
                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_FOCUS));
                                    else if(pseudo_class.equals("first-line"))
                                            pseudoElements.add(PseudoElementType.PE_FIRST_LINE);
                                    else if(pseudo_class.equals("first-letter"))
                                            pseudoElements.add(PseudoElementType.PE_FIRST_LETTER);
                                    else if(pseudo_class.equals("before"))
                                            pseudoElements.add(PseudoElementType.PE_BEFORE);
                                    else if(pseudo_class.equals("after"))
                                            pseudoElements.add(PseudoElementType.PE_AFTER);
                            }
                            else if(tok.curr.type==TokenType.CR_FUNCTION)
                            {
                                    pseudo_class=tok.curr.syntax;

                                    if(pseudo_class.equals("lang"))
                                    {
                                            tok.advance();
                                            if(tok.curr.type!=TokenType.CR_IDENT)
                                                    throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);

                                            pseudoClasses.add(new PseudoClass(PseudoClassType.PCT_LANG, tok.curr.syntax));

                                            tok.advance();
                                            if(!tok.curr.syntax.equals(")"))
                                                    throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);
                                    }
                            }
                            else
                                    throw new CSSParserException(CSSParserException.Type.SELECTOR_BAD_SYNTAX, tok.curr.syntax);
                    }
                    else
                    	cont=false;

                    if(cont)
                            tok.advance();
            }

            // there is nothing in this selector
        	if(elementName==null && ids.isEmpty() && attrs.isEmpty() && pseudoClasses.isEmpty() && pseudoElements.isEmpty())
        		throw new BadSelectorException();
            	
            return new SelectorElement(Objects.firstNonNull(elementName, "*"), attrs, pseudoClasses, pseudoElements, ids);
    }

	//combinator
	//  : PLUS S*
	//  | GREATER S*
	//  | S
	Operator parse_combinator()
	{
			Operator c=new Operator();

			if(tok.curr.type==TokenType.CR_WHITESPACE)
			{
					c.op=" ";
					tok.advance();
			}

			if(tok.curr.syntax.equals("+"))
			{
					c.op="+";
					tok.advance();
			}
			else if(tok.curr.syntax.equals(">"))
			{
					c.op=">";
					tok.advance();
			}

			if(tok.curr.type==TokenType.CR_WHITESPACE)
					tok.advance();

			return c;
	}

	//selector : simple_selector [ combinator simple_selector ]*
	Selector parse_selector() throws CSSParserException, BadSelectorException
	{
		try
		{
			SelectorElement se;
			Operator op;
			List<SelectorPart> parts=new ArrayList<>();
	
			tok.skipping(false);
	
			se=parse_simple_selector();
			parts.add(se);
	
			op=parse_combinator();
			while(op!=null)
			{
					if(!tok.curr.syntax.equals("{") && !tok.curr.syntax.equals(",") && tok.curr.type!=TokenType.CR_END)
					{
							parts.add(op);
	
							se=parse_simple_selector();
							parts.add(se);
	
							op=parse_combinator();
					}
					else
							op=null;
			}
	
			Selector sel=new Selector(parts);
			
			return sel;
		}
		catch(BadSelectorException bse)
		{
	    	// bad selector. abandon the whole stylerule
			// advance to the '{' 
			// parse the declarations but skip them
//			tok.advancePast(TokenType.CR_PUNCT, "{");
			
			throw bse;
		}
		finally
		{
			tok.skipping(true);
		}
	}


    //ruleset: (selector (',' selector)*)? '{' S* declaration? [ ';' S* declaration? ]* '}' S*;
    List<StyleRule> parse_ruleset(EnumSet<MediaType> mediaTypes) throws CSSParserException, MalformedURLException, IOException
    {
		List<Declaration> decs=new ArrayList<>();
		Selector sel;
		List<Selector> selectors=new ArrayList<>();
		List<StyleRule> styleRuleManySelectors=new ArrayList<>();
		boolean skipRule=false;
		
		logger.debug("parse_ruleset({})", mediaTypes);
		
		try
		{
		    if(!tok.curr.syntax.equals("{"))
			{
				sel=parse_selector();
				selectors.add(sel);
				while(tok.curr.syntax.equals(","))
				{
					tok.advance();
					sel=parse_selector();
					selectors.add(sel);
				}
			}
			else
			{
		        // no selector.. make a universal selector
		        sel=Selectors.createSimpleElementNameSelector("*");
		        selectors.add(sel);
			}
		    
		    if(!tok.curr.syntax.equals("{"))
		    	throw new BadSelectorException();
		    else
		    	tok.advance();		    
		}
		catch(BadSelectorException bse)
		{
	    	// bad selector. abandon the whole stylerule.
			// parse_selector() has already advanced past the {
			// parse the declarations but skip them
			tok.advancePast(TokenType.CR_PUNCT, "{");

			skipRule=true;
		}
		
		while(!tok.curr.syntax.equals("}"))
		{
			while(tok.curr.syntax.equals(";"))
				tok.advance();
			
			if(!tok.curr.syntax.equals("}"))
				decs.addAll(parse_declaration());
		}
		
		tok.advance();
		
		if(skipRule)
			return Collections.<StyleRule>emptyList();
		
		ImmutableMap<String, Declaration> decsMap=Maps.uniqueIndex(decs, new DeclarationName());
		
		for(Selector s : selectors)
		{
		    ++order;
			
			styleRuleManySelectors.add(new StyleRule(s, decsMap, mediaTypes, order));           	
		}
		
		return styleRuleManySelectors;
    }

    /*
    import
    : IMPORT_SYM S*
            [STRING|URI] S* [ medium [ COMMA S* medium]* ]? ';' S*
    */
    ImportRule processImport(Stylesheet stylesheet) throws BadImportStatement
    {
    	try
        {
            ImportRule ir = null;
            String location;
            List<String> media = new ArrayList<>();

            tok.advance();

            if (tok.curr.type == TokenType.CR_STRING)
                location = tok.curr.syntax;
            else if (tok.curr.type == TokenType.CR_URI)
                location = tok.curr.syntax;
            else
                throw new BadImportStatement("found " + tok.curr.syntax + ", expected STRING or URI");

            tok.advance();

            // !(ident >> *S >> (comma >> *S >> ident >> *S)) >> semicolon >> *S)
            if (tok.curr.type == TokenType.CR_IDENT)
            {
                media.add(tok.curr.syntax);

                tok.advance();
                while (tok.curr.syntax.equals(","))
                {
                    tok.advance();
                    media.add(tok.curr.syntax);
                    tok.advance();
                }
            } else
                media.add("all");

            if (!tok.curr.syntax.equals(";"))
                throw new BadImportStatement("found " + tok.curr.syntax + ", expected ';'");

            tok.advance();

            Set<MediaType> mediaTypes = media.stream()
                    .map(v -> "MT_" + v.toUpperCase())
                    .filter(v -> EnumUtils.isValidEnum(MediaType.class, v))
                    .map(v -> Enum.valueOf(MediaType.class, v))
                    .collect(Collectors.toSet());

            ir = new ImportRule(location, mediaTypes, new CssLoader(), stylesheet);

            return ir;
        }
    	catch(BadImportStatement bis)
    	{
    		tok.advancePast(TokenType.CR_PUNCT, ";");
    		throw bis;
    	}
    	catch(IllegalArgumentException iae)
    	{
    		throw new BadImportStatement(iae);
    	}
    }

	PageRule process_page() throws CSSParserException, MalformedURLException, IOException
	{
			PageRule pr=null;
			List<Declaration> decs;

			pr=new PageRule();
			pr.instruction="@page";

			tok.advance();

			if(tok.curr.syntax.equals(":"))
			{
					tok.advance();

					if(tok.curr.type==TokenType.CR_IDENT)
					{
							if(tok.curr.syntax.equals("left"))
									pr.type=PseudoPageType.PPT_LEFT;
							else if(tok.curr.syntax.equals("right"))
									pr.type=PseudoPageType.PPT_RIGHT;
							else
									pr.type=PseudoPageType.PPT_NONE;
					}

					tok.advance();
			}
			else
					pr.type=PseudoPageType.PPT_NONE;

			if(!tok.curr.syntax.equals("{"))
				throw new CSSParserException(CSSParserException.Type.PAGE_RULE_RULE_SYNTAX, tok.curr.syntax);

			tok.advance();

			decs=parse_declaration();
			pr.declarations.addAll(decs);

			while(tok.curr.syntax.equals(";"))
			{
					tok.advance();

					decs=parse_declaration();
					pr.declarations.addAll(decs);
			}

			if(!tok.curr.syntax.equals("}"))
				throw new CSSParserException(CSSParserException.Type.PAGE_RULE_RULE_SYNTAX, tok.curr.syntax);

			return pr;
	}

	MediaType getMediaType(String mt)
	{
			MediaType media;

			if(mt.equals("all"))
					media=MediaType.MT_ALL;
			else if(mt.equals("screen"))
					media=MediaType.MT_SCREEN;
			else if(mt.equals("strong"))
					media=MediaType.MT_STRONG;
			else if(mt.equals("braille"))
					media=MediaType.MT_BRAILLE;
			else if(mt.equals("embossed"))
					media=MediaType.MT_EMBOSSED;
			else if(mt.equals("handheld"))
					media=MediaType.MT_HANDHELD;
			else if(mt.equals("print"))
					media=MediaType.MT_PRINT;
			else if(mt.equals("projection"))
					media=MediaType.MT_PROJECTION;
			else if(mt.equals("speech"))
					media=MediaType.MT_SPEECH;
			else if(mt.equals("tty"))
					media=MediaType.MT_TTY;
			else if(mt.equals("tv"))
					media=MediaType.MT_TV;
			else
					media=MediaType.MT_NONE;

			return media;
	}

	/*
	media :
			@media S* ident S* (comma S* ident S*)* lbrace S* ruleset* S* rbrace S*
	*/
	List<StyleRule> parse_media() throws CSSParserException, MalformedURLException, IOException
	{
			List<StyleRule> srs=new ArrayList<StyleRule>();
			StyleRule sr;
			List<MediaType> media=new ArrayList<MediaType>();
			MediaType mt;

			tok.advance();

			if(tok.curr.type!=TokenType.CR_IDENT)
				throw new CSSParserException(CSSParserException.Type.MEDIA_RULE_RULE_SYNTAX, tok.curr.syntax);

			mt=getMediaType(tok.curr.syntax);
			if(mt!=MediaType.MT_NONE)
					media.add(mt);

			tok.advance();
			while(tok.curr.syntax.equals(","))
			{
					tok.advance();

					if(tok.curr.type!=TokenType.CR_IDENT)
						throw new CSSParserException(CSSParserException.Type.MEDIA_RULE_RULE_SYNTAX, tok.curr.syntax);

					mt=getMediaType(tok.curr.syntax);
					if(mt!=MediaType.MT_NONE)
							media.add(mt);

					tok.advance();
			}

			if(!tok.curr.syntax.equals("{"))
				throw new CSSParserException(CSSParserException.Type.MEDIA_RULE_RULE_SYNTAX, tok.curr.syntax);

			tok.advance();

			while(!tok.curr.syntax.equals("}"))
			{
				srs.addAll(parse_ruleset(EnumSet.copyOf(media)));
			}

			tok.advance();

			return srs;
	}


	//statement   : ruleset | at-rule;
	List<StyleRule> parse_statement(Stylesheet stylesheet) throws CSSParserException, MalformedURLException, IOException
	{
		List<StyleRule> rs=new ArrayList<>();

		try
		{
			boolean rulesEncountered=false;
	
			if(tok.curr.syntax.equals("@"))
			{
				tok.advance();
	
				if(tok.curr.syntax.equals("charset"))
				{}
				else if(tok.curr.syntax.equals("import"))
				{
					ImportRule ir;
					Stylesheet importedStylesheet;
	
					ir=processImport(stylesheet);
					if(!rulesEncountered)
					{
                        // TODO usingMediaTypesOf????
						importedStylesheet=ir.loadStylesheet(Collections.emptySet());

                        logger.debug("parseStatement returning "+importedStylesheet);

						return ImmutableList.copyOf(importedStylesheet.getRuleset());
					}
				}
	//				else if(tok.curr.syntax.equals("media"))
	//				{
	//						rs=parse_media();
	//				}
	//				else if(tok.curr.syntax.equals("page"))
	//				{
	//						r=process_page();
	//						rs.add(r);
	//				}
			}
			else
			{
				rs.addAll(parse_ruleset(EnumSet.of(MediaType.MT_ALL)));

                logger.debug("parseStatement() returning ruleset "+rs);

                rulesEncountered=true;
			}
		}
		catch(BadImportStatement bis)
		{
			logger.debug("ignoring import statement");
		}
		
		return rs;
	}

	// [ CDO | CDC | S | statement ]*;
	public List<Declaration> parse_declist() throws CSSParserException, MalformedURLException, IOException
	{
			List<Declaration> allDecs=new ArrayList<>();
			List<Declaration> d=new ArrayList<>();

			tok.advance();
			while(tok.curr.type!=TokenType.CR_END)
			{
                    while(tok.curr.syntax.equals(";"))
                            tok.advance();

					if(tok.curr.type==TokenType.CR_CD)
					{
							tok.advance();
					}
					else
					{
							d=parse_declaration();
							allDecs.addAll(d);
					}
			}

			return allDecs;
	}

	// num  [0-9]+|[0-9]*\.[0-9]+
	double extractDouble(String syntax) throws IOException
	{
		StreamTokenizer st=new StreamTokenizer(new StringReader(syntax));
		Number n=null;
		BigDecimal bd;

		st.nextToken();
		if(st.ttype==StreamTokenizer.TT_NUMBER)
		{
			return st.nval;
		}

		return 0;
	}

	
    //if 96dpi
    //1px = 1/96 inch = 0.26mm
    //1 inch = 2.54cm
    //in: inches - 1 inch is equal to 2.54 centimeters.
    //cm: centimeters
    //mm: millimeters
    //pt: points - the points used by CSS 2.1 are equal to 1/72th of an inch.
    //pc: picas - 1 pica is equal to 12 points.
	
	//DIMENSION  {num}{ident}
	Value extractDimension(String syntax) throws IOException
	{
		StreamTokenizer st=new StreamTokenizer(new StringReader(syntax));
		double amount;
		String unit;

		st.nextToken();
		if(st.ttype==StreamTokenizer.TT_NUMBER)
		{
			amount=st.nval;
			
			st.nextToken();
			if(st.ttype==StreamTokenizer.TT_WORD)
				unit=st.sval;
			
			switch(st.sval)
			{
			case "px":
				return new NumericValue(amount, "px");
			case "cm":
				return new NumericValue(amount, "cm");
			case "em":
				return new NumericValue(amount, "em");
			case "mm":
				return new NumericValue(amount, "mm");
			case "pt":
				return new NumericValue(amount, "pt");
			case "ex":
				return new NumericValue(amount, "ex");
			case "pc":
				return new NumericValue(amount, "pc");
			default:
				return new NumericValue(amount, "px");
			}
		}
		
		Preconditions.checkState(false);
		return null;
	}

	// PERCENTAGE  {num}%
	NumericValue extractPercentage(String syntax) throws IOException
	{
			StreamTokenizer st=new StreamTokenizer(new StringReader(syntax));
			NumericValue pv=null;
			BigDecimal bd;

			st.nextToken();
			if(st.ttype==StreamTokenizer.TT_NUMBER)
			{
				pv=new NumericValue(st.nval, "%");
			}

			return pv;
	}

}

