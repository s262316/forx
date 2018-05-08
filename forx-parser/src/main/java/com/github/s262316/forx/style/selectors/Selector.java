/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.style.selectors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.s262316.forx.css.StyleXNodes;
import com.github.s262316.forx.tree.NodeType;
import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.style.selectors.util.Selectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class Selector
{
    private List<SelectorPart> parts=new ArrayList<SelectorPart>();
    private Specificity specificity;

    public Selector(List<SelectorPart> parts)
    {
    	this.parts=parts;
    	this.specificity=Selectors.calculate_specificity(this);
    }
    
    public Specificity getSpecificity()
	{
		return specificity;
	}

	public boolean isMatch(XElement e, PseudoElementType pseudoType)
    {
		SelectorPart sp;
		Operator op;
		XElement lastMatchedNode=e;
		boolean carry_on=true, found;

        Iterator<SelectorPart> it=Lists.reverse(parts).iterator();

        sp=it.next();
		found=sp.isMatch(lastMatchedNode, pseudoType);
		if(found==true)
		{
            while(it.hasNext() && carry_on==true)
            {
                found=false;
                op=(Operator)it.next();
                sp=it.next();

                if(op.op.equals(" "))
                {
                	List<XmlNode> pathToRoot=Lists.reverse(XNodes.pathToHere((XmlNode)lastMatchedNode));
                	Iterator<XElement> pit=Iterators.filter(pathToRoot.listIterator(1), XElement.class);
                	
                	while(pit.hasNext() && !found)
                	{
                		lastMatchedNode=(XElement)pit.next();
                		// the parent elements are not pseudos. TODO should we pass in lastMatchedNode.pseudoType() instead?
                   		found=sp.isMatch(lastMatchedNode, PseudoElementType.PE_NOT_PSEUDO);
                	}
                }
                else if(op.op.equals(">"))
                {
                    if(lastMatchedNode.parentNode().type()==NodeType.X_ELEMENT)
                    {
                    	lastMatchedNode=(XElement)lastMatchedNode.parentNode();
                        // the parent elements are not pseudos. TODO should we pass in lastMatchedNode.pseudoType() instead?
                        if(sp.isMatch(lastMatchedNode, PseudoElementType.PE_NOT_PSEUDO))
                            found=true;
                    }
                }
                else if(op.op.equals("+"))
                {
                	lastMatchedNode=(XElement) StyleXNodes.previous(lastMatchedNode);
                    if(lastMatchedNode!=null)
                    {
                        // the sibling elements are not pseudos. TODO should we pass in lastMatchedNode.pseudoType() instead?
                        if(sp.isMatch(lastMatchedNode, PseudoElementType.PE_NOT_PSEUDO))
                            found=true;
                    }
                }

                carry_on=found;
            }
		}
	
		return found;
    }

	public List<SelectorPart> getParts()
	{
		return parts;
	}

	
	
	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			.addValue(parts)
			.toString();
	}
}

