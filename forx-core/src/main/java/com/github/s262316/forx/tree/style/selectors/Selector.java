/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.style.selectors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.s262316.forx.core.StyleXNodes;
import com.github.s262316.forx.tree.NodeType;
import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.style.util.Selectors;

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
                   		found=sp.isMatch(lastMatchedNode, pseudoType);
                	}
                }
                else if(op.op.equals(">"))
                {
                    if(lastMatchedNode.parentNode().type()==NodeType.X_ELEMENT)
                    {
                    	lastMatchedNode=(XElement)lastMatchedNode.parentNode();
                        if(sp.isMatch(lastMatchedNode, pseudoType))
                            found=true;
                    }
                }
                else if(op.op.equals("+"))
                {
                	lastMatchedNode=(XElement)StyleXNodes.previous(lastMatchedNode);
                    if(lastMatchedNode!=null)
                    {
                        if(sp.isMatch(lastMatchedNode, pseudoType))
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
		return Objects.toStringHelper(this)
			.addValue(parts)
			.toString();
	}
}
