package com.github.s262316.forx.tree.visual;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Node
{
    @XmlElements({
		@XmlElement(name="body", type=BlockNode.class),
        @XmlElement(name="p", type=BlockNode.class),
        @XmlElement(name="div", type=BlockNode.class),
        @XmlElement(name="span", type=InlineNode.class),
        @XmlElement(name="b", type=InlineNode.class),
        @XmlElement(name="i", type=InlineNode.class),
        @XmlElement(name="u", type=InlineNode.class)
	})
	List<Node> nodes=new ArrayList<>();
}
