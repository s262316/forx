package com.github.s262316.forx.tree.build;

import java.util.List;
import java.util.Map;
import com.github.s262316.forx.tree.XmlNode;

public class ElementKey
{
    public String name;
    public List<XmlNode> members;
    public Map<String, AttributeKey> attrs;
    public boolean whitespaceIsSignificant;
    public String lang;
}
