package com.github.s262316.forx.css.util;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.tree.style.ColourValue;
import com.github.s262316.forx.tree.style.FunctionValue;
import com.github.s262316.forx.tree.style.HashValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.Value;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public class CSSColours
{
	private final static Logger logger=LoggerFactory.getLogger(CSSColours.class);

	public static Color colourValueToColor(Value v)
	{
		Preconditions.checkArgument(v instanceof ColourValue);

		ColourValue cv=(ColourValue)v;
		
		return new Color(cv.r, cv.b, cv.g);
	}
	
	public static Color colourNameToColor(Value name)
	{
		Preconditions.checkArgument(name instanceof Identifier);

		String colourName=((Identifier)name).ident;
		if(!COLOUR_NAMES.containsKey(colourName))
			throw new IllegalArgumentException("unsupported colour name "+colourName);

		ColourValue cv=COLOUR_NAMES.get(colourName);
		
		return new Color(cv.r, cv.b, cv.g);
	}
	
	public static Color hashValueToColor(Value hashValue)
	{
		Preconditions.checkArgument(hashValue instanceof HashValue);
		
		String v=((HashValue)hashValue).str();
		int r, g, b;
		Color col=null;
		
		if(v.length()==7)
		{
			r=Integer.parseInt(v.substring(1, 3), 16);
			g=Integer.parseInt(v.substring(3, 5), 16);
			b=Integer.parseInt(v.substring(5), 16);

			col=new Color(r, g, b);
		}
		else if(v.length()==4)
		{
			r=17*Integer.parseInt(v.substring(1, 2), 16);
			g=17*Integer.parseInt(v.substring(2, 3), 16);
			b=17*Integer.parseInt(v.substring(3), 16);

			col=new Color(r, g, b);
		}
		else
			Preconditions.checkArgument(false, "invalid colour hash");

		return col;
	}
	
	public static Color rgbFunctionToColor(Value rgbFunction)
	{
		Preconditions.checkArgument(rgbFunction instanceof FunctionValue);

		FunctionValue fv=(FunctionValue)rgbFunction;
		int r, g, b;
		Color col;

		//rgb(255,0,0)       integer range 0 - 255
		//rgb(300,0,0)       clipped to rgb(255,0,0)
		//rgb(255,-10,0)     clipped to rgb(255,0,0)
		//rgb(110%, 0%, 0%)  clipped to rgb(100%,0%,0%)

		r=workOutColourPart(fv.values.members.get(0));
		g=workOutColourPart(fv.values.members.get(1));
		b=workOutColourPart(fv.values.members.get(2));

		col=new Color(r, g, b);

		return col;
	}
	
	@Deprecated
	public static ColourValue normalise(FunctionValue fv)
	{
		int r, g, b;
		ColourValue cv;

		//rgb(255,0,0)       integer range 0 - 255
		//rgb(300,0,0)       clipped to rgb(255,0,0)
		//rgb(255,-10,0)     clipped to rgb(255,0,0)
		//rgb(110%, 0%, 0%)  clipped to rgb(100%,0%,0%)

		r=workOutColourPart(fv.values.members.get(0));
		g=workOutColourPart(fv.values.members.get(1));
		b=workOutColourPart(fv.values.members.get(2));

		cv=new ColourValue(r, g, b);

		return cv;
	}

	@Deprecated
	public static ColourValue normalise(String v)
	{
		logger.debug("normalise("+v+")");

		boolean ok=false;
		ColourValue col=null;

		if(v.charAt(0)=='#')
		{
			int r, g, b;

			if(v.length()==7)
			{
				r=Integer.parseInt(v.substring(1, 3), 16);
				g=Integer.parseInt(v.substring(3, 5), 16);
				b=Integer.parseInt(v.substring(5), 16);

				col=new ColourValue(r, g, b);
				ok=true;
			}
			else if(v.length()==4)
			{
				r=17*Integer.parseInt(v.substring(1, 2), 16);
				g=17*Integer.parseInt(v.substring(2, 3), 16);
				b=17*Integer.parseInt(v.substring(3), 16);

				col=new ColourValue(r, g, b);
				ok=true;
			}
		}
		else if(COLOUR_NAMES.containsKey(v))
		{
			logger.debug("containsKey yes");

			col=COLOUR_NAMES.get(v);

			logger.debug(col.toString());

			ok=true;
		}

		return col;
	}

	private static int workOutColourPart(Value v)
	{
		int part=0;

		if(v.getClass().equals(NumericValue.class))
		{
			com.github.s262316.forx.tree.style.NumericValue nv=(com.github.s262316.forx.tree.style.NumericValue)v;
			part=(int)nv.amount;
			if(part>255)
				part=255;
			else if(part<0)
				part=0;
		}
//		else if(v.getClass().equals(PercentageValue.class))
//		{
//			PercentageValue pv=(PercentageValue)v;
//			int percentage;
//
//			percentage=pv.major;
//			if(pv.major>100)
//				percentage=100;
//			else if(pv.major<0)
//				percentage=0;
//
//			part=255*(percentage/100);
//		}

		return part;
	}

	public static final ImmutableMap<String, ColourValue> COLOUR_NAMES = new ImmutableMap.Builder<String, ColourValue>()
		.put("aliceblue", new ColourValue(240,248,255))
		.put("antiquewhite", new ColourValue(250,235,215))
		.put("aqua", new ColourValue(0,255,255))
		.put("aquamarine", new ColourValue(127,255,212))
		.put("azure", new ColourValue(240,255,255))
		.put("beige", new ColourValue(245,245,220))
		.put("bisque", new ColourValue(255,228,196))
		.put("black", new ColourValue(0,0,0))
		.put("blanchedalmond", new ColourValue(255,235,205))
		.put("blue", new ColourValue(0,0,255))
		.put("blueviolet", new ColourValue(138,43,226))
		.put("brown", new ColourValue(165,42,42))
		.put("burlywood", new ColourValue(222,184,135))
		.put("cadetblue", new ColourValue(95,158,160))
		.put("chartreuse", new ColourValue(127,255,0))
		.put("chocolate", new ColourValue(210,105,30))
		.put("cora", new ColourValue(255,127,80))
		.put("cornflowerblue", new ColourValue(100,149,237))
		.put("cornsilk", new ColourValue(255,248,220))
		.put("crimson", new ColourValue(220,20,60))
		.put("cyan", new ColourValue(0,255,255))
		.put("darkblue", new ColourValue(0,0,139))
		.put("darkcyan", new ColourValue(0,139,139))
		.put("darkgoldenrod", new ColourValue(184,134,11))
		.put("darkgray", new ColourValue(169,169,169))
		.put("darkgreen", new ColourValue(0,100,0))
		.put("darkkhaki", new ColourValue(189,183,107))
		.put("darkmagenta", new ColourValue(139,0,139))
		.put("darkolivegreen", new ColourValue(85,107,47))
		.put("darkorange", new ColourValue(255,140,0))
		.put("darkorchid", new ColourValue(153,50,204))
		.put("darkred", new ColourValue(139,0,0))
		.put("darksalmon", new ColourValue(233,150,122))
		.put("darkseagreen", new ColourValue(143,188,143))
		.put("darkslateblue", new ColourValue(72,61,139))
		.put("darkslategray", new ColourValue(47,79,79))
		.put("darkturquoise", new ColourValue(0,206,209))
		.put("darkviolet", new ColourValue(148,0,211))
		.put("deeppink", new ColourValue(255,20,147))
		.put("deepskyblue", new ColourValue(0,191,255))
		.put("dimgray", new ColourValue(105,105,105))
		.put("dodgerblue", new ColourValue(30,144,255))
		.put("feldspar", new ColourValue(209,146,117))
		.put("firebrick", new ColourValue(178,34,34))
		.put("floralwhite", new ColourValue(255,250,240))
		.put("forestgreen", new ColourValue(34,139,34))
		.put("fuchsia", new ColourValue(255,0,255))
		.put("gainsboro", new ColourValue(220,220,220))
		.put("ghostwhite", new ColourValue(248,248,255))
		.put("gold", new ColourValue(255,215,0))
		.put("goldenrod", new ColourValue(218,165,32))
		.put("gray", new ColourValue(128,128,128))
		.put("green", new ColourValue(0,128,0))
		.put("greenyellow", new ColourValue(173,255,47))
		.put("honeydew", new ColourValue(240,255,240))
		.put("hotpink", new ColourValue(255,105,180))
		.put("indianred", new ColourValue(205,92,92))
		.put("indigo", new ColourValue(75,0,130))
		.put("ivory", new ColourValue(255,255,240))
		.put("khaki", new ColourValue(240,230,140))
		.put("lavender", new ColourValue(230,230,250))
		.put("lavenderblush", new ColourValue(255,240,245))
		.put("lawngreen", new ColourValue(124,252,0))
		.put("lemonchiffon", new ColourValue(255,250,205))
		.put("lightblue", new ColourValue(173,216,230))
		.put("lightcora", new ColourValue(240,128,128))
		.put("lightcyan", new ColourValue(224,255,255))
		.put("lightgoldenrodyellow", new ColourValue(250,250,210))
		.put("lightgrey", new ColourValue(211,211,211))
		.put("lightgreen", new ColourValue(144,238,144))
		.put("lightpink", new ColourValue(255,182,193))
		.put("lightsalmon", new ColourValue(255,160,122))
		.put("lightseagreen", new ColourValue(32,178,170))
		.put("lightskyblue", new ColourValue(135,206,250))
		.put("lightslateblue", new ColourValue(132,112,255))
		.put("lightslategray", new ColourValue(119,136,153))
		.put("lightsteelblue", new ColourValue(176,196,222))
		.put("lightyellow", new ColourValue(255,255,224))
		.put("lime", new ColourValue(0,255,0))
		.put("limegreen", new ColourValue(50,205,50))
		.put("linen", new ColourValue(250,240,230))
		.put("magenta", new ColourValue(255,0,255))
		.put("maroon", new ColourValue(128,0,0))
		.put("mediumaquamarine", new ColourValue(102,205,170))
		.put("mediumblue", new ColourValue(0,0,205))
		.put("mediumorchid", new ColourValue(186,85,211))
		.put("mediumpurple", new ColourValue(147,112,216))
		.put("mediumseagreen", new ColourValue(60,179,113))
		.put("mediumslateblue", new ColourValue(123,104,238))
		.put("mediumspringgreen", new ColourValue(0,250,154))
		.put("mediumturquoise", new ColourValue(72,209,204))
		.put("mediumvioletred", new ColourValue(199,21,133))
		.put("midnightblue", new ColourValue(25,25,112))
		.put("mintcream", new ColourValue(245,255,250))
		.put("mistyrose", new ColourValue(255,228,225))
		.put("moccasin", new ColourValue(255,228,181))
		.put("navajowhite", new ColourValue(255,222,173))
		.put("navy", new ColourValue(0,0,128))
		.put("oldlace", new ColourValue(253,245,230))
		.put("olive", new ColourValue(128,128,0))
		.put("olivedrab", new ColourValue(107,142,35))
		.put("orange", new ColourValue(255,165,0))
		.put("orangered", new ColourValue(255,69,0))
		.put("orchid", new ColourValue(218,112,214))
		.put("palegoldenrod", new ColourValue(238,232,170))
		.put("palegreen", new ColourValue(152,251,152))
		.put("paleturquoise", new ColourValue(175,238,238))
		.put("palevioletred", new ColourValue(216,112,147))
		.put("papayawhip", new ColourValue(255,239,213))
		.put("peachpuff", new ColourValue(255,218,185))
		.put("peru", new ColourValue(205,133,63))
		.put("pink", new ColourValue(255,192,203))
		.put("plum", new ColourValue(221,160,221))
		.put("powderblue", new ColourValue(176,224,230))
		.put("purple", new ColourValue(128,0,128))
		.put("red", new ColourValue(255,0,0))
		.put("rosybrown", new ColourValue(188,143,143))
		.put("royalblue", new ColourValue(65,105,225))
		.put("saddlebrown", new ColourValue(139,69,19))
		.put("salmon", new ColourValue(250,128,114))
		.put("sandybrown", new ColourValue(244,164,96))
		.put("seagreen", new ColourValue(46,139,87))
		.put("seashel", new ColourValue(255,245,238))
		.put("sienna", new ColourValue(160,82,45))
		.put("silver", new ColourValue(192,192,192))
		.put("skyblue", new ColourValue(135,206,235))
		.put("slateblue", new ColourValue(106,90,205))
		.put("slategray", new ColourValue(112,128,144))
		.put("snow", new ColourValue(255,250,250))
		.put("springgreen", new ColourValue(0,255,127))
		.put("steelblue", new ColourValue(70,130,180))
		.put("tan", new ColourValue(210,180,140))
		.put("tea", new ColourValue(0,128,128))
		.put("thistle", new ColourValue(216,191,216))
		.put("tomato", new ColourValue(255,99,71))
		.put("turquoise", new ColourValue(64,224,208))
		.put("violet", new ColourValue(238,130,238))
		.put("violetred", new ColourValue(208,32,144))
		.put("wheat", new ColourValue(245,222,179))
		.put("white", new ColourValue(255,255,255))
		.put("whitesmoke", new ColourValue(245,245,245))
		.put("yellow", new ColourValue(255,255,0))
		.put("yellowgreen", new ColourValue(154,205,50))
		.build();
}



