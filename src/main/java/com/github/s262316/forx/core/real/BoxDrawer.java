package com.github.s262316.forx.core.real;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.AbsoluteBox;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.ColGroup;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.InlineBlockBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.ReplacedInline;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.RowGroup;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.box.Text;
import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.BackgroundProperties;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.util.AreaType;
import com.github.s262316.forx.box.util.BackgroundRepeat;
import com.github.s262316.forx.box.util.Boxes;
import com.github.s262316.forx.box.util.Overflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoxDrawer
{
	private final static Logger logger=LoggerFactory.getLogger(BoxDrawer.class);

    private int indent=0;
    private BoxRealMapping realMapping;

    @Autowired
    public BoxDrawer(BoxRealMapping realMapping)
    {
    	this.realMapping=realMapping;
    }
    
    private String print_indent()
    {
        String ind="";
        for(int i=0; i < indent; i++)
            ind+=" ";
        return ind;
    }

    private void draw_text(Text t, Graphics2D canvas, int offx, int offy)
    {
		logger.debug("draw_text(\"{}\",{},{},{})", t.text(), "{canvas}", offx, offy);

        indent++;
        if(t.whitespace() == false)
        {

            logger.debug(print_indent() + "[text " + t.getId() + "] at  " + t.text() + " at " + (t.left() - offx) + "," + (t.baseline() - offy) + " (" + t.width() + "," + t.height() +" line :" + t.line().left()+","+t.line().top()+" "+t.line().width()+","+t.line().height());

            canvas.drawString(t.text(), t.left() - offx, t.baseline() - offy);
            realMapping.update(t, t.left() - offx, t.baseline() - offy, t.width(), t.height());
        }
        else
        {

            logger.debug(print_indent() + "[whitespace " + t.getId() + "] " + t.text() + " at " + (t.left() - offx) + "," + (t.baseline() - offy) + " (" + t.width() + "," + t.height() );
        }
        indent--;
    }

    private void adjust_drawarea_for_overflow(DrawArea area, Layable lay, BlockBox cont)
    {
        if(lay.right() > cont.right())
        {
            if(cont.getOverflow() == Overflow.OF_SCROLL)
                area.change_width(lay.right() - cont.right());
            else if(cont.getOverflow() == Overflow.OF_HIDDEN)
            {
            }
        }
        if(lay.bottom() > cont.bottom())
        {
            if(cont.getOverflow() == Overflow.OF_SCROLL)
                area.change_height(lay.bottom() - cont.bottom());
            else if(cont.getOverflow() == Overflow.OF_HIDDEN)
            {
            }
        }
    }

    private void draw_background(Box box, Graphics2D canvas, int offx, int offy)
    {
        BackgroundProperties bp=new BackgroundProperties();
        int left, top, width, height;

        box.getVisual().workout_background_properties(new PropertyBoxAdaptor(box), bp);

        if(box.container() == null)
        {
            if(bp.colour != null)
                canvas.setColor(bp.colour);
            else if(bp.image == null)
            {
				List<Layable> members=box.getMembersAll();

				if(members.size()>0)
				{
					Box body=BoxTypes.toBox(members.get(0));

					body.getVisual().workout_background_properties(new PropertyBoxAdaptor(body), bp);

					BoxTypes.toRootBox(box).adopt_body_background(true);
				}
            }
            left=box.left();
            top=box.top();
            width=box.width();
            height=box.height();
        }
        else
        {

            if(box.flows() == true && box.container().container() == null)
            {
                if(BoxTypes.toRootBox(box.container()).has_adopted_body_background())
                {
                    bp.colour=null;
                    bp.image=null;
                }
            }

            if(bp.colour != null)
            {
                canvas.setColor(bp.colour);
                logger.debug("** colour : "+bp.colour);
			}

            left=box.contentLeft();
            top=box.contentTop();
            width=box.contentWidth();
            height=box.contentHeight();
        }

        if(bp.colour != null)
            canvas.fillRect(left - offx, top - offy, width, height);

        if(bp.image != null)
        {
            // don't draw body's background if the root has adopted it
            int c_left, c_top;
            int i_left, i_top, i_bottom, i_right;
            canvas.setClip(new Rectangle(left - offx, top - offy, left + width - 1 - offx, top + height - 1 - offy));
            c_left=left + bp.horiz;
            c_top=top
             + bp.vert  ;
   			// find the coords of leftmost image
            i_left=c_left;
            while(i_left+bp.image.swidth().value-1 > left)
            {
                i_left-=bp.image.swidth().value + 1;
            }
            // find the coords of rightmost image
            i_right=c_left + bp.image.swidth().value - 1;
            while(i_right - bp.image.swidth().value + 1 < left + width - 1)
            {
                i_right+=bp.image.swidth().value - 1;
            }
            // find the coords of the topmost image
            i_top= top - offy;
			while(i_top+bp.image.sheight().value-1 > top)
            {
                i_top-=bp.image.sheight().value + 1;
            }   // find the coords of the bottommost image
            i_bottom=top + bp.image.sheight().value - 1;
            while(i_bottom - bp.image.sheight().value + 1 < top + height - 1)
            {
                i_bottom+=bp.image.sheight().value - 1;
            }   // repeat horizontally?
            if(bp.repeat == BackgroundRepeat.BR_REPEAT_X)
            {
                int x=i_left, y=c_top;
                while(x <= i_right)
                {
                    bp.image.set_position(x, y);
                    bp.image.draw(canvas, offx, offy);
                    x+=bp.image.swidth().value;
                }
            }
            // repeat vertically?
            else if(bp.repeat == BackgroundRepeat.BR_REPEAT_Y)
            {
                int x=c_left, y=i_top;
                while(y <= i_bottom)
                {
                    bp.image.set_position(x, y);
                    bp.image.draw(canvas, offx, offy);
                    y+=bp.image.sheight().value;
                }
            }
            else if(bp.repeat == BackgroundRepeat.BR_REPEAT)
            {
                int x, y=i_top;
                while(y <= i_bottom)
                {
                    x=i_left;
                    while(x <= i_right)
                    {
                        bp.image.set_position(x, y);
                        bp.image.draw(canvas, offx, offy);
                        x+=bp.image.swidth().value;
                    }
                    y+=bp.image.sheight().value;
                }
            }
            else if(bp.repeat == BackgroundRepeat.BR_NO_REPEAT)
            {
                bp.image.set_position(c_left, c_top);
                bp.image.draw(canvas, offx, offy);
            }
            canvas.setClip(null);
        }
    }

    private void draw_abs(AbsoluteBox box, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        AbsoluteBox ab;
        indent++;

        logger.debug(print_indent() + "[abs" + box.getId() + "] at  " + " at " + (box.left() - offx) + "," + (box.top() - offy) + " (" + box.width() + "," + box.height() + ") cw " + box.contentLeft() + "," + box.contentTop() + " (" + box.contentWidth() + "," + box.contentHeight() + ")" );

        realMapping.update(box, box.left() - offx, box.top() - offy, box.width(), box.height());
        
        draw_background(box, area.canvas, offx, offy);
        box.draw_borders(offx, offy, graphics);
        if(box.replace_object() == null)
        {
            // negative positioned

            for(Layable lay : box.getMembersPositioned())
            {
            	if(Boxes.existsInSpace(lay))
            	{
	                adjust_drawarea_for_overflow(area, lay, box);
	
	                if(BoxTypes.isAbsoluteBox(lay))
	                {
	                    ab=(AbsoluteBox) lay;
	
	                    if(ab.zIndex() < 0)
	                        draw_abs(ab, area, offx, offy, graphics);
	                }
            	}
            }
            draw_block_members(box, area, offx, offy, graphics);
            // 0 then positive positioned boxes

            for(Layable lay  : box.getMembersPositioned())
            {
            	if(Boxes.existsInSpace(lay))
            	{
	                adjust_drawarea_for_overflow(area, lay, box);
	
	                if(BoxTypes.isAbsoluteBox(lay))
	                {
	                    ab=(AbsoluteBox) lay;
	
	                    if(ab.zIndex() >= 0)
	                        draw_abs(ab, area, offx, offy, graphics);
	                }
            	}
            }
        }
        else
        {

            logger.debug(print_indent() + "[breplace " /*+ box.getId()*/ + "] at  " + " at " + (box.left() - offx) + "," + box.top() + " (" + box.width() + "," + box.height() + ") cw " + (box.contentLeft() - offx) + "," + (box.contentTop() - offy) + " (" + box.contentWidth() + "," + box.contentHeight() + ")" + " " + box.marginTop() + " " + box.marginBottom() );

            box.replace_object().draw(area.canvas, offx, offy);
        }
        indent--;
    }

    private void draw_float(FloatBox fb, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        draw_block(fb, area, offx, offy, graphics);
    }

    private void draw_replaced(ReplacedInline ri, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        logger.debug(print_indent() + "[ireplace " + ri.getId() + "] at  " + " at " + (ri.left() - offx) + "," + (ri.top() - offy) + " (" + ri.width() + "," + ri.height()  );

        if(ri.is_relative() == true)
        {
            offx-=ri.getRelLeft();
            offy-=ri.getRelTop();
        }
        ri.replace_object().draw(area.canvas, offx, offy);
    }

    private void draw_inline_block(InlineBlockBox inlineblockbox, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        BlockBox bb;
        indent++;
        if(inlineblockbox.is_relative() == true)
        {
            offx-=inlineblockbox.getRelLeft();
            offy-=inlineblockbox.getRelTop();
        }
        logger.debug(print_indent() + "[inlineblockbox" /*+ inlineblockbox.getId()*/ + "] at  " + " at " + (inlineblockbox.left() - offx) + "," + (inlineblockbox.top()-offy) + " (" + inlineblockbox.width() + "," + inlineblockbox.height() + ") cw " + (inlineblockbox.contentLeft() - offx) + "," + (inlineblockbox.contentTop() - offy) + " (" + inlineblockbox.contentWidth() + "," + inlineblockbox.contentHeight() + ")" + " " + inlineblockbox.marginTop() + " " + inlineblockbox.marginBottom() );

        draw_background(inlineblockbox, area.canvas, offx, offy);
        inlineblockbox.draw_borders(offx, offy, graphics);
        realMapping.update((Box)inlineblockbox, inlineblockbox.left() - offx, inlineblockbox.top() - offy, inlineblockbox.width(), inlineblockbox.height());
        for(Layable lay : inlineblockbox.getMembersAll())
        {
            if(BoxTypes.isBlockBox(lay))
                draw_block(BoxTypes.toBlockBox(lay), area, offx, offy, graphics);
        }
        indent--;
    }

    private void draw_inline(InlineBox in, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
		logger.debug("draw_inline");

        indent++;
        if(in.is_relative() == true)
        {
            offx-=in.getRelLeft();
            offy-=in.getRelTop();
        }

        logger.debug(print_indent() + "[inline" + in.getId() + "] at  " + " at " + (in.left() - offx) + "," + (in.top() - offy) + " (" + in.width() + "," + in.height() );

        in.draw_borders(offx, offy, graphics);
        realMapping.update((Box)in, in.left() - offx, in.top() - offy, in.width(), in.height());

        for(Layable lay  : in.getMembersAll())
        {
        	if(Boxes.existsInSpace(lay))
        	{
	            if(lay.right() > in.right())
	            {
	                if(area.type == AreaType.AT_SCROLLABLE)
	                    area.change_width(lay.right() - in.right());
	            }
	            if(lay.bottom() > in.bottom())
	            {
	                if(area.type == AreaType.AT_SCROLLABLE)
	                    area.change_height(lay.bottom() - in.bottom());
	            }
	            area.canvas.setColor(in.getForegroundColour());
	            area.canvas.setFont(in.getFont());
	            if(BoxTypes.isText(lay))
	                draw_text(BoxTypes.toText(lay), area.canvas, offx, offy);
	            else if(BoxTypes.isInlineBox(lay))
	                draw_inline(BoxTypes.toInlineBox(lay), area, offx, offy, graphics);
	            else if(BoxTypes.isFloatBox(lay))
	                draw_float(BoxTypes.toFloatBox(lay), area, offx, offy, graphics);
	            else if(BoxTypes.isReplacedInline(lay))
	                draw_replaced(BoxTypes.toReplacedInline(lay), area, offx, offy, graphics);
	            else if(BoxTypes.isInlineBlockBox(lay))
	                draw_inline_block(BoxTypes.toInlineBlockBox(lay), area, offx, offy, graphics);
        	}
        }
        indent--;
    }

    private void draw_cell(CellBox cell, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        draw_block(cell, area, offx, offy, graphics);
    }

    private void draw_colgroup(ColGroup colgroup, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
    }

    private void draw_rowgroup(RowGroup rowgroup, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
    }

    private void draw_row(TableRow row, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        logger.debug(print_indent() + "[row" + row + "] at  " + " at " + (row.top() - offy) + "(" + row.height() + ") cw "); //+
///*   " (" + row.contentTop() + ")" + */
///*   " (" + row.contentHeight() + ")" +*/ );
    }

    private void draw_column(Column col, DrawArea area, int offx, int offy)
    {
        logger.debug(print_indent() + "[col" + col + "] at  " + " at " + (col.left() - offx) + "(" + col.width() + ") cw "); //+
//                //   " (" + row.contentTop() + ")" +
//                /*   " (" + row.contentHeight() + ")" +*/ );
    }

    private void draw_table(TableBox table, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
        CellBox cb;
        int i;
        logger.debug(print_indent() + "[table" /*+ table.getId()*/ + "] at  " + " at " + (table.left() - offx) + "," + (table.top() - offy) + " (" + table.width() + "," + table.height() + ") cw " + (table.contentLeft() - offx) + "," + (table.contentTop() - offy) + " (" + table.contentWidth() + "," + table.contentHeight() + ")" );

        if(table.is_relative() == true)
        {
            offx-=table.getRelLeft();
            offy-=table.getRelTop();
        }
        draw_background(table, area.canvas, offx, offy);
        for(i=1; i <= table.row_count(); i++)
            draw_row(table.row(i), area, offx, offy, graphics);
        // for(list<ColGroup*>::iterator cgit=colgroups.begin(); cgit!=colgroups.end(); cgit++)
        //  draw_colgroup(*cgit, canvas);
        for(i=1; i <= table.col_count(); i++)
            draw_column(table.column(i), area, offx, offy);
        for(Layable lay : table.getMembersCells())
        {
            cb=BoxTypes.toCellBox(lay);

            draw_cell(cb, area, offx, offy, graphics);
        }
    }

    private void draw_block(BlockBox block, DrawArea area, int offx, int offy, Graphics2D graphics)
    {
		logger.debug("draw_block("+block+","+area+","+offx+","+offy+")");

        int rel_left=0, rel_top=0;
        AbsoluteBox ab;
        indent++;
        if(block.is_relative() == true)
        {
            offx-=block.getRelLeft();
            offy-=block.getRelTop();
        }
        if(block.getOverflow() == Overflow.OF_SCROLL)
        {
        }    //area=make_drawarea(AT_SCROLLABLE, area.canvas);
        else if(block.getOverflow() == Overflow.OF_HIDDEN)
        {
            Rectangle clip_region=new Rectangle((block.contentLeft() - offx), (block.contentTop() - offy), (block.contentRight() - offx), (block.contentBottom() - offy));
            area.clippers.push(clip_region);
            area.canvas.setClip(clip_region);
        }

        draw_background(block, area.canvas, offx, offy);
        block.draw_borders(offx, offy, graphics);
        realMapping.update(block, block.left() - offx, block.top() - offy, block.width(), block.height());

        if(block.replace_object() == null)
        {
            logger.debug(print_indent() + "[block" + block.getId() + "] at  " + " at " + (block.left() - offx) + "," + block.top() + " (" + block.width() + "," + block.height() + ") cw " + (block.contentLeft() - offx) + "," + (block.contentTop() - offy) + " (" + block.contentWidth() + "," + block.contentHeight() + ")" );
            logger.debug(print_indent() + " " + block.marginTop() + " " + block.marginBottom() );
//            if(block.get_flow_context() == block)
//                logger.debug(print_indent());// + " " + block.get_flow_context().print());
            // negative positioned
            for(Layable lay  : block.getMembersPositioned())
            {
            	if(Boxes.existsInSpace(lay))
            	{
	                adjust_drawarea_for_overflow(area, lay, block);
	
	                if(BoxTypes.isAbsoluteBox(lay))
	                {
	                    ab=BoxTypes.toAbsoluteBox(lay);
	
	                    if(ab.zIndex() < 0)
	                        draw_abs(ab, area, offx, offy, graphics);
	                }
            	}
            }
            draw_block_members(block, area, offx, offy, graphics);
            // 0 then positive positioned boxes

            for(Layable lay  : block.getMembersPositioned())
            {
            	if(Boxes.existsInSpace(lay))
            	{
	                adjust_drawarea_for_overflow(area, lay, block);
	
	                if(BoxTypes.isAbsoluteBox(lay))
	                {
	                    ab=BoxTypes.toAbsoluteBox(lay);
	
	                    if(ab.zIndex() >= 0)
	                        draw_abs(ab, area, offx, offy, graphics);
	                }
            	}
            }
        }
        else
        {

            logger.debug(print_indent() + "[breplace " + /*block.getId() +*/ "] at  " + " at " + (block.left() - offx) + "," + block.top() + " (" + block.width() + "," + block.height() + ") cw " + (block.contentLeft() - offx) + "," + (block.contentTop() - offy) + " (" + block.contentWidth() + "," + block.contentHeight() + ")" );
            logger.debug(print_indent() + " " + block.marginTop() + " " + block.marginBottom() );

            block.replace_object().draw(area.canvas, offx, offy);
        }
        if(block.getOverflow() == Overflow.OF_HIDDEN)
        {
            area.canvas.setClip(null);
            area.clippers.pop();
            if(area.clippers.size() > 0)
                area.canvas.setClip(area.clippers.peek());
        }
        indent--;
    }

    private void draw_block_members(BlockBox block, DrawArea area, int offx, int offy, Graphics2D graphics)
	{
		logger.debug("draw_block_members("+block+","+area+","+offx+","+offy+")");

		BlockBox bb;
		InlineBox ib;
		FloatBox fb;
		TableBox tb;
		InlineBlockBox ibb;
		int rel_left=0, rel_top=0;

        // in-flow non-inline
        for(Layable lay  : block.getMembersFloating())
        {
        	if(Boxes.existsInSpace(lay))
        	{
	            adjust_drawarea_for_overflow(area, lay, block);
	
	            if(BoxTypes.isTableBox(lay))
	                draw_table(BoxTypes.toTableBox(lay), area, offx, offy, graphics);
	            else if(BoxTypes.isBlockBox(lay))
	                draw_block(BoxTypes.toBlockBox(lay), area, offx, offy, graphics);
        	}
        }

        // floats
        for(Layable lay  : block.getMembersFloating())
        {
        	if(Boxes.existsInSpace(lay))
        	{
	            adjust_drawarea_for_overflow(area, lay, block);
	
	            if(BoxTypes.isFloatBox(lay))
	                draw_float(BoxTypes.toFloatBox(lay), area, offx, offy, graphics);
        	}
        }
        // in-flow inline-level descendants

        for(Layable lay  : block.getMembersAll())
        {
        	if(Boxes.existsInSpace(lay))
        	{
	            adjust_drawarea_for_overflow(area, lay, block);
	
	            if(BoxTypes.isInlineBox(lay))
	            {
		            draw_inline(BoxTypes.toInlineBox(lay), area, offx, offy, graphics);
				}
				else if(BoxTypes.isInlineBlockBox(lay))
				{
					draw_inline_block(BoxTypes.toInlineBlockBox(lay), area, offx, offy, graphics);
				}
				else if(BoxTypes.isBlockBox(lay))
				{
					draw_block(BoxTypes.toBlockBox(lay), area, offx, offy, graphics);
				}
        	}
		}
	}

	private void draw_root(RootBox block, Graphics2D graphics, int offx, int offy)
	{
		logger.debug("draw_root("+block+","+graphics+","+offx+","+offy+")");

		AbsoluteBox ab;
		int rel_left=0, rel_top=0;
		DrawArea area=null;

		indent++;

		if(block.is_relative()==true)
		{
			offx-=block.getRelLeft();
			offy-=block.getRelTop();
		}

		draw_background(block, graphics, offx, offy);
		block.draw_borders(offx, offy, graphics);
		if(block.getOverflow()==Overflow.OF_SCROLL)
			area=DrawArea.make_drawarea(AreaType.AT_SCROLLABLE, graphics, block);
		else if(block.getOverflow()==Overflow.OF_VISIBLE)
			area=DrawArea.make_drawarea(AreaType.AT_VISIBLE, graphics, block);
		else if(block.getOverflow()==Overflow.OF_HIDDEN)
			area=DrawArea.make_drawarea(AreaType.AT_CLIPPED, graphics, block);

		System.out.println("area: "+area);

		if(block.replace_object()==null)
		{
			logger.debug(print_indent() + "[block" + /*block.getId() +*/ "] at  " + " at " + (block.left()-offx) + "," + (block.top()-offy) + " (" + block.width() + "," + block.height() + ") cw " + (block.contentLeft()-offx) + "," + (block.contentTop()-offy) + " (" + block.contentWidth() + "," + block.contentHeight() + ")"  );
			logger.debug(print_indent() + " " + block.marginTop() + " " + block.marginBottom()  );

			realMapping.update(block, block.left()-offx, block.top()-offy, block.width(), block.height());
			
//			if(block.get_flow_context()==block)
//			    logger.debug(print_indent());// + " " + block.get_flow_context().print());

			// negative positioned
			for(Layable lay : block.getMembersPositioned())
			{
	        	if(Boxes.existsInSpace(lay))
	        	{
					adjust_drawarea_for_overflow(area, lay, block);
	
					if(BoxTypes.isAbsoluteBox(lay))
					{
						ab=BoxTypes.toAbsoluteBox(lay);
	
						if(ab.zIndex()<0)
							draw_abs(ab, area, offx, offy, graphics);
					}
	        	}
			}

			draw_block_members(block, area, offx, offy, graphics);
			// 0 then positive positioned boxes

			for(Layable lay : block.getMembersPositioned())
			{
	        	if(Boxes.existsInSpace(lay))
	        	{
					adjust_drawarea_for_overflow(area, lay, block);
					if(BoxTypes.isAbsoluteBox(lay))
					{
						ab=BoxTypes.toAbsoluteBox(lay);
	
						if(ab.zIndex()>=0)
							draw_abs(ab, area, offx, offy, graphics);
					}
	        	}
			}
		}
		else
		{
			logger.debug(print_indent() + "[breplace " + /*block.getId() +*/ "] at  " + " at " + (block.left()-offx) + "," + block.top() + " (" + block.width() + "," + block.height() + ") cw " + (block.contentLeft()-offx) + "," + (block.contentTop()-offy) + " (" + block.contentWidth() + "," + block.contentHeight() + ")" );
			logger.debug(print_indent() + " " + block.marginTop() + " " + block.marginBottom()  );

			block.replace_object().draw(graphics, offx, offy);
		}

		indent--;
	}

	public void draw_tree(RootBox root, int offx, int offy, Graphics2D graphics)
	{
		logger.debug("------ draw_tree");
		draw_root(root, graphics, offx, offy);
	}
}

