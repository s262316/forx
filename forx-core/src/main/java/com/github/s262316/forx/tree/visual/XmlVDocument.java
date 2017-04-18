package com.github.s262316.forx.tree.visual;

import com.github.s262316.forx.common.ApplicationConfigException;
import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.CSSParserException;
import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.css.CssLoader;
import com.github.s262316.forx.tree.DocumentTypeDecl;
import com.github.s262316.forx.tree.XmlDocument;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.tree.build.XmlDocumentBuilder;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.StyleRule;
import com.github.s262316.forx.style.Stylesheet;
import com.github.s262316.forx.style.StylesheetFactory;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoClassType;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.util.Selectors;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class XmlVDocument extends XmlDocument
{
    private CssLoader cssLoader;
    private CSSPropertiesReference cssPropertiesReference;
    private Stylesheet agentStylesheet;
    private Stylesheet userNormalStylesheet;
    private Stylesheet authorNormalStylesheet;
    private Stylesheet authorImportantStylesheet;
    private Stylesheet userImportantStylesheet;
    private ListMultimap<XmlNode, Stylesheet> styleSource = ArrayListMultimap.create();
    private String defaultStyleLanguage="text/com.github.s262316.forx.css";

    public XmlVDocument(DocumentTypeDecl dtd, XmlDocumentBuilder builder, EventDispatcher eventDispatcher, URL location, CSSPropertiesReference cssPropertiesReference) throws ApplicationConfigException
    {
        super(dtd, builder, eventDispatcher, location);

        this.cssPropertiesReference=cssPropertiesReference;

        userNormalStylesheet= StylesheetFactory.createEmptyStylesheet();
        authorNormalStylesheet=StylesheetFactory.createEmptyStylesheet();
        authorImportantStylesheet=StylesheetFactory.createEmptyStylesheet();
        userImportantStylesheet=StylesheetFactory.createEmptyStylesheet();

        cssLoader=new CssLoader(() -> com.google.common.base.Optional.absent());

        try
        {
            // TODO difference com.github.s262316.forx.css for xhtml/html docs
            File userAgentCss= ResourceUtils.getFile("classpath:com/github/s262316/forx/com.github.s262316.forx.css/user_agent.com.github.s262316.forx.css");
            String cssData= FileUtils.readFileToString(userAgentCss, StandardCharsets.UTF_8);

            CSSParser parser=new CSSParser(cssData, this, cssPropertiesReference);
            agentStylesheet=parser.parse_stylesheet();
        }
        catch(CSSParserException | IOException e)
        {
            throw new ApplicationConfigException(e);
        }
    }

    public void mergeStyles(XmlNode source, Stylesheet ss)
    {
        styleSource.put(source, ss);
        refreshStyles();
    }

    public void demergeStylesFrom(XmlNode source)
    {
        styleSource.removeAll(source);
        refreshStyles();
    }

    private void refreshStyles()
    {
        authorNormalStylesheet.reset();
        authorImportantStylesheet.reset();

        for(Map.Entry<XmlNode, Stylesheet> e : styleSource.entries())
        {
            authorNormalStylesheet.mergeNormalRules(e.getValue());
            authorImportantStylesheet.mergeImportantRules(e.getValue());
        }
    }

    public void setDefaultStyleLanguage(String language)
    {
        defaultStyleLanguage=language;
    }

    public String getDefaultStyleLanguage()
    {
        return defaultStyleLanguage;
    }

    public Value getPropertyValue(XmlElement element, String propertyName, MediaType mediaType, PseudoElementType pseudoType)
    {
        Declaration d;
        Value v=null;

        d=userImportantStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
        if(d==null)
        {
            d=authorImportantStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
            if(d==null)
            {
                d=authorNormalStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
                if(d==null)
                {
                    d=userNormalStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
                    if(d==null)
                    {
                        d=agentStylesheet.findDeclaration(element, propertyName, mediaType, pseudoType);
                    }
                }
            }
        }

        if(d!=null)
            v=d.getValue();

        return v;
    }

    public boolean isSensitiveToPseudoElement(XmlElement element, MediaType mediaType, PseudoElementType pseudoType, PseudoClassType pseudoClass)
    {
        List<StyleRule> rules;

        rules=userImportantStylesheet.findAllRules(element, mediaType, pseudoType);
        if(rules.isEmpty())
        {
            rules=authorImportantStylesheet.findAllRules(element, mediaType, pseudoType);
            if(rules.isEmpty())
            {
                rules=authorNormalStylesheet.findAllRules(element, mediaType, pseudoType);
                if(rules.isEmpty())
                {
                    rules=userNormalStylesheet.findAllRules(element, mediaType, pseudoType);
                    if(rules.isEmpty())
                    {
                        rules=agentStylesheet.findAllRules(element, mediaType, pseudoType);
                    }
                }
            }
        }

        return !rules.isEmpty();
    }

    public boolean isSensitiveToPseudoClass(XmlElement element, MediaType mediaType, PseudoElementType pseudoType, PseudoClassType pseudoClass)
    {
        Collection<StyleRule> allRules= Lists.newArrayList();
        Collection<StyleRule> rules;

        Preconditions.checkArgument(pseudoClass!=PseudoClassType.PCT_NO_CLASS, "isSensitiveTo not appropriate with PCT_NO_CLASS");
        Preconditions.checkArgument(pseudoClass!=PseudoClassType.PCT_FIRST_CHILD, "isSensitiveTo not appropriate with PCT_FIRST_CHILD");
        Preconditions.checkArgument(pseudoClass!=PseudoClassType.PCT_LANG, "isSensitiveTo not appropriate with PCT_LANG");

        rules=userImportantStylesheet.findAllRules(element, mediaType, pseudoType);
        allRules.addAll(rules);
        rules=authorImportantStylesheet.findAllRules(element, mediaType, pseudoType);
        allRules.addAll(rules);
        rules=authorNormalStylesheet.findAllRules(element, mediaType, pseudoType);
        allRules.addAll(rules);
        rules=userNormalStylesheet.findAllRules(element, mediaType, pseudoType);
        allRules.addAll(rules);
        rules=agentStylesheet.findAllRules(element, mediaType, pseudoType);
        allRules.addAll(rules);

        for(StyleRule sr : allRules)
        {
            if(Selectors.containsPseudoType(sr.getSelector())==true)
                return true;
        }

        return false;
    }

    public Value getPropertyValue(XmlElement element, String propertyName, MediaType mediaType)
    {
        return getPropertyValue(element, propertyName, mediaType, PseudoElementType.PE_NOT_PSEUDO);
    }


    public CssLoader getCssLoader()
    {
        return cssLoader;
    }
}
