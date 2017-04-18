/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.style.selectors;

import com.github.s262316.forx.tree.XElement;

public abstract class SelectorPart
{
    public boolean isMatch(XElement e, PseudoElementType pseudoType)
    {
        return false;
    }
}
