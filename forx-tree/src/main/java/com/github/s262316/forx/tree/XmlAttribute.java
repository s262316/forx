package com.github.s262316.forx.tree;

public class XmlAttribute extends XmlNode implements XAttribute
{
    private String name, value;

    public XmlAttribute(String n, String v, XmlDocument doc, int id)
    {
        super(doc, id);
        name=n;
        value=v;
    }

    @Override
    public NodeType type()
    {
            return NodeType.X_ATTR;
    }

    @Override
    public String getName()
    {
            return name;
    }

    @Override
    public String getValue()
    {
            return value;
    }

    public void add(XmlNode node)
    {
    }

    public void insert(XmlNode newNode, XmlNode beforeNode)
    {

    }

    public void remove(XmlNode node)
    {

    }

}
