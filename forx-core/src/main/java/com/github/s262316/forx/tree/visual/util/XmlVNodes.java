package com.github.s262316.forx.tree.visual.util;

import com.github.s262316.forx.tree.XNode;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.XmlVElement;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

public class XmlVNodes
{
    /**
     *
     * @param target
     * @return top is first in the returned list. i.e. document
     */
    public static List<XmlVElement> pathToHere(XmlVElement target)
    {
        LinkedList<XmlVElement> path= Lists.newLinkedList();
        XNode parent=target;

        while(parent!=null)
        {
            if(parent.getClass().equals(XmlVElement.class))
                path.addFirst((XmlVElement)parent);
            parent=parent.parentNode();
        }

        return path;
    }

}
