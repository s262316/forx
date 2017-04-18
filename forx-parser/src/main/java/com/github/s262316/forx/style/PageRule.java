/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.style;

import java.util.List;

public class PageRule extends AtRule
{
	public String instruction;
    public PseudoPageType type;
    public List<Declaration> declarations;

    @Override
    public String toString()
    {
        String str="";
	str += "type :"+type + "\n";
	for(Declaration d : declarations)
            str+=d.toString();

        return str;
    }
}
