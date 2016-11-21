/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.validate;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import com.github.s262316.forx.tree.style.Value;

public class OrValidator implements PropertyValidator
{
    protected List<PropertyValidator> subvalidators=Lists.newArrayList();

    public OrValidator()
    {}

    @Override
    public boolean validate(Value value)
    {
        boolean result=false;

        Iterator<PropertyValidator> it=subvalidators.iterator();
        while(it.hasNext() && result==false)
            result=it.next().validate(value);

        return result;
    }
}

