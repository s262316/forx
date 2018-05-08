/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.style.selectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Operator extends SelectorPart
{
	public String op;

    public Operator()
    {
	}	

    public Operator(String op)
    {
		this.op = op;
	}

	@Override
    public String toString()
    {
		return MoreObjects.toStringHelper(this).addValue(op).toString();
    }

}
