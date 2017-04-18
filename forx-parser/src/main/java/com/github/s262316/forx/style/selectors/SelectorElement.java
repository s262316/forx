package com.github.s262316.forx.style.selectors;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.s262316.forx.css.StyleXNodes;
import com.github.s262316.forx.css.VisualConstants;
import com.github.s262316.forx.css.VisualState;
import com.github.s262316.forx.tree.XAttribute;
import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.style.selectors.util.Selectors;

import com.google.common.base.Objects;

public class SelectorElement extends SelectorPart
{
    public String name;
    public List<SelectorAttr> attrs;
    public List<PseudoClass> pseudoClasses;
    public List<PseudoElementType> pseudoElements; // should this be a single PseudoElementType?
    public List<String> ids;

    public SelectorElement(String name)
    {
        this(name, Collections.<SelectorAttr>emptyList(), Collections.<PseudoClass>emptyList(), Collections.<PseudoElementType>emptyList(), Collections.<String>emptyList());
    }

    public SelectorElement(String name, List<SelectorAttr> attrs, List<PseudoClass> pseudoClasses, List<PseudoElementType> pseudoElements, List<String> ids)
    {
		this.name = name;
		this.attrs = attrs;
		this.pseudoClasses = pseudoClasses;
		this.pseudoElements = pseudoElements;
		this.ids = ids;
	}

	public boolean attributeMatch(XElement e)
    {
        boolean match=true;

        Iterator<SelectorAttr> it=attrs.iterator();
        while(it.hasNext() && match==true)
            match=it.next().apply(e);

        return match;
    }

    @Override
    public boolean isMatch(XElement e, PseudoElementType pseudoType)
    {
        boolean matches=false;

        if(e.getName().equals(name) || name.equals("*"))
        {
            if(attributeMatch(e)==true)
            {
                matches=true;

                // check pseudo classes
                if(pseudoClasses.size()==0)
                    matches=true;
                else
                {
                	VisualState state=e.getProperty(VisualConstants.VISUAL_STATE, VisualState.class);
                    Iterator<PseudoClass> it=pseudoClasses.iterator();
                    while(it.hasNext()&&matches==true)
                    {
                        PseudoClass pc=it.next();

                        switch(pc.type)
                        {
                            case PCT_FIRST_CHILD:
                            {
                            	// if this is the first child of its parent
                                if(StyleXNodes.firstMember(e.parentNode())!=e)
                                    matches=false;

                                break;
                            }
                            case PCT_LINK:
                                if(!state.isLink())
                                    matches=false;

                                break;
                            case PCT_VISITED:
                                if(!state.isVisited())
                                    matches=false;

                                break;
                            case PCT_HOVER:
                                if(!state.isHover())
                                    matches=false;

                                break;
                            case PCT_ACTIVE:
                                if(!state.isActive())
                                    matches=false;

                                break;
                            case PCT_FOCUS:
                                if(!state.isFocus())
                                    matches=false;

                                break;
                            case PCT_LANG:
                            {
                            	matches=Selectors.dashmatch(pc.info, e.lang());
                            	
                                break;
                            }
                            default:
                                matches=false;
                                break;
                        }
                    }
                }

                if(matches==true)
                {
                    // is it possible to have multiple pseudo-elements?????
                	// let's assume it's not. and the selector checks
                	// for the presence of pseudoType.
                	
                	// PE_NOT_PSEUDO must have ZERO pseudoElements

                	// check pseudo elements
                    if(pseudoElements.size()==0 && pseudoType==PseudoElementType.PE_NOT_PSEUDO)
                        matches=true;
                    else
                    {
                        if(!pseudoElements.contains(pseudoType))
                        	matches=false;
                    }

                    // check IDs
                    Iterator<String> it=ids.iterator();
                    while(it.hasNext()&&matches==true)
                    {
                        XAttribute a=e.getAttr("id");
                        if(a!=null)
                        {
                            if(!it.next().equals(a.getValue()))
                                matches=false;
                        }
                        else
                            matches=false;
                    }
                }
            }
        }


        return matches;
    }

    @Override
    public String toString()
    {
		return Objects.toStringHelper(this)
				.add("name", name)
				.add("attrs", attrs)
				.add("pseudoClasses", pseudoClasses)
				.add("pseudoElements", pseudoElements)
				.add("ids", ids)
				.toString();
    }
}

