package com.github.s262316.forx.box.util;

import java.awt.Color;

public class BorderDescriptor
{
	public int borderLeftWidth, borderRightWidth, borderTopWidth, borderBottomWidth;
	public Color borderLeftColour, borderRightColour, borderTopColour, borderBottomColour;
	public BorderStyle borderLeftStyle, borderRightStyle, borderTopStyle, borderBottomStyle;
	public int paddingLeftWidth, paddingRightWidth, paddingTopWidth, paddingBottomWidth;

	public BorderDescriptor()
	{
		borderLeftWidth=0;
		borderRightWidth=0;
		borderTopWidth=0;
		borderBottomWidth=0;
		borderLeftColour=null;
		borderRightColour=null;
		borderTopColour=null;
		borderBottomColour=null;
		borderLeftStyle=BorderStyle.BS_NONE;
		borderRightStyle=BorderStyle.BS_NONE;
		borderTopStyle=BorderStyle.BS_NONE;
		borderBottomStyle=BorderStyle.BS_NONE;
		paddingLeftWidth=0;
		paddingRightWidth=0;
		paddingTopWidth=0;
		paddingBottomWidth=0;
	}
}
