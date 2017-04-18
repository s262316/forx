package com.github.s262316.forx.tree;

public class XmlComment extends XmlNode implements XComment
{
    private String text;

    public XmlComment(String t, XmlDocument doc, int id)
    {
        super(doc, id);
        text=t;
    }

    @Override
    public NodeType type()
    {
            return NodeType.X_COMMENT;
    }

    @Override
    public String getName()
    {
            return "";
    }

    @Override
    public String getValue()
    {
            return text;
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

