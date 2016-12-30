package com.github.s262316.forx.box;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;


public class BoxBorders
{
    public static void drawTopBorderOutset(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            c.setColor(c.getColor().brighter());

            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), dim.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), dim.height());

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), right.top(), right.width(), dim.height());
            c.setClip(null);
    }

    public static void drawLeftBorderOutset(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            c.setColor(c.getColor().brighter());

            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), dim.width(), top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), top.bottom()+1, dim.width(), dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), dim.width(), bottom.height());
            c.setClip(null);
    }

    public static void drawRightBorderOutset(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            c.setColor(c.getColor().darker());

            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), dim.width(), top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), dim.top()+top.height(), dim.width(), dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), dim.width(), bottom.height());
            c.setClip(null);
    }

    public static void drawBottomBorderOutset(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            c.setColor(c.getColor().darker());

            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), dim.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), dim.height());

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), dim.top(), right.width(), dim.height());
            c.setClip(null);
    }

    public static void drawTopBorderInset(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            c.setColor(c.getColor().darker());

            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), dim.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), dim.height());

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), right.top(), right.width(), dim.height());
            c.setClip(null);
    }

    public static void drawLeftBorderInset(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            c.setColor(c.getColor().darker());

            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), dim.width(), top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), top.bottom()+1, dim.width(), dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), dim.width(), bottom.height());
            c.setClip(null);
    }

    public static void drawRightBorderInset(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            c.setColor(c.getColor().brighter());

            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), dim.width(), top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), dim.top()+top.height(), dim.width(), dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), dim.width(), bottom.height());
            c.setClip(null);
    }

    public static void drawBottomBorderInset(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            c.setColor(c.getColor().brighter());

            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), dim.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), dim.height());

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), dim.top(), right.width(), dim.height());
            c.setClip(null);
    }

    public static void drawHorizBorderRidge(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            int bar_width=0;
            int div, rem;
            Color lighter, darker;

            // solid border
            if(dim.height()<2)
            {
                    drawTopBorderSolid(dim, c, left, right, left_clip, right_clip);
                    return;
            }

            lighter=c.getColor().brighter();
            darker=c.getColor().darker();

            div=dim.height()/2;
            rem=dim.height()%2;

            if(rem==0)
                    bar_width=div;
            else if(rem==1)
                    bar_width=div+1;

            // left Polygon
            c.setClip(left_clip);
            c.setColor(lighter);
            c.fillRect(dim.left(), dim.top(), left.width(), bar_width);
            c.setColor(darker);
            c.fillRect(dim.left(), dim.top()+bar_width, left.width(), bar_width);
            c.setClip(null);

            // main part top bar
            c.setColor(lighter);
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), bar_width);

            // main part bottom bar
            c.setColor(darker);
            c.fillRect(dim.left()+left.width(), dim.top()+bar_width, dim.width()-left.width()-right.width(), bar_width);

            // right Polygon
            c.setClip(right_clip);
            c.setColor(lighter);
            c.fillRect(right.left(), right.top(), right.width(), bar_width);
            c.setColor(darker);
            c.fillRect(right.left(), dim.top()+bar_width, right.width(), bar_width);
            c.setClip(null);
    }

    public static void drawVertBorderRidge(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            int bar_width=0;
            int div, rem;
            Color lighter, darker;

            // solid border
            if(dim.height()<3)
            {
                    drawTopBorderSolid(dim, c, top, bottom, top_clip, bottom_clip);
                    return;
            }

            lighter=c.getColor().brighter();
            darker=c.getColor().darker();

            div=dim.width()/2;
            rem=dim.width()%2;

            if(rem==0)
                    bar_width=div;
            else if(rem==1)
                    bar_width=div+1;

            // top Polygon
            c.setClip(top_clip);
            c.setColor(lighter);
            c.fillRect(dim.left(), dim.top(), bar_width, top.height());
            c.setColor(darker);
            c.fillRect(dim.left()+bar_width, dim.top(), bar_width, top.height());
            c.setClip(null);

            // main part
            c.setColor(lighter);
            c.fillRect(dim.left(), top.bottom()+1, bar_width, dim.height()-top.height()-bottom.height());
            c.setColor(darker);
            c.fillRect(dim.left()+bar_width, top.bottom()+1, bar_width, dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.setColor(lighter);
            c.fillRect(dim.left(), bottom.top(), bar_width, bottom.height());
            c.setColor(darker);
            c.fillRect(dim.left()+bar_width, bottom.top(), bar_width, bottom.height());
            c.setClip(null);
    }

    public static void drawHorizBorderGroove(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            int bar_width=0;
            int div, rem;
            Color lighter, darker;

            // solid border
            if(dim.height()<2)
            {
                    drawTopBorderSolid(dim, c, left, right, left_clip, right_clip);
                    return;
            }

            lighter=c.getColor().brighter();
            darker=c.getColor().darker();

            div=dim.height()/2;
            rem=dim.height()%2;

            if(rem==0)
                    bar_width=div;
            else if(rem==1)
                    bar_width=div+1;

            // left Polygon
            c.setClip(left_clip);
            c.setColor(darker);
            c.fillRect(dim.left(), dim.top(), left.width(), bar_width);
            c.setColor(lighter);
            c.fillRect(dim.left(), dim.top()+bar_width, left.width(), bar_width);
            c.setClip(null);

            // main part top bar
            c.setColor(darker);
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), bar_width);

            // main part bottom bar
            c.setColor(lighter);
            c.fillRect(dim.left()+left.width(), dim.top()+bar_width, dim.width()-left.width()-right.width(), bar_width);

            // right Polygon
            c.setClip(right_clip);
            c.setColor(darker);
            c.fillRect(right.left(), right.top(), right.width(), bar_width);
            c.setColor(lighter);
            c.fillRect(right.left(), dim.top()+bar_width, right.width(), bar_width);
            c.setClip(null);
    }

    public static void drawVertBorderGroove(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            int bar_width=0;
            int div, rem;
            Color lighter, darker;

            // solid border
            if(dim.height()<3)
            {
                    drawTopBorderSolid(dim, c, top, bottom, top_clip, bottom_clip);
                    return;
            }

            lighter=c.getColor().brighter();
            darker=c.getColor().darker();

            div=dim.width()/2;
            rem=dim.width()%2;

            if(rem==0)
                    bar_width=div;
            else if(rem==1)
                    bar_width=div+1;

            // top Polygon
            c.setClip(top_clip);
            c.setColor(darker);
            c.fillRect(dim.left(), dim.top(), bar_width, top.height());
            c.setColor(lighter);
            c.fillRect(dim.left()+bar_width, dim.top(), bar_width, top.height());
            c.setClip(null);

            // main part
            c.setColor(darker);
            c.fillRect(dim.left(), top.bottom()+1, bar_width, dim.height()-top.height()-bottom.height());
            c.setColor(lighter);
            c.fillRect(dim.left()+bar_width, top.bottom()+1, bar_width, dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.setColor(darker);
            c.fillRect(dim.left(), bottom.top(), bar_width, bottom.height());
            c.setColor(lighter);
            c.fillRect(dim.left()+bar_width, bottom.top(), bar_width, bottom.height());
            c.setClip(null);
    }

    public static void drawHorizBorderDouble(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            int bar_width=0, gap_width=0;
            int div, rem;

            // solid border
            if(dim.height()<3)
            {
                    drawTopBorderSolid(dim, c, left, right, left_clip, right_clip);
                    return;
            }

            div=dim.height()/3;
            rem=dim.height()%3;

            if(rem==0)
            {
                    bar_width=div;
                    gap_width=div;
            }
            else if(rem==1)
            {
                    bar_width=div;
                    gap_width=div+1;
            }
            else if(rem==2)
            {
                    bar_width=div+1;
                    gap_width=div;
            }

            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), bar_width);
            c.fillRect(dim.left(), dim.top()+bar_width+gap_width, left.width(), bar_width);
            c.setClip(null);

            // main part top bar
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), bar_width);

            // main part bottom bar
            c.fillRect(dim.left()+left.width(), dim.top()+bar_width+gap_width, dim.width()-left.width()-right.width(), bar_width);

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), right.top(), right.width(), bar_width);
            c.fillRect(right.left(), dim.top()+bar_width+gap_width, right.width(), bar_width);
            c.setClip(null);
    }

    public static void drawVertBorderDouble(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            int bar_width=0, gap_width=0;
            int div, rem;

            // solid border
            if(dim.height()<3)
            {
                    drawTopBorderSolid(dim, c, top, bottom, top_clip, bottom_clip);
                    return;
            }

            div=dim.width()/3;
            rem=dim.width()%3;

            if(rem==0)
            {
                    bar_width=div;
                    gap_width=div;
            }
            else if(rem==1)
            {
                    bar_width=div;
                    gap_width=div+1;
            }
            else if(rem==2)
            {
                    bar_width=div+1;
                    gap_width=div;
            }

            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), bar_width, top.height());
            c.fillRect(dim.left()+bar_width+gap_width, dim.top(), bar_width, top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), top.bottom()+1, bar_width, dim.height()-top.height()-bottom.height());
            c.fillRect(dim.left()+bar_width+gap_width, top.bottom()+1, bar_width, dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), bar_width, bottom.height());
            c.fillRect(dim.left()+bar_width+gap_width, bottom.top(), bar_width, bottom.height());
            c.setClip(null);
    }


    public static void drawTopBorderSolid(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), dim.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), dim.height());

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), right.top(), right.width(), dim.height());
            c.setClip(null);
    }

    public static void drawLeftBorderSolid(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), dim.width(), top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), top.bottom()+1, dim.width(), dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), dim.width(), bottom.height());
            c.setClip(null);
    }

    public static void drawRightBorderSolid(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            // top Polygon
            c.setClip(top_clip);
            c.fillRect(dim.left(), dim.top(), dim.width(), top.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left(), dim.top()+top.height(), dim.width(), dim.height()-top.height()-bottom.height());

            // bottom Polygon
            c.setClip(bottom_clip);
            c.fillRect(dim.left(), bottom.top(), dim.width(), bottom.height());
            c.setClip(null);
    }

    public static void drawBottomBorderSolid(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            // left Polygon
            c.setClip(left_clip);
            c.fillRect(dim.left(), dim.top(), left.width(), dim.height());
            c.setClip(null);

            // main part
            c.fillRect(dim.left()+left.width(), dim.top(), dim.width()-left.width()-right.width(), dim.height());

            // right Polygon
            c.setClip(right_clip);
            c.fillRect(right.left(), dim.top(), right.width(), dim.height());
            c.setClip(null);
    }

    public static void drawHorizBorderDotted(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            int dot_width, slots, spaces, dots, total_remainder, remainder_per_space=0, remainder_remainder=0, curr_left, i, j;
            boolean left_is_clipped=true, right_is_clipped=false;

            // width of a dot
            dot_width=dim.height();

            // number of slots
            slots=dim.width()/dot_width;
            if(slots%2==0)
                    --slots;

            // number of spaces
            spaces=(slots-1)/2;

            // number of dots
            dots=spaces+1;

            // total remainder. max = dot_width
            total_remainder=dim.width()%(slots*dot_width);

            if(spaces>0)
            {
                    // remainder per space
                    remainder_per_space=total_remainder/spaces;

                    // remainder of remainder
                    remainder_remainder=total_remainder%spaces;
            }

            // apply the left clip
    //	c.setClip(left_clip);

            curr_left=dim.left();
            for(i=0, j=0; i<slots; i++)
            {
                    if(i%2==0)
                    {
                            // draw dot
                            c.fillOval(curr_left, dim.top(), dot_width, dim.height());

                            j++;
                    }
                    else
                    {
                            curr_left+=remainder_per_space;

                            if(remainder_remainder>0)
                            {
                                    ++curr_left;
                                    --remainder_remainder;
                            }
                    }

                    curr_left+=dot_width;

    //		if(left_is_clipped==true && curr_left > left.right())
    //		{
    //			c.setClip(null);
    //			left_is_clipped=false;
     //		}
    //		else if(left_is_clipped==false && curr_left > right.left())
    //		{
    //			c.setClip(right_clip);
    //			right_is_clipped=true;
    //		}
            }

    //	c.setClip(null);
    }

    public static void drawVertBorderDotted(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            int dot_width, slots, spaces, dots, total_remainder, remainder_per_space=0, remainder_remainder=0, curr_top, i, j;
            boolean top_is_clipped=true, bottom_is_clipped=false;

            // width of a dot
            dot_width=dim.width();

            // number of slots
            slots=dim.height()/dot_width;
            if(slots%2==0)
                    --slots;

            // number of spaces
            spaces=(slots-1)/2;

            // number of dots
            dots=spaces+1;

            // total remainder. max = dot_width
            total_remainder=dim.height()%(slots*dot_width);

            if(spaces>0)
            {
                    // remainder per space
                    remainder_per_space=total_remainder/spaces;

                    // remainder of remainder
                    remainder_remainder=total_remainder%spaces;
            }

    //	// apply the left clip
    //	c.setClip(top_clip);

            curr_top=dim.top();
            for(i=0, j=0; i<slots; i++)
            {
                    if(i%2==0)
                    {
                            // draw dot
                            c.fillOval(dim.left(), curr_top, dim.width(), dot_width);

                            j++;
                    }
                    else
                    {
                            curr_top+=remainder_per_space;

                            if(remainder_remainder>0)
                            {
                                    ++curr_top;
                                    --remainder_remainder;
                            }
                    }

                    curr_top+=dot_width;

    //		if(top_is_clipped==true && curr_top > top.bottom())
    //		{
    //			c.setClip(null);
    //			top_is_clipped=false;
     //		}
    //		else if(top_is_clipped==false && curr_top > bottom.top())
    //		{
    //			c.setClip(bottom_clip);
    //			bottom_is_clipped=true;
    //		}
            }
    //
    //	c.setClip(null);
    }

    public static void drawHorizBorderDashed(Dimensionable dim, Graphics2D c, Dimensionable left, Dimensionable right, Polygon left_clip, Polygon right_clip)
    {
            int dot_width, slots, spaces, dots, total_remainder, remainder_per_space=0, remainder_remainder=0, curr_left, i, j;

            // width of a dot
            dot_width=dim.height()*2;

            // number of slots
            slots=dim.width()/dot_width;
            if(slots%2==0)
                    --slots;

            // number of spaces
            spaces=(slots-1)/2;

            // number of dots
            dots=spaces+1;

            // total remainder. max = dot_width
            total_remainder=dim.width()%(slots*dot_width);

            if(spaces>0)
            {
                    // remainder per space
                    remainder_per_space=total_remainder/spaces;

                    // remainder of remainder
                    remainder_remainder=total_remainder%spaces;
            }

            curr_left=dim.left();
            for(i=0, j=0; i<slots; i++)
            {
                    if(i%2==0)
                    {
                            // draw dash
                            c.fillRect(curr_left, dim.top(), dot_width, dim.height());

                            j++;
                    }
                    else
                    {
                            curr_left+=remainder_per_space;

                            if(remainder_remainder>0)
                            {
                                    ++curr_left;
                                    --remainder_remainder;
                            }
                    }

                    curr_left+=dot_width;
            }
    }

    public static void drawVertBorderDashed(Dimensionable dim, Graphics2D c, Dimensionable top, Dimensionable bottom, Polygon top_clip, Polygon bottom_clip)
    {
            int dot_width, slots, spaces, dots, total_remainder, remainder_per_space=0, remainder_remainder=0, curr_top, i, j;

            // width of a dot
            dot_width=dim.width()*2;

            // number of slots
            slots=dim.height()/dot_width;
            if(slots%2==0)
                    --slots;

            // number of spaces
            spaces=(slots-1)/2;

            // number of dots
            dots=spaces+1;

            // total remainder. max = dot_width
            total_remainder=dim.height()%(slots*dot_width);

            if(spaces>0)
            {
                    // remainder per space
                    remainder_per_space=total_remainder/spaces;

                    // remainder of remainder
                    remainder_remainder=total_remainder%spaces;
            }

            curr_top=dim.top();
            for(i=0, j=0; i<slots; i++)
            {
                    if(i%2==0)
                    {
                            // draw dash
                            c.fillRect(dim.left(), curr_top, dim.width(), dot_width);

                            j++;
                    }
                    else
                    {
                            curr_top+=remainder_per_space;

                            if(remainder_remainder>0)
                            {
                                    ++curr_top;
                                    --remainder_remainder;
                            }
                    }

                    curr_top+=dot_width;
            }
    }

    public static void drawHorizBorderSloped(Dimensionable dim, Dimensionable leftBorder, Dimensionable rightBorder, Graphics2D c)
    {
            int left_tri_x[]=new int[3], left_tri_y[]=new int[3];
            int right_tri_x[]=new int[3], right_tri_y[]=new int[3];

            // left sloped part
            //  +----+ 0
            //  |   /|
            //  | /  |
            //2 +/---+ 1
            left_tri_x[0]=leftBorder.right()-dim.left()+1;
            left_tri_y[0]=dim.top();
            left_tri_x[1]=leftBorder.right()-dim.left()+1;
            left_tri_y[1]=dim.top()+dim.height()-1;
            left_tri_x[2]=dim.left();
            left_tri_y[2]=dim.top()+dim.height()-1;

            c.fillPolygon(left_tri_x, left_tri_y, 3);

            // right sloped part
            // 2+---+ 0
            //  |\  |
            //  | \ |
            //  +--\+ 1
            right_tri_x[0]=leftBorder.right()-dim.left()+1;
            right_tri_y[0]=dim.top();
            right_tri_x[1]=leftBorder.right()-dim.left()+1;
            right_tri_y[1]=dim.top()+dim.height()-1;
            right_tri_x[2]=dim.left();
            right_tri_y[2]=dim.top();

            c.fillPolygon(right_tri_x, right_tri_y, 3);

            // main section
            int left_width=left_tri_x[1]-left_tri_x[2]+1;
            int right_width=left_tri_x[0]-left_tri_x[2]+1;

            c.fillRect(leftBorder.right()+1, dim.top(), dim.width()-left_width-right_width, dim.height());
    }

    public static void drawHorizBorderDouble(Dimensionable dim, Dimensionable leftBorder, Dimensionable rightBorder, Graphics2D c)
    {
            int bar_width=0, gap_width=0;
            int div, rem;

            // solid border
            if(dim.height()<3)
            {
                    drawHorizBorderSloped(dim, leftBorder, rightBorder, c);
                    return;
            }

            div=dim.height()/3;
            rem=dim.height()%3;

            if(rem==0)
            {
                    bar_width=div;
                    gap_width=div;
            }
            else if(rem==1)
            {
                    bar_width=div;
                    gap_width=div+1;
            }
            else if(rem==2)
            {
                    bar_width=div+1;
                    gap_width=div;
            }

            int left_inner_bar_width=0, left_inner_space_width=0;

            div=leftBorder.width()/3;
            rem=leftBorder.width()%3;
            if(rem==0)
            {
                    left_inner_bar_width=div;
                    left_inner_space_width=div;
            }
            else if(rem==1)
            {
                    left_inner_bar_width=div;
                    left_inner_space_width=div+1;
            }
            else if(rem==2)
            {
                    left_inner_bar_width=div+1;
                    left_inner_space_width=div;
            }

            int right_inner_bar_width=0, right_inner_space_width=0;

            div=rightBorder.width()/3;
            rem=rightBorder.width()%3;
            if(rem==0)
            {
                    right_inner_bar_width=div;
                    right_inner_space_width=div;
            }
            else if(rem==1)
            {
                    right_inner_bar_width=div;
                    right_inner_space_width=div+1;
            }
            else if(rem==2)
            {
                    right_inner_bar_width=div+1;
                    right_inner_space_width=div;
            }

            // outer (upper)
            drawHorizBorderSloped(new Dimensionable(dim.left(), dim.top(), dim.width(), dim.height()), leftBorder, rightBorder, c);

            // inner (lower)
            drawHorizBorderSloped(new Dimensionable(
                    dim.left()+left_inner_bar_width+left_inner_space_width,
                    dim.top()+bar_width+gap_width,
                    dim.width()-left_inner_bar_width-left_inner_space_width,
                    dim.height()-right_inner_bar_width-right_inner_space_width),
                    leftBorder, rightBorder, c);
    }



}
