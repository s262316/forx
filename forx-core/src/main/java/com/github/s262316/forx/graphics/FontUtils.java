package com.github.s262316.forx.graphics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.util.List;

import com.github.s262316.forx.box.util.FontStyle;
import com.github.s262316.forx.box.util.FontVariant;

public class FontUtils
{
	public static int size_px(Font f, FontMetrics fm)
	{
		return fm.getHeight();
	}

	public static int size_pt(Font f)
	{
		return f.getSize();
	}

	public static int weight(Font f)
	{
		return 400;
	}


	public static int x_height(Font f, FontMetrics fm)
	{
		return fm.getHeight()/2;
	}

	public static Font get_font(List<String> familyName, FontStyle fontStyle, FontVariant fontVariant, int fontWeight, int fontSize, GraphicsContext gfxCtx)
	{
		Font fonts[]=GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		boolean found=false;
		int jFontStyle=0;
		Font chosen=null;

		for(String preferredFont : familyName)
		{
			for(Font f : fonts)
			{
				chosen=f;
				if(chosen.getFamily().equals(preferredFont))
				{
					found=true;
					break;
				}
			}

			if(found)
				break;
		}

		if(!found)
			return null;

		switch(fontStyle)
		{
		case FS_NORMAL:
			jFontStyle=Font.PLAIN;
			break;
		case FS_ITALIC:
		case FS_OBLIQUE:
			jFontStyle=Font.ITALIC;
			break;
		}

		if(fontWeight>400)
			jFontStyle=Font.BOLD;

		double scale=gfxCtx.getDpi()/72f;
		
		return chosen.deriveFont(jFontStyle, fontSize*=scale);
	}


}
