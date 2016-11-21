package com.github.s262316.forx.tree;

public class TreePath
{
    private XNode array[];
    private int size;

    public TreePath()
    {
        this.array=null;
        this.size=0;    
    }

    public TreePath(XNode a[], int s)
    {
        this.array=a;
        this.size=s;
    }

    public XNode get(int n)
    {
            return array[n];
    }

    public int length()
    {
        return size;
    }

}
