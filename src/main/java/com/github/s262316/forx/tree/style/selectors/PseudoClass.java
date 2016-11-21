/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.style.selectors;

public class PseudoClass
{
    PseudoClassType type;
    String info;

    public PseudoClass(PseudoClassType t, String i)
    {
        type=t;
        info=i;
    }

    public PseudoClass(PseudoClassType t)
    {
        type=t;
        info="";
    }
}
