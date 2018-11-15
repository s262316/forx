package com.github.s262316.forx.box.util;

public class PositionDescriptor
{

    public Length left, right, top, bottom;
    public Length zIndex;

    public PositionDescriptor()
    {
        left=new Length(SpecialLength.SL_AUTO);
        right=new Length(SpecialLength.SL_AUTO);
        top=new Length(SpecialLength.SL_AUTO);
        bottom=new Length(SpecialLength.SL_AUTO);
        zIndex=new Length(SpecialLength.SL_AUTO);
    }
}
