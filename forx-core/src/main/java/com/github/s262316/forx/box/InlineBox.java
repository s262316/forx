/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.s262316.forx.box;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.fluent.FluentFlowspace;
import com.github.s262316.forx.box.fluent.FluentLine;
import com.github.s262316.forx.box.mediator.BoxRelation;
import com.github.s262316.forx.box.properties.HasAbsolutePosition;
import com.github.s262316.forx.box.properties.HasBorders;
import com.github.s262316.forx.box.properties.HasColour;
import com.github.s262316.forx.box.properties.HasFontProperties;
import com.github.s262316.forx.box.properties.HasLineProperties;
import com.github.s262316.forx.box.properties.HasWordProperties;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PropertiesInjector;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.relayouter.Layouters;
import com.github.s262316.forx.box.relayouter.Relayouter;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.Boxes;
import com.github.s262316.forx.box.util.Direction;
import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.box.util.VerticalAlignment;
import com.github.s262316.forx.box.util.VerticalAlignmentSpecial;
import com.github.s262316.forx.graphics.FontUtils;
import com.github.s262316.forx.graphics.Triangle;
import com.github.s262316.forx.util.ZIndexComparator;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class InlineBox implements Box, Inline, HasBorders, HasWordProperties, HasLineProperties, HasColour, HasAbsolutePosition, HasFontProperties
{
	private final static Logger logger=LoggerFactory.getLogger(InlineBox.class);
	
    public int id=BoxCounter.count++;
    private LinkedList<Inline> inlines=new LinkedList<Inline>();
    private LinkedList<FloatBox> float_list=new LinkedList<FloatBox>();
    private LinkedList<Layable> all=new LinkedList<Layable>();
	private TreeSet<AbsoluteBox> positionedBoxes=Sets.newTreeSet(new ZIndexComparator());    
    private VerticalAlignment verticalAlign;
    private Flowspace _flowspace;
    private int _bl_shift;
    private int marginLeftSize, marginRightSize, marginTopSize, marginBottomSize;
    private int paddingLeftSize, paddingRightSize, paddingTopSize, paddingBottomSize;
    private Border borders[];
    private int BORDER_LEFT, BORDER_RIGHT, BORDER_TOP, BORDER_BOTTOM;
    private Direction _direction;
    private Font font;
    private Drawer drawer;
    protected ReplaceableBoxPlugin _replace_object;
    protected int lineHeight;
    protected int letterSpacing;
    protected int wordSpacing;
    protected Color foregroundColour;
    protected boolean _auto_width, _auto_height;
    protected Visual visual;
    private Dimensionable dimensions=new Dimensionable();
    private boolean relative;
    private int relTop;
    private int relLeft;
    private Box _container;
    private BoxRelation rels;
    private Optional<Integer> futureWidth=Optional.absent();
    private Optional<Integer> futureHeight=Optional.absent();

    public InlineBox(Visual visual, ReplaceableBoxPlugin repl, Drawer d, BoxRelation rels)
    {
        this.visual=visual;
        this._replace_object=repl;
        this.drawer=d;
        _bl_shift=0;
		this.rels=rels;

        borders=new Border[4];
        for(int i=0; i<4; i++)
	        borders[i]=new Border();
    }

    @Override
    public void flow_insert(Box b, int before)
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
    public int floating_boxes()
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
    public int flowing_boxes()
    {
        return inlines.size();
    }

    public boolean isInline()
    {
        return true;
    }

    public void shift_vertical_position(int amount)
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
    public boolean explicit_newline()
    {
        return false;
    }
    
    @Override
	public void calculateStaticPosition(AbsoluteBox newMember)
	{
		// TODO Auto-generated method stub
	}

	private LayoutResult position_inline(Layable layMember)
    {
		logger.debug("position_inline {}", layMember.getId());

        int x, y;//set these on the box to be laid out (inl)
        Rectangle r1;
        SizeResult size;
        int width_of_first_member;
        Line line=null;
        InlineBox inl;

        inl=BoxTypes.toInlineBox(layMember);
        size=inl.compute_dimensions();

        inl.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);
        inl.set_dimensions(size.width.value +
                size.marginLeft.value + size.marginRight.value +
                inl.paddingLeft() + inl.paddingRight() +
                inl.borderLeft().width + inl.borderRight().width,
                size.height.value);
        // find the width of the first member (nonono)
        if(inl.flowing_boxes() > 0)
            width_of_first_member=inl.front_atomic().width();
        else
            width_of_first_member=1;

        // if the box has dimensions
        if(Boxes.existsInSpace(inl))
        {
        	// find the last line
	        if(flowspace().line_count(this) == 0)
	        {
	            // no last line... create one
	            r1=get_flow_context().metricsNoClear(contentLeft(),// atLeastX
	                    contentRight(),//atMostX
	                    contentTop(),// atLeastY
	                    inl.width(),//atLeastWidth
	                    inl.height());
	
	            // we do not create a line
	            x=contentLeft();
	            y=r1.y;
	            
	            // but do check that the inlinebox fits in this box
	            if(r1.y + r1.height - 1 > contentBottom())
	            {
	            	int resizeBy; // TODO by or to?
	                // not enough space in this inline box for another line
	            	resizeBy=r1.y + r1.height - 1 - contentBottom();
	                if(height() == 0)
	                    ++resizeBy;
	                return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, layMember, resizeBy)));
	            }
	        }
	        else
	        {
	        	// looking into member boxes for sizes isn't right.
	        	
	            line=flowspace().back_line(this);
	            // got a last line.. can we fit the first member box on it?
	            // find the right edge of the last box
	            int last_right_edge;
	            if(flowspace().atomic_count(line) > 0)
	                last_right_edge=flowspace().back_atomic(line).right();
	            else
	                last_right_edge=line.left();
	            if(last_right_edge + width_of_first_member + wordSpacing - 1 > line.right())
	            {
	                // is this box stretchy?
	                if(stretchy_cwidth() == true)
	                {
	                    int amount=can_stretch(width_of_first_member + wordSpacing);
	                    if(amount == width_of_first_member + wordSpacing)
	                    {
	                        return new LayoutResult(false, Optional.of(Layouters.moreWidth(this, layMember, width_of_first_member + wordSpacing)));
	                    }
	                }
	                // not enough space on this line
	                r1=get_flow_context().metricsNoClear(contentLeft(),// atLeastX
	                        contentRight(),//atMostX
	                        line.bottom(),// atLeastY
	                        width_of_first_member + wordSpacing,//atLeastWidth
	                        inl.getLineHeight());
	                if(r1.y + r1.height - 1 > contentBottom())
	                {
	                	int resizeBy; // TODO by or to?
	                    // not enough space in this inline box for another line
	                	resizeBy=r1.y + r1.height - 1 - contentBottom();
	                    if(height() == 0)
	                        ++resizeBy;
	                    return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, layMember, resizeBy)));
	                }
	                else
	                {
	                    // there is enough space for another line
	                    // do not create a line here
	                    x=contentLeft();
	                    y=r1.y;
	                }
	            }
	            else
	            {
	                // TODO: is there the height on this line???
	                // there is space on this line
	                x=contentLeft();
	                y=line.top();
	            }
	        }
        }
        else
        {
        	// this inlinebox has no size. just give it the same x,y as its parent
            x=contentLeft();
            y=contentTop();
        }
        
        inl.set_position(x, y);

        logger.debug("position_inline success");

        return new LayoutResult(true, Optional.<Relayouter>absent());
    }
    
    private LayoutResult position_atomic(Layable layMember)
    {
		logger.debug("------------------ position_atomic ---------------- {} ", layMember);

        int x=0;//set these on the box to be laid out
        Rectangle r1;
        LineInfo baseline=new LineInfo();
        SizeResult size;
        Line line;
        AtomicInline lastAtomic;
        AtomicInline ainl;
        boolean overflow_width=false;
        int atMostX, atLeastX;

        ainl=BoxTypes.toAtomic(layMember);
        size=ainl.compute_dimensions();
        ainl.set_dimensions(size.width.value, size.height.value);
        ainl.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);

		logger.debug("atomic size= {} ", size);
        if(size.width.value > contentWidth())
        {
    		int parentCanIncreaseWidthBy=stretchAmountWidth(layMember, size.width.getInt() - contentWidth());
    		if(parentCanIncreaseWidthBy>0)
    		{
    			// take any more width we can get
                return new LayoutResult(false, Optional.of(Layouters.moreWidth(this, layMember, parentCanIncreaseWidthBy)));
    		}        	
        	
        	// flag to ignore width constraints
        	overflow_width=true;
        }

        // find the last atomic
        lastAtomic=ainl.prev_atomic();
        if(lastAtomic == null)
        {
			logger.debug("there is no last atomic");

            int left_margin_space=0, right_margin_space=0;
            // add the margin space required
            // (the atomic is first)
            left_margin_space=marginLeft() + borderLeft().width + paddingLeft();
            // (the atomic is last)
            if(ainl.next_atomic() == null)
                right_margin_space=paddingRight() + borderRight().width + marginRight();

            if(flowspace().line_count(this) > 0)
            {
				logger.debug("there are already lines. get the first line");

                line=flowspace().front_line(this);
			}
            else
            {
				logger.debug("no existing lines found");

                if(overflow_width == true)
                    atMostX=contentLeft() + size.width.value + left_margin_space + right_margin_space;
                else
                    atMostX=right();
                // no last line... create one
                r1=get_flow_context().metricsNoClear(contentLeft(),// atLeastX
                        atMostX,//atMostX
                        contentTop(),// atLeastY
                        size.width.value + left_margin_space + right_margin_space,//atLeastWidth
                        ainl.height());
                if(r1.y + r1.height - 1 > contentBottom())
                {
                	int resizeBy; // TODO by or to?
                    // not enough space in this inline box for another line
                	resizeBy=r1.y + r1.height - 1 - contentBottom();
                    if(height() == 0)
                        ++resizeBy;

                    logger.debug("exiting. need more height");

                    return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, layMember, resizeBy)));
                }
                line=new Line(r1.x, r1.y, r1.width, r1.height, _flowspace, block().getTextAlign());

                logger.debug("created line "+r1.x+","+r1.y+" ("+r1.width+","+r1.height+")");

                if(aligned_subtree_root() != inline_root())
                    line.set_baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root(), BoxTypes.toInlineBox(container()).dummy_atomic_baseline(line, verticalAlign));
                line.set_baseline(aligned_subtree_root(), baseline_from_bottom_for_bl(ainl, r1.y + r1.height - 1));

                logger.debug("adding line to flowspace");

                flowspace().add_line(line);
            }

            // the line should be tall enough because it was created especially
            // for us a few lines above
            x=line.left();
            calculate_vert_position(ainl, line, baseline);

            if(baseline.line_baseline != line.baseline(aligned_subtree_root()))
                line.set_baseline(aligned_subtree_root(), baseline.line_baseline);
        }
        else
        {
			logger.debug("found last atomic "+lastAtomic);

            int last_right_edge;
            int right_margin_space=0;
            last_right_edge=lastAtomic.right();
            if(lastAtomic.container() != ainl.container())
            {
                last_right_edge+=lastAtomic.container().paddingRight() +
                        lastAtomic.container().borderRight().width +
                        lastAtomic.container().marginRight();
            }
            line=lastAtomic.line();
            // this atomic is not first

			logger.debug("last line is "+line);

            // if the atomic is last
            if(ainl.next_atomic() == null)
                right_margin_space=paddingRight() + borderRight().width + marginRight() < 0?0:marginRight();
            else if(ainl.next_atomic().whitespace() == true && ainl.next_atomic().next_atomic() == null)
            {
                int space_width;
//                container().canvas().setFont(container().font());
                space_width=visual.getGraphicsContext().fontMetrics(getFont()).stringWidth(" ");
                right_margin_space=space_width + paddingRight() + borderRight().width + marginRight() < 0?0:marginRight();
            }

            // got a last line.. can we fit the first member box on it?
            if((last_right_edge + right_margin_space + size.width.value + wordSpacing > line.right()) && ainl.whitespace() == false)
            {
				logger.debug("no room on the line");

                if(overflow_width == true)
                    atMostX=contentLeft() + size.width.value + right_margin_space;
                else
                {
                    int amount;
                    atMostX=right();
                    if(stretchy_cwidth() == true)
                    {
                        amount=can_stretch(last_right_edge + right_margin_space + size.width.value + wordSpacing - line.right());
                        // is this box stretchy?
                        if(amount == last_right_edge + right_margin_space + size.width.value + wordSpacing - line.right())
                        {
                            int resizeBy=last_right_edge + right_margin_space + size.width.value + wordSpacing - line.right();
                            
                            return new LayoutResult(false, Optional.of(Layouters.moreWidth(this, layMember, resizeBy)));
                        }
                    }
                }
                // not enough space on this line
                r1=get_flow_context().metricsNoClear(left(),// atLeastX
                        atMostX,//atMostX
                        line.bottom() + 1,// atLeastY
                        size.width.value + wordSpacing + right_margin_space,//atLeastWidth
                        ainl.height());
                if(r1.y + r1.height - 1 > contentBottom())
                {
                	int resizeBy;
                    // not enough space in this inline box for another line
                	resizeBy=r1.y + r1.height - 1 - contentBottom();
                    if(height() == 0)
                        ++resizeBy;

                    return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, layMember, resizeBy)));
                }
                // there is enough space for another line
                line=new Line(r1.x, r1.y, r1.width, r1.height, _flowspace, block().getTextAlign());
                if(aligned_subtree_root() != inline_root())
                    line.set_baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root(), BoxTypes.toInlineBox(container()).dummy_atomic_baseline(line, verticalAlign));
                line.set_baseline(aligned_subtree_root(), baseline_from_bottom_for_bl(ainl, r1.y + r1.height - 1));
                flowspace().add_line(line);
                x=r1.x;
                calculate_vert_position(ainl, line, baseline);
                if(aligned_subtree_root() != inline_root())
                {
                    if(baseline.line_baseline != line.baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root()))
                        line.set_baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root(), baseline.line_baseline);
                }
                else
                {
                    if(baseline.line_baseline != line.baseline(aligned_subtree_root()))
                        line.set_baseline(aligned_subtree_root(), baseline.line_baseline);
                }
                
                if(flowspace().inlineboxesOnLineCount(line)>1)
                	inline_root().reposition_on_line(line);
            }
            else
            {
				logger.debug("room on the line");

                // room on the line
                int new_line_height;
                // the line is big enough width ways for us...
                // ...is this line big enough for our text?
                // this is badly named.. it's really workOutLineHeightGivenWhatsAlreadyOnIt
                new_line_height=workOutLineHeight(ainl, line);
                if(new_line_height <= line.height())
                {
					logger.debug("atomic can fit heightways on line");

                    // no need to mess with the line.. just stick the
                    // atomic right in there
                    x=last_right_edge + wordSpacing + 1;
                    calculate_vert_position(ainl, line, baseline);
                    InlineBox asr=aligned_subtree_root();
                    if(asr != inline_root())
                    {
                        InlineBox casr=BoxTypes.toInlineBox(container()).aligned_subtree_root();
                        if(baseline.line_baseline != line.baseline(casr))
                            line.set_baseline(casr, baseline.line_baseline);
                    }
                    else
                    {
                        if(baseline.line_baseline != line.baseline(asr))
                            line.set_baseline(asr, baseline.line_baseline);
                    }
                    
                    if(flowspace().inlineboxesOnLineCount(line)>1)
                    	inline_root().reposition_on_line(line);                    
                }
                else if(new_line_height > line.height())
                {
					logger.debug("atomic cannot fit heightways on line");

                    // the line height needs changing
                    r1=get_flow_context().metricsNoClear(line.left(),// atLeastX
                            line.right(),//atMostX
                            line.top(),// atLeastY
                            last_right_edge + size.width.value + wordSpacing - 1 + right_margin_space,//atLeastWidth
                            new_line_height);
                    if(r1.x == line.left() && r1.y == line.top())
                    {
                        if(r1.y + r1.height - 1 > contentBottom())
                        {
                        	int resizeBy;
                            // not enough space in this inline box for another line
                        	resizeBy=r1.y + r1.height - 1 - contentBottom();
                            if(height() == 0)
                                ++resizeBy;
                            return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, layMember, resizeBy)));
                        }
                        // can fit on this line
                        line.change_height(new_line_height - line.height());
//                        line.set_baseline(aligned_subtree_root(), baseline_from_bottom_for_bl(ainl, r1.y + r1.height - 1, canvas()));
                        // there is space on this line
                        x=last_right_edge + wordSpacing + right_margin_space - 1;
                        calculate_vert_position(ainl, line, baseline);
                        if(aligned_subtree_root() != inline_root())
                        {
                            if(baseline.line_baseline != line.baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root()))
                                line.set_baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root(), baseline.line_baseline);
                        }
                        else
                        {
                            if(baseline.line_baseline != line.baseline(aligned_subtree_root()))
                                line.set_baseline(aligned_subtree_root(), baseline.line_baseline);
                        }
                        
                        if(flowspace().inlineboxesOnLineCount(line)>1)
                        	inline_root().reposition_on_line(line);
                    }
                    else
                    {

                        if(overflow_width == true)
                            atMostX=contentLeft() + size.width.value + right_margin_space;
                        else
                            atMostX=right();
                        // couldn't extend the line height without moving it...
                        // ...a new line is required
                        r1=get_flow_context().metricsNoClear(contentLeft(),// atLeastX
                                atMostX,//atMostX
                                line.bottom(),// atLeastY
                                size.width.value + wordSpacing + right_margin_space,//atLeastWidth
                                new_line_height);

                        if(r1.y + r1.height - 1 > contentBottom())
                        {
                        	int resizeBy; // TODO by or to?                        	
                            // not enough space in this inline box for another line
                        	resizeBy=r1.y + r1.height - 1 - contentBottom();
                            if(height() == 0)
                                ++resizeBy;

                            logger.debug("exiting need more height {}", resizeBy);

                            return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, layMember, resizeBy)));
                        }
                        else
                        {
                            // there is enough space for another line
                            line=new Line(r1.x, r1.y, r1.width, r1.height, _flowspace, block().getTextAlign());
                            if(aligned_subtree_root() != inline_root())
                                line.set_baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root(), BoxTypes.toInlineBox(container()).dummy_atomic_baseline(line, verticalAlign));
                            line.set_baseline(aligned_subtree_root(), baseline_from_bottom_for_bl(ainl, r1.y + r1.height - 1));
                            flowspace().add_line(line);
                            x=contentLeft();
                            baseline.top=r1.y;
                            calculate_vert_position(ainl, line, baseline);
                            if(aligned_subtree_root() != inline_root())
                            {
                                if(baseline.line_baseline != line.baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root()))
                                    line.set_baseline(BoxTypes.toInlineBox(container()).aligned_subtree_root(), baseline.line_baseline);
                            }
                            else
                            {
                                if(baseline.line_baseline != line.baseline(aligned_subtree_root()))
                                    line.set_baseline(aligned_subtree_root(), baseline.line_baseline);
                            }
                       }
                    }
                }
            }
        }

        flowspace().assoc_atomic(line, ainl);

        ainl.set_position(x, baseline.top);
        ainl.set_baseline(baseline.baseline);
        ainl.set_line(line);

        logger.debug("setting position : "+x+","+baseline.top);
        logger.debug("setting basleine : "+baseline.baseline);
        logger.debug("setting line "+line);

		logger.debug("end ---------------------------------");

        return new LayoutResult(true, Optional.<Relayouter>absent());
    }

    private LayoutResult position_float(FloatBox newMember)
    {
        SizeResult size;
        int y;
        Rectangle r1;
        
		logger.debug("------------------ position_float ---------------- {} ", newMember);
        
        size=newMember.compute_dimensions();
        newMember.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);
        // size.width/size.height are the content width/height
        newMember.set_dimensions(size.width.value + size.marginLeft.value + size.marginRight.value + newMember.paddingLeft() + newMember.paddingRight() + newMember.borderLeft().width + newMember.borderRight().width,
                size.height.value + newMember.marginTop() + newMember.marginBottom() + newMember.paddingTop() + newMember.paddingBottom() + newMember.borderTop().width + newMember.borderBottom().width);
        
        // find last line
        FluentLine fluentLine=FluentFlowspace.from(flowspace())
    	        .lastLine(this).or(FluentLine.DEAD); 
        // find first & last atomic
        Optional<AtomicInline> firstAtomic=fluentLine.firstAtomic();
        Optional<AtomicInline> lastAtomic=fluentLine.lastAtomic();
        
        if(lastAtomic.isPresent() && lastAtomic.get().right()+size.width.value > contentRight())
        	y=fluentLine.line().bottom() + 1;        		
        else if(lastAtomic.isPresent() && lastAtomic.get().right()+size.width.value <= contentRight())
            y=fluentLine.line().top();
        else
        	y=contentTop();

        // don't mess with the flow area unless the box has dimensions
        if(Boxes.existsInSpace(newMember))
        {
	        // look for a space to be taken out of the flow
	        r1=get_flow_context().metricsNoClear(contentLeft(),// atLeastX
	                contentRight(),//atMostX
	                y,// atLeastY
	                newMember.width(),//atLeastWidth
	                newMember.height());
	
	        if(newMember.getFloatPosition() == FloatPosition.RIGHT)
	            r1.x=r1.x + r1.width - 1 - newMember.width() + 1;
	
	        newMember.set_position(r1.x, r1.y);
	        // only change the flow area if the box has dimensions
	
            if(newMember.getFloatPosition() == FloatPosition.LEFT)
                get_flow_context().takeout_flow_area(newMember.top(), newMember.bottom(), newMember.right(), newMember.getFloatPosition());
            else if(newMember.getFloatPosition() == FloatPosition.RIGHT)
                get_flow_context().takeout_flow_area(newMember.top(), newMember.bottom(), newMember.left(), newMember.getFloatPosition());
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
	
	        // relayout the line if necessary
	        if(firstAtomic.isPresent())
	        	return new LayoutResult(true, Optional.of(Layouters.floatOnLine(firstAtomic.get(), newMember)));
	        else
	        	return new LayoutResult(true, Optional.<Relayouter>absent());
        }
        else
        {
        	// floatbox has no dimensions and will collapse horizontally and vertically
            if(newMember.getFloatPosition() == FloatPosition.LEFT)        	
            	newMember.set_position(contentLeft(), y);
            else
            	newMember.set_position(contentRight(), y);
        	
        	return new LayoutResult(true, Optional.<Relayouter>absent());
        }
    }
    
    // this can be called on any box that is a member, and even a new box that is not
    // yet a member (but does have a container). pos points to "inlin" and is an
    // iterator in the FLOW list
    // it is now an iterator in the ALL list

    @Override
    public LayoutResult calculate_position(Layable lay)
    {
        int pos=getMembersAll().indexOf(lay);
        lay=all.get(pos);
        if(BoxTypes.isAtomic(lay) == true)
        {
            return position_atomic(lay);
        }
        else if(BoxTypes.isInlineBox(lay) == true)
        {
            return position_inline(lay);
        }
        else if(BoxTypes.isFloatBox(lay) == true)
        {
            FloatBox fb=BoxTypes.toFloatBox(lay);
            return position_float(fb);
        }
        else if(BoxTypes.isAbsoluteBox(lay) == true)
        {
            return position_absolute(lay, pos);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        
    }

//    @Override
//    public Box resize_width(int from, int amount)
//    {
//        // force the change in width
//        all.get(from).change_width(amount);
//        // not much we can do about this... pass it up the hiearchy
//        return container().resize_width(container().getMembersAll().indexOf(this), amount);
//    }
    
    @Override
    public int stretchAmountWidth(Layable whichChild, int maxExtraAmount)
    {
        if(stretchy_cwidth())
        {
			logger.debug("atomic width is greater than content width - box is stretchy");

            int amount=can_stretch(maxExtraAmount);
            if(amount > 0)
                return amount;
        }
 
       	return 0;
    }    

//	@Override
//    public Layable resize_height(int from, int amount)
//    {
//        Layable requester=all.get(from);
//        Layable earliest_affected=null;
//        // force the change in height
//        requester.change_height(amount);
//        if(requester.bottom() > contentBottom())
//        {
//            amount=requester.bottom() - contentBottom();
//            //  if(sticky_height()==true && amount+contentHeight() > sticky_height_max())
//            //   amout=contentHeight()-sticky_height_max();
//            earliest_affected=container().resize_height(container().getMembersAll().indexOf(this), amount);
//        }
//        // if requester is an atomic...
//        if(earliest_affected == null && BoxTypes.isAtomic(requester) == true)
//            earliest_affected=inline_root();
//        return earliest_affected;
//    }
	
    @Override
	public int stretchAmountHeight(Layable whichChild, int y, int maxExtraAmount)
    {
		return maxExtraAmount;
	}
    
	@Override
	public int canStretchHeight(int amount)
	{
		// space without resizing anything
		int currentAvailSpace=container().contentBottom() - bottom();

		if(currentAvailSpace >= amount)
			return amount;
		else
		{
			if(isAutoHeight())
			{
				amount-=currentAvailSpace;
				currentAvailSpace+=container().canStretchHeight(amount);
			}
		}
		
		return currentAvailSpace;
	}    

	private void unposition_atomic(Layable member)
    {
        AtomicInline ail;
        Line line;
        ail=BoxTypes.toAtomic(member);
        line=ail.line();
        flowspace().disassoc_atomic(line, ail);
        ail.set_line(null);
        // if the line has nowt else on it, disassociate it with the
        // inlinebox too, because a linebox with no atomics on it
        // will never be picked up by the layouter but will hang around,
        // and lineboxes with no atomics cause trouble
        if(flowspace().atomic_count(line) == 0)
        {
            // remove it from container inlineboxes too as they may not
            // get an unposition call
            flowspace().disassoc_inlinebox(line, this);
            Box b=container();
            while(BoxTypes.isInlineBox(b))
            {
                flowspace().disassoc_inlinebox(line, BoxTypes.toInlineBox(b));
                b=b.container();
            }
        }
        
        LayoutUtils.invalidatePosition(member);
    }

    private void unposition_inline(Layable member)
    {
        InlineBox ilb=BoxTypes.toInlineBox(member);
        List<Line> lines=flowspace().line_list(ilb);
        if(lines!=null)
        {
			for(Line l : lines)
				flowspace().disassoc_inlinebox(l, ilb);
		}
        
        LayoutUtils.invalidatePosition(member);
    }

    private void unposition_float(FloatBox member)
    {
    	if(member.top()!=Dimensionable.INVALID &&
    		member.left()!=Dimensionable.INVALID &&
    		member.width()!=Dimensionable.INVALID &&
    		member.height()!=Dimensionable.INVALID &&
    		Boxes.existsInSpace(member))
    	{
        	get_flow_context().putback(member.top(), member.bottom(), member.left(), member.right(), member.getFloatPosition());
    	}
    	
        LayoutUtils.invalidatePosition(member);
    }
    
    private void unposition_absolute(AbsoluteBox member)
    {
    	LayoutUtils.invalidatePosition(member);
    }

    @Override
    public void uncalculate_position(Layable member)
    {
        if(BoxTypes.isAtomic(member) == true)
        {
            unposition_atomic(member);
        }
        else if(BoxTypes.isInlineBox(member) == true)
        {
            unposition_inline(member);
        }
        else if(BoxTypes.isFloatBox(member) == true)
        {
            unposition_float((FloatBox)member);
        }
        else if(BoxTypes.isAbsoluteBox(member) == true)
        {
            unposition_absolute((AbsoluteBox)member);
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
    public void flow_back(Box b)
    {
        if(BoxTypes.isBlockBox(b) == true)
            throw new BoxError(BoxExceptionType.BET_WRONGTYPE);
        else if(BoxTypes.isAtomic(b) == true)//for inline blocks
            flow_back(BoxTypes.toInline(b));
        else
        {
            Inline ib=BoxTypes.toInlineBox(b);
            inlines.add(ib);
            all.add(ib);
            ib.set_container(this);
            ib.computeProperties();
            LayoutUtils.doLoadingLayout(ib);
        }
    }

    @Override
    public void flow_back(Inline ib)
    {
		logger.debug("flow_back "+ib);

        inlines.add(ib);
        all.add(ib);
        ib.set_container(this);
        ib.computeProperties();
        if(BoxTypes.isAtomic(ib))
            set_prev_next(BoxTypes.toAtomic(ib));
        LayoutUtils.doLoadingLayout(ib);
    }

    public void flow_batch_back(List<String> strs, List<SpaceFlag> spaces)
    {
        if(strs.size() != spaces.size())
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        int strit=0;
        int spit=0;
        boolean text_begin=false;
        Text t;
        // ignore leading spaces if this is the first line
        if(flowspace().line_count(this) == 0)
        {
            while(spit < spaces.size())
            {
                if(spaces.get(spit) == SpaceFlag.SF_NOT_SPACE)
                    text_begin=true;
                else
                {
                    strit++;
                    spit++;
                }
            }
        }
        // create Texts for each
        for(; strit <= strs.size(); strit++, spit++)
        {
            t=new Text(strs.get(strit), letterSpacing, spaces.get(spit), _bl_shift);
            t.set_container(this);
            set_prev_next(t);
            inlines.add(t);
            all.add(t);
        }
        // commented out to make this compile
        // layout_all(this, texts);
    }

    @Override
    public void table_back(TableMember cont, TableMember memb)
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);

    }

    private void set_prev_next(AtomicInline ainl)
    {
        Line last_line;
        AtomicInline prev=null, next=null;
        if(flowspace().line_count(this) > 0)
        {
            last_line=flowspace().back_line(this);
            if(flowspace().atomic_count(last_line) > 0)
            {
                prev=flowspace().back_atomic(last_line);
                next=prev.next_atomic();
                ainl.set_prev_atomic(prev);
                ainl.set_next_atomic(next);
                prev.set_next_atomic(ainl);
                if(next != null)
                    next.set_prev_atomic(ainl);
            }
            else
            {
                next=find_next_atomic(all.indexOf(ainl) + 1);
                ainl.set_prev_atomic(null);
                ainl.set_next_atomic(next);
                if(next != null)
                    next.set_prev_atomic(ainl);
            }
        }
        else
        {
            int start=all.indexOf(ainl);
            prev=find_previous_atomic(start);
            if(prev == null)
            {
                next=find_next_atomic(all.indexOf(ainl) + 1);
                ainl.set_prev_atomic(null);
                ainl.set_next_atomic(next);
                if(next != null)
                    next.set_prev_atomic(ainl);
            }
            else
            {
                next=prev.next_atomic();
                ainl.set_prev_atomic(prev);
                ainl.set_next_atomic(next);
                prev.set_next_atomic(ainl);
                if(next != null)
                    next.set_prev_atomic(ainl);
            }
        }
    }

    @Override
    public void flow_back(String str, SpaceFlag space)
    {
		logger.debug("flow_back("+str+", "+space+")");

        Text text;
        Line last_line;
        AtomicInline prev=null, next=null;
        // we don't want to add or lay spaces if it's at the beginning
        // of a line in a new box
        if(!(space == SpaceFlag.SF_SPACE && flowspace().line_count(this) == 0))
        {
            text=new Text(str, letterSpacing, space, _bl_shift);
			logger.debug("creating new Text object "+text.getId());
            text.set_container(this);
            if(flowspace().line_count(this) > 0)
            {
				logger.debug("existing lines already");

                last_line=flowspace().back_line(this);
                if(flowspace().atomic_count(last_line) > 0)
                {
					logger.debug("there are atomics already on the last line");

					// there are atomics already on the last line
                    prev=flowspace().back_atomic(last_line);

                    next=prev.next_atomic();

                    if(prev!=null)
	                    logger.debug("prev is "+((Text)prev).text());
					if(next!=null)
	                    logger.debug("next is "+((Text)next).text());

                    text.set_prev_atomic(prev);
                    text.set_next_atomic(next);
                    prev.set_next_atomic(text);
                    if(next != null)
                        next.set_prev_atomic(text);
                }
                else
                {
					logger.debug("there are no atomics on the last line(!)");

					// there are no atomics on the last line
					// how is this possible?
					System.out.println("***********??????????????************");
                    next=find_next_atomic(all.size());
                    text.set_prev_atomic(null);
                    text.set_next_atomic(next);
                    if(next != null)
                        next.set_prev_atomic(text);
                }
            }
            else
            {
				logger.debug("no lines in this ilb");

				// there are no lines in this ilb
                int start=all.indexOf(text);
                prev=find_previous_atomic(all.size()-1);
                if(prev == null)
                {
                    next=find_next_atomic(all.size());
                    text.set_prev_atomic(null);
                    text.set_next_atomic(next);
                    if(next != null)
                        next.set_prev_atomic(text);
                }
                else
                {
                    next=prev.next_atomic();
                    text.set_prev_atomic(prev);
                    text.set_next_atomic(next);
                    prev.set_next_atomic(text);
                    if(next != null)
                        next.set_prev_atomic(text);
                }
            }

			if(text.prev_atomic()==null)
				logger.debug(" - prev :null");
 			else
				logger.debug(" - prev :"+text.prev_atomic().id+" "+((Text)text.prev_atomic()).text());

			if(text.next_atomic()==null)
				logger.debug(" - next :null");
 			else
				logger.debug(" - next :"+text.next_atomic().id+" "+((Text)text.next_atomic()).text());



            inlines.add(text);
            all.add(text);
            if(table_root() != null)
            {
                SizeResult size;
                size=text.compute_dimensions();
                text.set_dimensions(size.width.value, size.height.value);
                LayoutUtils.doLoadingLayout(text);
            }
            else
            {
//                if(prev != null)
//                {
//                    if(prev.container() == this)
//                        LayoutUtils.doLoadingLayout(prev);
//                    else
//                        LayoutUtils.doLoadingLayout(text);
//                }
//                else
                    LayoutUtils.doLoadingLayout(text);
            }

			if(text.prev_atomic()==null)
				logger.debug(" - prev :null");
 			else
				logger.debug(" - prev :"+text.prev_atomic().id+" "+((Text)text.prev_atomic()).text());

			if(text.next_atomic()==null)
				logger.debug(" - next :null");
 			else
				logger.debug(" - next :"+text.next_atomic().id+" "+((Text)text.next_atomic()).text());

        }

    }

//    public void flow_insert(Box b, int before)
//    {
//        // TODO
//        //if(b.isBlock()==true)
//        // throw BoxException(BET_BLOCK_BOX_CANNOT_BE_ADDED_TO_INLINE_BOX);
//        //InlineBox ib=static_cast<InlineBox>(b);
//        //inlines.insert(before, ib);
//        //ib.set_container(this);
//        //ib.calculate_stacking_position();
//        //ib.compute_properties();
//        //calculate_position(ib, find_inline_all(ib));
//    }
    // before is an iterator into the all list

    public void flow_insert(String str, SpaceFlag space, int before)
    {
        Text text;
        AtomicInline prev, next;
        boolean found=false;
        int before_inline;
        // create the text object
        text=new Text(str, letterSpacing, space, _bl_shift);
        text.set_container(this);
        // find the position in the inline list
        int next_il=before;
        while(next_il != all.size() && !found)
        {
            if(BoxTypes.isInline(all.get(next_il)))
                found=true;
            next_il++;
        }
        if(found == true)
            before_inline=inlines.indexOf(BoxTypes.toInline(all.get(next_il)));
        else
            before_inline=inlines.size();
        // find the prev/next atomics
        int start=before;
        prev=find_previous_atomic(start);
        if(prev == null)
        {
            next=find_next_atomic(before);
            if(next == null)
            {
                text.set_prev_atomic(prev);
                text.set_next_atomic(next);
            }
            else
            {
                text.set_prev_atomic(prev);
                text.set_next_atomic(next);
                next.set_prev_atomic(text);
            }
        }
        else
        {
            next=prev.next_atomic();
            text.set_prev_atomic(prev);
            text.set_next_atomic(prev.next_atomic());
            prev.set_next_atomic(text);
            if(next != null)
                next.set_prev_atomic(text);
        }
        // insert into the inlines list
        inlines.add(before_inline, text);
        // insert into the all list
        all.add(before, text);
        LayoutUtils.doLoadingLayout(text);
    }

    @Override
    public void float_back(FloatBox fb)
    {
        float_list.add(fb);
        all.add(fb);
        fb.set_container(this);
        fb.computeProperties();
        LayoutUtils.doLoadingLayout(fb);
    }

    @Override
    public void float_insert(FloatBox b, int before)
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
	public void absBackStatic(AbsoluteBox b)
	{
        Box ancestor=Boxes.absoluteContainer(b);
        b.setStaticContainer(this);
        ancestor.absBack(b);
	}

    @Override
	public void absBack(AbsoluteBox b)
	{
    	Preconditions.checkArgument(relative==true, "Flowing box cannot be a non-static container of an absolutebox");
    	
    	positionedBoxes.add(b);
        all.add(b);

        b.set_container(this);
        b.computeProperties();
        
        LayoutUtils.doLoadingLayout(b);		
	}

    private AtomicInline find_next_atomic(int start)
    {
        Layable lay;
        InlineBox ilb;
        AtomicInline result=null;
        boolean found=false;

        for(; start < all.size() && found == false; start++)
        {
            lay=all.get(start);
            if(BoxTypes.isAtomic(lay))
            {
                result=BoxTypes.toAtomic(lay);
                found=true;
            }
            else if(BoxTypes.isInline(lay))
            {
                ilb=BoxTypes.toInlineBox(lay);
                result=ilb.front_atomic();
                if(result != null)
                    found=true;
            }
        }
        if(found == false)
        {
            InlineBox cont;
            if(BoxTypes.isInline(container()))
            {
                cont=BoxTypes.toInlineBox(container());
                result=cont.find_next_atomic(cont.getMembersAll().indexOf(this));
            }
        }
        return result;
    }

    private AtomicInline find_previous_atomic(int start)
    {
        Layable lay;
        InlineBox ilb;
        AtomicInline result=null;
        boolean found=false;
        for(; start >= 0 && found == false; start--)
        {
            lay=all.get(start);
            if(BoxTypes.isAtomic(lay))
            {
                result=BoxTypes.toAtomic(lay);
                found=true;
            }
            else if(BoxTypes.isInline(lay))
            {
                ilb=BoxTypes.toInlineBox(lay);
                result=ilb.back_atomic();
                if(result != null)
                    found=true;
            }
        }
        if(found == false)
        {
            InlineBox cont;
            if(BoxTypes.isInlineBox(container()))
            {
                cont=BoxTypes.toInlineBox(container());
                start=cont.getMembersAll().indexOf(this);
                result=cont.find_previous_atomic(start);
            }
        }
        return result;
    }

    @Override
    public void computeProperties()
    {
    	PropertiesInjector.inject(visual, new PropertyBoxAdaptor(this), this, null, null, this, this, null, null, this, this, null, null, this);
    }
    
//    @Override
//    public void compute_properties()
//    {
//    	PropertiesInjector.inject(visual, null, this, null, null, this, this, null, null, null, this, null, null, this);
//    	
//		logger.debug("compute_properties "+getId());
//
//        BorderDescriptor bd=new BorderDescriptor();
//        WordProperties wp=new WordProperties();
//        LineDescriptor linesDescriptor=new LineDescriptor();
//        ColourDescriptor cd=new ColourDescriptor();
//        PositionDescriptor pd=new PositionDescriptor();
//        Font f;
//
//        // important to get this before any other properties
//        f=visual.workOutFontProperties(this);
//        set_font(f);
//        logger.debug("font : "+f);
//
//        // inline boxes do not have dimensions... they just fill up the space that is there
//        visual.calculateBorders(this, bd);
//        visual.workOutLineProperties(this, linesDescriptor);
//        visual.workOutWordProperties(this, wp);
//        visual.workoutColours(this, cd);
//        if(is_relative() == true)
//        {
//            visual.workOutAbsolutePosition(this, pd);
//            if(pd.left.specified == SpecialLength.SL_AUTO && pd.right.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_left(-pd.right.value);
//            if(pd.right.specified == SpecialLength.SL_AUTO && pd.left.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_left(pd.left.value);
//            if(pd.top.specified == SpecialLength.SL_AUTO && pd.bottom.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_top(-pd.bottom.value);
//            if(pd.bottom.specified == SpecialLength.SL_AUTO && pd.top.specified == SpecialLength.SL_SPECIFIED)
//                set_rel_top(pd.top.value);
//        }
//        _line_height=(int) linesDescriptor.lineHeight;
//        _vertical_align=linesDescriptor.verticalAlign;
//        if(_vertical_align.specified == VerticalAlignmentSpecial.VAS_SUPER)
//            _bl_shift=-canvas().getFontMetrics(font()).getHeight();
//        else if(_vertical_align.specified == VerticalAlignmentSpecial.VAS_SUB)
//            _bl_shift=canvas().getFontMetrics(font()).getHeight();
//        else if(_vertical_align.specified == VerticalAlignmentSpecial.VAS_LENGTH)
//            _bl_shift=-_vertical_align.value;
//        _letter_spacing=wp.letter_spacing;
////        if(wp.word_spacing == SpecialLength.SL_AUTO)
////            _word_spacing=0;
////        else
//        _word_spacing=wp.word_spacing;
//        set_borders(bd);
//        _foreground_colour=cd.foreground;
//    }

    @Override
    public SizeResult compute_dimensions()
    {
        SizeResult result=new SizeResult();
        Length widthProperty=new Length(0), heightProperty=new Length(0);
        MarginDescriptor md=new MarginDescriptor();
        visual.computeMarginProperties(new PropertyBoxAdaptor(this), md);
        // work out the width
        calculateWidth(widthProperty, md.left, md.right, heightProperty);
        result.width=widthProperty;
        // work out the height
        // height value DOES NOT include the borders/margins/padding
        calculateHeight(heightProperty, md.top, md.bottom, widthProperty);
        result.height=heightProperty;
        result.marginLeft=md.left;
        result.marginTop=md.top;
        result.marginRight=md.right;
        result.marginBottom=md.bottom;
        return result;
    }

    private void calculateWidth(Length width, Length marginLeft, Length marginRight, Length height)
    {
        int spend;
        // The 'width' property does not apply. A computed value of 'auto' for 'left',
        // 'right', 'margin-left' or 'margin-right' becomes a used value of '0'.
        if(marginLeft.specified == SpecialLength.SL_AUTO)
            marginLeft.set(0);
        if(marginRight.specified == SpecialLength.SL_AUTO)
            marginRight.set(0);
        // we make the width as wide as the content width of the containing block
        spend=marginLeft.value + marginRight.value + paddingLeft() + paddingRight() + borderLeft().width + borderRight().width;
        
        if(futureWidth.isPresent())
        	width.set(futureWidth.get()-marginLeft.getInt()-marginRight.getInt());
        else if(spend > container().contentWidth())
        {
            // not enough space in the box... set it to be big enough and let someone else
            // worry about it!
            width.set(spend);
        }
        else
        {
            // set the width to be all of the available width
            width.set(container().contentWidth() -
                    (marginLeft.value + marginRight.value + paddingLeft() + paddingRight() +
                    borderLeft().width + borderRight().width));
        }
    }

    private void calculateHeight(Length height, Length marginTop, Length marginBottom,
            Length width)
    {
        // The 'height' property doesn't apply
        // The vertical padding, border and margin of an inline, non-replaced box start at the top and bottom
        // of the content area, not the 'line-height'. But only the 'line-height' is used when calculating the
        // height of the line box.
        if(marginTop.specified == SpecialLength.SL_AUTO)
            marginTop.set(0);
        if(marginBottom.specified == SpecialLength.SL_AUTO)
            marginBottom.set(0);
        
        if(futureHeight.isPresent())
        	height.set(futureHeight.get());
        else
        	height.set(0);
    }

    public class LineInfo
    {
        int line_baseline;
        int baseline;
        int top;
    }

    public void calculate_vert_position(AtomicInline place, Line on, LineInfo baseline_out)
    {
		logger.debug("calculate_vert_position({},{},{})", place, on, baseline_out);

        if(place.replace_object() == null)
        {
            // non-replaced elements
            if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BASELINE)
            {
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root());
                baseline_out.top=top_from_baseline(place, baseline_out.baseline);

                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_BOTTOM)
            {
                int bottommost;
                int bottom, diff;
                bottommost=inline_bottom_content(on);
                bottom=bottommost;
                baseline_out.top=bottom - place.height() + 1;
                baseline_out.baseline=baseline_from_bottom(place, bottom);
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                if(baseline_out.top < on.top())
                {
                    diff=on.top() - baseline_out.top;
                    bottom+=diff;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_TOP)
            {
				logger.debug("not in baseline102");
                int topmost;
                int bottom, diff;
                topmost=inline_top_content(on);
                baseline_out.top=topmost;
                bottom=baseline_out.top + place.height() - 1;
                baseline_out.baseline=baseline_from_bottom(place, bottom);
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                if(bottom > on.bottom())
                {
                    diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    bottom-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_MIDDLE)
            {
				// Align the vertical midpoint of the box with the baseline of the parent box
				// plus half the x-height of the parent.

				int bottom, middle, baseline;

                baseline_out.line_baseline=on.baseline((InlineBox)place.container().container());

                middle=baseline_out.line_baseline-(FontUtils.x_height(place.container().container().getFont(), visual.getGraphicsContext().fontMetrics(place.container().container().getFont()))/2);

				baseline_out.baseline=baseline_from_middle(place, middle);
				baseline_out.top=top_from_baseline(place, baseline_out.baseline);
				bottom=baseline_out.top+place.height()-1;

                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
				}

			}
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUPER)
            {
				logger.debug("VAS_SUPER: "+_bl_shift);

                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root()) - Math.abs(((InlineBox)place.container()).bl_shift());
                baseline_out.top=top_from_baseline(place, baseline_out.line_baseline);

                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUB)
            {
                int bottom;
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root()) + Math.abs(((InlineBox)place.container()).bl_shift());
                baseline_out.top=top_from_baseline(place, baseline_out.line_baseline);
                bottom=bottom_from_baseline(place, baseline_out.line_baseline);
                if(bottom > on.bottom())
                {
                    int diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_LENGTH)
            {
                int bottom;
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root()) + ((InlineBox)place.container()).bl_shift();
                baseline_out.top=top_from_baseline(place, baseline_out.line_baseline);
                bottom=bottom_from_baseline(place, baseline_out.line_baseline);
                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
                if(bottom > on.bottom())
                {
                    int diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BOTTOM)
            {
                int bottom, diff;
                bottom=lowest_point_on_line(on, place);
                baseline_out.top=bottom - place.height() + 1;
                baseline_out.baseline=baseline_from_bottom(place, bottom);
                // the container will always be an inlinebox if we have top or bottom
                baseline_out.line_baseline=on.baseline(BoxTypes.toInlineBox(place.container().container()).aligned_subtree_root());
                if(baseline_out.top < on.top())
                {
                    diff=on.top() - baseline_out.top;
                    bottom+=diff;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TOP)
            {
                int bottom, diff;
                baseline_out.top=highest_point_on_line(on, place);
                bottom=baseline_out.top + place.height() - 1;
                baseline_out.baseline=baseline_from_bottom(place, bottom);
                // the container will always be an inlinebox if we have top or bottom
                baseline_out.line_baseline=on.baseline(BoxTypes.toInlineBox(place.container().container()).aligned_subtree_root());
                if(bottom > on.bottom())
                {
                    diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    bottom-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
        }
        else
        {
            // replaced elements
            if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BASELINE)
            {
                // when the replaced box is placed on the baseline, it's top
                // could be higher than the top of the line. in this case
                // the line must be made taller. the baseline does not move
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root());
                baseline_out.top=baseline_out.baseline - place.height() + 1;
                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_BOTTOM)
            {
                int bottommost;
                int bottom, diff;
                bottommost=inline_bottom_content(on);
                bottom=bottommost;
                baseline_out.top=bottom - place.height() + 1;
                baseline_out.baseline=on.baseline(aligned_subtree_root());
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                if(baseline_out.top < on.top())
                {
                    diff=on.top() - baseline_out.top;
                    bottom+=diff;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_TOP)
            {
                int topmost;
                int bottom, diff;
                topmost=inline_top_content(on);
                baseline_out.top=topmost;
                bottom=baseline_out.top + place.height() - 1;
                baseline_out.baseline=on.baseline(aligned_subtree_root());
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                if(bottom > on.bottom())
                {
                    diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    bottom-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_MIDDLE)
            {
                int bottom, middle;
                middle=on.top() + ((on.bottom() - on.top()) / 2);
                baseline_out.top=middle - (place.height() / 2);
                bottom=baseline_out.top + place.height() - 1;
                baseline_out.baseline=bottom - visual.getGraphicsContext().fontMetrics(getFont()).getDescent();
                // middle + half container xheight - not quite right yet
                baseline_out.line_baseline=middle + (visual.getGraphicsContext().fontMetrics(getFont()).getHeight() / 2);
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUPER)
            {
                int bottom;
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root()) - Math.abs(place.bl_shift());
                bottom=baseline_out.baseline;
                baseline_out.top=baseline_out.baseline - place.height() + 1;
                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
                if(bottom > on.bottom())
                {
                    int diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUB)
            {
                int bottom;
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root()) + Math.abs(place.bl_shift());
                bottom=baseline_out.baseline;
                baseline_out.top=bottom - place.height() + 1;
                if(bottom > on.bottom())
                {
                    int diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_LENGTH)
            {
                int bottom;
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                baseline_out.baseline=on.baseline(aligned_subtree_root()) + place.bl_shift();
                bottom=baseline_out.baseline;
                baseline_out.top=bottom - place.height() + 1;
                if(baseline_out.top < on.top())
                {
                    int diff=on.top() - baseline_out.top;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
                if(bottom > on.bottom())
                {
                    int diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BOTTOM)
            {
                int bottom, diff;
                bottom=lowest_point_on_line(on, place);
                baseline_out.top=bottom - place.height() + 1;
                baseline_out.baseline=bottom;
                // the container will always be an inlinebox if we have top or bottom
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                if(baseline_out.top < on.top())
                {
                    diff=on.top() - baseline_out.top;
                    bottom+=diff;
                    baseline_out.top+=diff;
                    baseline_out.baseline+=diff;
                    baseline_out.line_baseline+=diff;
                }
            }
            else if(place.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TOP)
            {
                int bottom, diff;
                baseline_out.top=highest_point_on_line(on, place);
                bottom=baseline_out.top + place.height() - 1;
                baseline_out.baseline=bottom;
                // the container will always be an inlinebox if we have top or bottom
                baseline_out.line_baseline=on.baseline(aligned_subtree_root());
                if(bottom > on.bottom())
                {
                    diff=bottom - on.bottom();
                    baseline_out.top-=diff;
                    bottom-=diff;
                    baseline_out.baseline-=diff;
                    baseline_out.line_baseline-=diff;
                }

            }
            else
            {
				logger.debug("not in baseline118");
			}
        }
    }

    public int inline_top_content(Line on)
    {
		logger.debug("inline_top_content");

        int top;
        if(verticalAlign.specified == VerticalAlignmentSpecial.VAS_TEXT_TOP)
        {

            if(BoxTypes.isInlineBox(container()) == true)
                top=BoxTypes.toInlineBox(container()).inline_top_content(on);
            else
                top=on.baseline(aligned_subtree_root()) - visual.getGraphicsContext().fontMetrics(font).getAscent() - visual.getGraphicsContext().fontMetrics(font).getLeading();
        }
        else
        {
			logger.debug("font is "+getFont());

            Text t=new Text("X", 0, SpaceFlag.SF_NOT_SPACE, _bl_shift);
            LineInfo baseline=new LineInfo();
            SizeResult size;
            t.set_container(this);
            size=t.compute_dimensions();
            t.set_dimensions(size.width.value, size.height.value);
            calculate_vert_position(t, on, baseline);
            t.set_position(0, baseline.top);
            top=t.content_top();
        }
        return top;
    }

    public int inline_bottom_content(Line on)
    {
        int bottom;

		logger.debug("inline_bottom_content");

        if(verticalAlign.specified == VerticalAlignmentSpecial.VAS_TEXT_BOTTOM)
        {
            if(BoxTypes.isInlineBox(container())== true)
                bottom=BoxTypes.toInlineBox(container()).inline_bottom_content(on);
            else
                bottom=on.baseline(aligned_subtree_root()) + visual.getGraphicsContext().fontMetrics(font).getDescent();
        }
        else
        {
			logger.debug("font() : "+getFont());

            Text t=new Text("X", 0, SpaceFlag.SF_NOT_SPACE, _bl_shift);
            LineInfo baseline=new LineInfo();
            SizeResult size;
            t.set_container(this);
            size=t.compute_dimensions();
            t.set_dimensions(size.width.value, size.height.value);
            calculate_vert_position(t, on, baseline);
            t.set_position(0, baseline.top);
            bottom=t.content_bottom();
        }
        return bottom;
    }

    private int highest_point_on_line(Line on, AtomicInline laying)
    {
        AtomicInline ainl;
        int highest;
        if(flowspace().atomic_count(on) > 0)
        {
            ainl=flowspace().front_atomic(on);
            if(ainl != null && ainl != laying)
            {
                highest=ainl.top();
                while(ainl != null && ainl != laying)
                {
                    if(ainl.top() < highest)
                        highest=ainl.top();
                    ainl=ainl.next_atomic();
                }
            }
            else
                highest=on.top();
        }
        else
            highest=on.top();
        return highest;
    }

    private int lowest_point_on_line(Line on, AtomicInline laying)
    {
        AtomicInline ainl;
        int lowest;
        if(flowspace().atomic_count(on) > 0)
        {
            ainl=flowspace().front_atomic(on);
            if(ainl != null && ainl != laying)
            {
                lowest=ainl.bottom();
                while(ainl != null && ainl != laying)
                {
                    if(ainl.bottom() > lowest)
                        lowest=ainl.bottom();
                    ainl=ainl.next_atomic();
                }
            }
            else
                lowest=on.bottom();
        }
        else
            lowest=on.bottom();
        return lowest;
    }

    public int workOutLineHeight(AtomicInline ainl, Line on)
    {
        int lineheight;
        int top, bottom;

        logger.debug("workOutLineHeight");

        if(ainl.replace_object() == null && !BoxTypes.isBox(ainl))
        {
            // non-replaced elements
            if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BASELINE)
            {
                top=top_from_baseline(ainl, on.baseline(aligned_subtree_root()));
                bottom=bottom_from_baseline(ainl, on.baseline(aligned_subtree_root()));

                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();

                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_TOP)
            {
				logger.debug("not in baseline1");

                top=inline_top_content(on);
                bottom=top + ainl.height() - 1;
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_BOTTOM)
            {
				logger.debug("not in baseline2");
                bottom=inline_bottom_content(on);
                top=bottom - ainl.height() + 1;
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_MIDDLE)
            {
				logger.debug("not in baseline3");
				int baseline=on.baseline(aligned_subtree_root()) - (FontUtils.x_height(getFont(), visual.getGraphicsContext().fontMetrics(getFont())) / 2);

                top=top_from_baseline(ainl, baseline);
                bottom=bottom_from_baseline(ainl, baseline);
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();

                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUPER)
            {
				logger.debug("not in baseline4");
                top=top_from_baseline(ainl, on.baseline(aligned_subtree_root()));
                lineheight=on.bottom() - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUB)
            {
				logger.debug("not in baseline5");
                bottom=bottom_from_baseline(ainl, on.baseline(aligned_subtree_root()));
                lineheight=bottom - on.top() + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_LENGTH)
            {
				logger.debug("not in baseline6");
                top=top_from_baseline(ainl, on.baseline(aligned_subtree_root()));
                bottom=bottom_from_baseline(ainl, on.baseline(aligned_subtree_root()));
                if(on.top() < top)
                    top=on.top();
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TOP)
            {
				logger.debug("not in baseline7");
                top=on.top();
                bottom=top + ainl.height() - 1;
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BOTTOM)
            {
				logger.debug("not in baseline8");
                bottom=on.bottom();
                top=bottom - ainl.height() + 1;
                lineheight=bottom - top + 1;
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        else
        {
            // replaced elements and inline blocks
            if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BASELINE)
            {
				logger.debug("not in baseline9");

                // when the replaced box is placed on the baseline, it's top
                // could be higher than the top of the line. in this case
                // the line must be made taller. the baseline does not move
                bottom=on.baseline(aligned_subtree_root());
                top=bottom - ainl.height() + 1;
                if(top < on.top())
                    lineheight=on.bottom() - top + 1;
                else
                    lineheight=this.lineHeight;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_TOP)
            {
				logger.debug("not in baseline10");
                top=inline_top_content(on);
                bottom=top + ainl.height() - 1;
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TEXT_BOTTOM)
            {
				logger.debug("not in baseline11");
                bottom=inline_bottom_content(on);
                top=bottom - ainl.height() + 1;
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_MIDDLE)
            {
//                int middle, bottom;
				logger.debug("not in baseline12");
                int middle=on.top() + ((on.bottom() - on.top()) / 2);
                top=middle - (ainl.height() / 2);
                bottom=top + ainl.height() - 1;
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUPER)
            {
				logger.debug("not in baseline13");
                bottom=on.baseline(aligned_subtree_root());
                bottom-=Math.abs(ainl.bl_shift());
                top=bottom - ainl.height() + 1;
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                if(on.top() < top)
                    top=on.top();
                lineheight=on.bottom() - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_SUB)
            {
				logger.debug("not in baseline14");
                bottom=on.baseline(aligned_subtree_root());
                bottom+=Math.abs(ainl.bl_shift());
                top=bottom - ainl.height() + 1;
                bottom=Math.max(on.bottom(), bottom);
                top=Math.min(on.top(), top);
                lineheight=bottom - on.top() + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_LENGTH)
            {
				logger.debug("not in baseline15");
                bottom=on.baseline(aligned_subtree_root());
                bottom+=Math.abs(ainl.bl_shift());
                top=bottom - ainl.height() + 1;
                if(on.top() < top)
                    top=on.top();
                if(on.bottom() > bottom)
                    bottom=on.bottom();
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TOP)
            {
				logger.debug("not in baseline16");
                top=on.top();
                bottom=top + ainl.height() - 1;
                lineheight=bottom - top + 1;
            }
            else if(ainl.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BOTTOM)
            {
				logger.debug("not in baseline17");
                bottom=on.bottom();
                top=bottom - ainl.height() + 1;
                lineheight=bottom - top + 1;
            }
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }

        logger.debug("workOutLineHeight : "+lineheight);

        return lineheight;
    }

    public BlockBox block()
    {
        Box b=container();
        while(BoxTypes.isBlockBox(b) == false)
            b=BoxTypes.toBlockBox(b);
        return BoxTypes.toBlockBox(b);
    }

    @Override
    public ReplaceableBoxPlugin replace_object()
    {
        return this._replace_object;
    }

    @Override
    public void set_dimensions(int width, int height)
    {
        dimensions.set_width(width);
        dimensions.set_height(height);
    }

    @Override
    public void set_width(int width)
    {
        dimensions.set_width(width);
    }

    @Override
    public void set_height(int height)
    {
        dimensions.set_height(height);
    }

    @Override
    public void set_container(Box cont)
    {
        _container=cont;

        if(BoxTypes.isBlockBox(cont))
            _flowspace=new Flowspace();
        else
            _flowspace=BoxTypes.toInlineBox(cont).flowspace();
    }

    public AtomicInline front_atomic()
    {
        Inline il=null;
        boolean found=false;
        for(int it=0; it<inlines.size() && !found; it++)
        {
            il=inlines.get(it);
            if(BoxTypes.isAtomic(il) == false)
            {
                il=BoxTypes.toInlineBox(il).front_atomic();
                if(il != null)
                    found=true;
            }
            else if(BoxTypes.isAtomic(il) == true)
                found=true;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        return BoxTypes.toAtomic(il);
    }

    public AtomicInline back_atomic()
    {
        Inline il=null;
        boolean found=false;

        for(int rit=inlines.size() - 1; rit >= 0 && !found; rit--)
        {
            il=inlines.get(rit);
            if(BoxTypes.isAtomic(il) == false)
            {
                il=BoxTypes.toInlineBox(il).back_atomic();
                if(il != null)
                    found=true;
            }
            else if(BoxTypes.isAtomic(il) == true)
                found=true;
            else
                throw new BoxError(BoxExceptionType.BET_UNKNOWN);
        }
        if(found == false)
            il=null;

        return BoxTypes.toAtomic(il);
    }

    public Flowspace flowspace()
    {
        return _flowspace;
    }

	// currently laid object has a line of null
    // this is not done using the normal layouter. shuffle previous boxes on the
    // line without it really knowing that we did it
    public void reposition_on_line(Line line)
    {
		logger.debug("reposition_on_line : "+line.getId());

        double dur;
        boolean on_line=true;
        Inline inl;
        InlineBox ilb_to_notify;
        AtomicInline ainl_to_notify;

        if(flowspace().atomic_count(line) > 0)
        {
            ainl_to_notify=flowspace().front_atomic(line);
            logger.debug(ainl_to_notify.toString());
            while(ainl_to_notify!=null && on_line == true)
            {
				logger.debug("ainl_to_notify "+ainl_to_notify.getId());

                on_line=(line == ainl_to_notify.line());
                if(on_line == true)
                {
                    LineInfo baseline=new LineInfo();
                    // the atomics may be in different inlineboxes with
                    // different fonts
                    calculate_vert_position(ainl_to_notify, line, baseline);
                    ainl_to_notify.set_position(ainl_to_notify.left(), baseline.top);
                    ainl_to_notify.set_baseline(baseline.baseline);
                }

                ainl_to_notify=ainl_to_notify.next_atomic();
            }
        }
    }

    public InlineBox inline_root()
    {
        Box subj=this;
        while(BoxTypes.isInlineBox(subj.container()))
            subj=subj.container();
        return BoxTypes.toInlineBox(subj);
    }

    public InlineBox aligned_subtree_root()
    {
        InlineBox subj=this;
        if(subj.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_TOP ||
                subj.getVerticalAlign().specified == VerticalAlignmentSpecial.VAS_BOTTOM)
        {
        } // found
        else if(BoxTypes.isInlineBox(subj.container()))
            subj=BoxTypes.toInlineBox(container()).aligned_subtree_root();
        else
        {
        } // inline root

        return subj;
    }

    public void draw_borders(int offx, int offy, Graphics2D graphics)
    {
		logger.debug("draw_borders");

        Text t=new Text("X", 0, SpaceFlag.SF_NOT_SPACE, _bl_shift);
        LineInfo baseline=new LineInfo();
        int top, bottom, left, right;
        SizeResult size;
        AtomicInline first, last;
        boolean drawleft, drawright;
        t.set_container(this);
        size=t.compute_dimensions();
        t.set_dimensions(size.width.value, size.height.value);
        // first line and last line are different....
        // for each line...

		List<Line> lines=flowspace().line_list(this);
        for(Line on : lines)
        {
            calculate_vert_position(t, on, baseline);
            t.set_position(0, baseline.top);
            top=t.content_top();
            bottom=t.content_bottom();
            first=flowspace().front_atomic(on);
            last=flowspace().back_atomic(on);
            while(first.is_descendent_of(this) == false && first != last)
            {
                // first should never be null.... (except for an empty inline box)
                first=first.next_atomic();
            }
            // just in case this box has no member boxes on the line
            if(first != last || last.line() == on)
            {
                while(last.is_descendent_of(this) == false && last != first)
                {
                    // last should never be null!
                    last=last.prev_atomic();
                }
                left=first.left();
                right=last.right();

                if(first.prev_atomic() == null)
                    drawleft=true;
                else if(first.prev_atomic().container() != first.container())
                    drawleft=true;
                else
                    drawleft=false;
                if(last.next_atomic() == null)
                    drawright=true;
                else if(last.next_atomic().container() != last.container())
                    drawright=true;
                else
                    drawright=false;
                draw_inline_border(left - paddingLeft() - 1, top - paddingTop() - 1, right + paddingRight() + 1, bottom + paddingBottom() + 1, drawleft, drawright, graphics);
            }
        }

        logger.debug("end draw_borders");
    }
    // these coordinates are the edges of the padding area

    public void draw_inline_border(int left, int top, int right, int bottom, boolean leftside, boolean rightside, Graphics2D graphics)
    {
        // top
        Dimensionable top_dims=new Dimensionable(left - borderLeft().width,
                top,
                right - left + 1 + borderLeft().width + borderRight().width,
                borderTop().width);
        // right
        Dimensionable right_dims=new Dimensionable(right + 1,
                top,
                borderRight().width,
                bottom - top + 1);
        // bottom
        Dimensionable bottom_dims=new Dimensionable(left - borderLeft().width,
                bottom - borderBottom().width + 1,
                right - left + 1 + borderLeft().width + borderRight().width,
                borderBottom().width);
        // left
        Dimensionable left_dims=new Dimensionable(left - borderLeft().width,
                top,
                borderLeft().width,
                bottom - top + 1);
        Polygon top_left_clip=Triangle.newTriangle(
                top_dims.left(),
                top_dims.top(),
                left_dims.right(),
                top_dims.top(),
                left_dims.right(),
                top_dims.bottom());
        Polygon top_right_clip=Triangle.newTriangle(
                right_dims.left(),
                top_dims.top(),
                right_dims.right(), top_dims.top(),
                right_dims.left(), top_dims.bottom());
        Polygon left_top_clip=Triangle.newTriangle(left_dims.left(), left_dims.top(),
                left_dims.right(), top_dims.bottom(),
                left_dims.left(), top_dims.bottom());
        Polygon left_bottom_clip=Triangle.newTriangle(left_dims.left(), bottom_dims.top(),
                left_dims.right(), bottom_dims.top(),
                left_dims.left(), left_dims.bottom());
        Polygon right_top_clip=Triangle.newTriangle(right_dims.right(), right_dims.top(),
                right_dims.right(), top_dims.bottom(),
                right_dims.left(), top_dims.bottom());
        Polygon right_bottom_clip=Triangle.newTriangle(right_dims.left(), bottom_dims.top(),
                right_dims.right(), right_dims.bottom(),
                right_dims.right(), bottom_dims.top());
        Polygon bottom_left_clip=Triangle.newTriangle(left_dims.right(), bottom_dims.top(),
                left_dims.right(), bottom_dims.bottom(),
                bottom_dims.left(), bottom_dims.bottom());
        Polygon bottom_right_clip=Triangle.newTriangle(right_dims.left(), bottom_dims.top(),
                bottom_dims.right(), bottom_dims.bottom(),
                right_dims.left(), bottom_dims.bottom());
        switch(borderTop().style)
        {
            case BS_NONE:
                break;
            case BS_HIDDEN:
                break;
            case BS_DOTTED:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawHorizBorderDotted(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_DASHED:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawHorizBorderDashed(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_SOLID:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawTopBorderSolid(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_DOUBLE:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawHorizBorderDouble(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_GROOVE:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawHorizBorderGroove(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_RIDGE:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawHorizBorderRidge(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_INSET:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawTopBorderInset(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_OUTSET:
                graphics.setColor(borderTop().colour);
                BoxBorders.drawTopBorderOutset(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
        }
        if(rightside)
        {
            switch(borderRight().style)
            {
                case BS_NONE:
                    break;
                case BS_HIDDEN:
                    break;
                case BS_DOTTED:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawVertBorderDotted(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_DASHED:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawVertBorderDashed(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_SOLID:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawRightBorderSolid(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_DOUBLE:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawVertBorderDouble(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_GROOVE:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawVertBorderGroove(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_RIDGE:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawVertBorderRidge(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_INSET:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawRightBorderInset(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_OUTSET:
                    graphics.setColor(borderRight().colour);
                    BoxBorders.drawRightBorderOutset(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
            }
        }
        switch(borderBottom().style)
        {
            case BS_NONE:
                break;
            case BS_HIDDEN:
                break;
            case BS_DOTTED:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawHorizBorderDotted(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
            case BS_DASHED:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawHorizBorderDashed(bottom_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
                break;
            case BS_SOLID:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawBottomBorderSolid(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
            case BS_DOUBLE:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawHorizBorderDouble(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
            case BS_GROOVE:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawHorizBorderGroove(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
            case BS_RIDGE:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawHorizBorderRidge(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
            case BS_INSET:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawBottomBorderInset(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
            case BS_OUTSET:
                graphics.setColor(borderBottom().colour);
                BoxBorders.drawBottomBorderOutset(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
                break;
        }
        if(leftside)
        {
            switch(borderLeft().style)
            {
                case BS_NONE:
                    break;
                case BS_HIDDEN:
                    break;
                case BS_DOTTED:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawVertBorderDotted(left_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_DASHED:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawVertBorderDashed(left_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
                    break;
                case BS_SOLID:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawLeftBorderSolid(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
                    break;
                case BS_DOUBLE:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawVertBorderDouble(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
                    break;
                case BS_GROOVE:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawVertBorderGroove(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
                    break;
                case BS_RIDGE:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawVertBorderRidge(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
                    break;
                case BS_INSET:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawLeftBorderInset(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
                    break;
                case BS_OUTSET:
                    graphics.setColor(borderLeft().colour);
                    BoxBorders.drawLeftBorderOutset(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
                    break;
            }
        }
    }

    @Override
    public boolean is_descendent_of(Box b)
    {
        Box cont=container();
        while(cont != null && cont != b)
            cont=cont.container();
        if(cont == null)
            return false;
        return true;
    }

    @Override
    public int contentTop()
    {
        return top();
    }

    @Override
    public int contentBottom()
    {
        return bottom();
    }

    @Override
    public int contentHeight()
    {
        return height();
    }

    @Override
    public int atomic_width()
    {
        int w=0;
        for(Inline inl : inlines)
        {
            w+=inl.atomic_width();
        }
        return w;
    }

    @Override
    public int preferred_width()
    {
        int pw=0;
        for(Inline inl : inlines)
        {
            pw+=inl.preferred_width();
        }
        return pw;
    }

    @Override
    public int preferred_min_width()
    {
        int pmw=0;
        for(Inline inl : inlines)
        {
            pmw=Math.max(pmw, inl.preferred_min_width());
        }
        return pmw;
    }

    @Override
    public int preferred_shrink_width(int avail_width)
    {
        int biggest_psw=0;
        int psw_total=0;
        int psw;
        AtomicInline ainl, prev=null;
        ainl=front_atomic();
        while(ainl != null)
        {
            psw=ainl.width();
            if(psw_total + psw > avail_width)
            {
                // deduct eol whitespace from the shrink width
                if(prev != null)
                {
                    if(prev.whitespace() == true)
                        psw_total-=prev.width();
                }
                if(psw_total > biggest_psw)
                    biggest_psw=psw_total;
                psw_total=0;
                // don't include bol whitespace
                if(ainl.whitespace() == false)
                    psw_total+=psw;
            }
            else
            {
                psw_total+=psw;
            }
            prev=ainl;
            ainl=ainl.next_atomic();
        }
        // last line
        // deduct eol whitespace from the shrink width
        if(prev != null)
        {
            if(prev.whitespace() == true)
                psw_total-=prev.width();
        }
        if(psw_total > biggest_psw)
            biggest_psw=psw_total;
        return biggest_psw;
    }

    @Override
    public int bl_shift()
    {
        return _bl_shift;
    }

    // in the all list,
    private LayoutResult position_absolute(Layable layMember, int pos)
    {
//        AbsoluteBox newMember;
//        DimensionsDescriptor dd=new DimensionsDescriptor();
//        BorderDescriptor bd=new BorderDescriptor();
//        PositionDescriptor pd=new PositionDescriptor();
//        MarginDescriptor md=new MarginDescriptor();
//        
//        newMember=BoxTypes.toAbsoluteBox(layMember);
//        newMember.visual.workOutFlowDimensions(newMember, dd);
//        newMember.visual.calculateBorders(newMember, bd);
//        newMember.visual.workOutAbsolutePosition(newMember, pd);
//        newMember.visual.computeMarginProperties(newMember, md);
//        if(dd.width.specified == SpecialLength.SL_AUTO)
//            newMember._stretchy_cwidth=true;
//        else
//            newMember._stretchy_cwidth=false;
//        
//        newMember.set_relative_position(pd.left, pd.top, pd.right, pd.bottom);
//        newMember.calculateWidth(dd.width, md.left, md.right, pd.left, pd.right, dd.height);
//        newMember.calculateHeight(dd.height, md.top, md.bottom, pd.top, pd.bottom, dd.width);
//        newMember.set_borders(bd);
//        newMember.set_margins(md.left.value, md.top.value, md.right.value, md.bottom.value);
//        newMember.set_dimensions(dd.width.value + md.left.value + md.right.value + bd.borderLeftWidth + bd.borderRightWidth + bd.paddingLeftWidth + bd.paddingRightWidth,
//                dd.height.value + md.top.value + md.bottom.value + bd.borderTopWidth + bd.borderBottomWidth + bd.paddingTopWidth + bd.paddingBottomWidth);
//        newMember.set_position(contentLeft() + pd.left.value, contentTop() + pd.top.value);
//        
//        return new LayoutResult(true, Optional.<Relayouter>absent());
    	
    	throw new RuntimeException("not implemented");
    }

    public void calculate_static_position(int pos)
    {
        AtomicInline prev;
        Line last_line;
        int sx, sy;
        AbsoluteBox ab=BoxTypes.toAbsoluteBox(all.get(pos));
        if(flowspace().line_count(this) > 0)
        {
            last_line=flowspace().back_line(this);
            if(flowspace().atomic_count(last_line) > 0)
            {
                prev=flowspace().back_atomic(last_line);
                sx=prev.right() + 1;
                sy=prev.line().top();
            }
            else
            {
                sx=contentLeft();
                sy=contentTop();
            }
        }
        else
        {
            int start=all.indexOf(this);

            prev=find_previous_atomic(start);
            if(prev == null)
            {
                sx=contentLeft();
                sy=contentTop();
            }
            else
            {
                sx=prev.right() + 1;
                sy=prev.line().top();
            }
        }
        ab.set_static_position(sx, sy);
    }

    @Override
    public int contentLeft()
    {
        if(left() == Dimensionable.INVALID)
            return Dimensionable.INVALID;

        return left() + paddingLeft() + marginLeft() + borderLeft().width;
    }

    @Override
    public int contentRight()
    {
        if(right() == Dimensionable.INVALID)
            return Dimensionable.INVALID;

        return right() - paddingLeft() - marginRight() - borderRight().width;
    }

    @Override
    public int contentWidth()
    {
        if(width() == Dimensionable.INVALID)
            return Dimensionable.INVALID;

        return width() - paddingLeft() - paddingRight() - borderLeft().width - borderRight().width -
                marginLeft() - marginRight();
    }

    @Override
    public int paddingLeft()
    {
        return paddingLeftSize;
    }

    @Override
    public int paddingRight()
    {
        return paddingRightSize;
    }

    @Override
    public int paddingTop()
    {
        return paddingTopSize;
    }

    @Override
    public int paddingBottom()
    {
        return paddingBottomSize;
    }

    @Override
    public int marginLeft()
    {
        return marginLeftSize;
    }

    @Override
    public int marginTop()
    {
        return marginTopSize;
    }

    @Override
    public int marginRight()
    {
        return marginRightSize;
    }

    @Override
    public int marginBottom()
    {
        return marginBottomSize;
    }

    @Override
    public Border borderLeft()
    {
        return borders[BORDER_LEFT];
    }

    @Override
    public Border borderRight()
    {
        return borders[BORDER_RIGHT];
    }

    @Override
    public Border borderTop()
    {
        return borders[BORDER_TOP];
    }

    @Override
    public Border borderBottom()
    {
        return borders[BORDER_BOTTOM];
    }

    // these are called by calculate_position
    @Override
    public void set_margins(int left, int top, int right, int bottom)
    {
        marginLeftSize=left;
        marginTopSize=top;
        marginRightSize=right;
        marginBottomSize=bottom;
    }

// i think there should be one of these for every box type?
// commented out the calculatewidth/calculateheight for now
    @Override
    public void applyMinMaxConstraints(Length width, Length height,
            Length marginLeft, Length marginRight,
            Length minWidth, Length maxWidth,
            Length minHeight, Length maxHeight)
    {
        // these are for less typing
        Length miw=minWidth;
        Length maw=maxWidth;
        Length mih=minHeight;
        Length mah=maxHeight;

        /*	if(height==SL_AUTO && width==SL_AUTO && replace_object)
        {

        if(replace_object->width() > miw && replace_object->width() < maw &&
        replace_object->height() > mih && replace_object->height() < mah)
        {}
        else
        {
        if(replace_object->width() > maw && replace_object->height() > mah &&
        maw/replace_object->width() <= mah/replace_object->height())
        {
        width=maxWidth;
        height=max(minHeight.value, maxWidth.value * (height/width));
        }
        else if(replace_object->width() > maw && replace_object->height() > mah &&
        maw/replace_object->width() > mah/replace_object->height())
        {
        width=max(minWidth.value, maxHeight.value * (width/height));
        height=maxHeight;
        }
        else if(replace_object->width() < miw && replace_object->height() < mih &&
        miw/replace_object->width() <= mih/replace_object->height())
        {
        width=min(maxWidth.value, minHeight.value * (width/height));
        height=minHeight;
        }
        else if(replace_object->width() < miw && replace_object->height() < mih &&
        miw/replace_object->width() > mih/replace_object->height())
        {
        width=minWidth;
        height=min(maxHeight.value, minWidth.value * (height/width));
        }
        else if(replace_object->width() < miw && replace_object->height() > mah)
        {
        width=minWidth;
        height=maxHeight;
        }
        else if(replace_object->width() > maw && replace_object->height() < mih)
        {
        width=maxWidth;
        height=minHeight;
        }
        else if(replace_object->width() > maw)
        {
        width=maxWidth.value;
        height=max(maxWidth.value * (height/width), minHeight.value);
        }
        else if(replace_object->width() < miw)
        {
        width=minWidth.value;
        height=min(minWidth.value * (height/width), maxHeight.value);
        }
        else if(replace_object->height() > mah)
        {
        width=max(maxHeight.value * (width/height), minWidth.value);
        height=maxHeight;
        }
        else if(replace_object->height() < mih)
        {
        width=min(minHeight.value * (width/height), maxWidth.value);
        height=minHeight;
        }

        //			calculateWidth(width, marginLeft, marginRight);
        }
        }*/
//	else
//	{
        if(maw.specified != SpecialLength.SL_NULL)
        {
            if(width.value > maxWidth.value)
            {
                // 2. If the tentative used width is greater than 'max-width', the rules above
                // are applied again, but this time using the computed value of 'max-width' as
                // the computed value for 'width'.
                width=maw;
//				calculateWidth(width, marginLeft, marginRight);
            }
        }

        if(width.value < minWidth.value)
        {
            // 3. If the resulting width is smaller than 'min-width', the rules above are applied
            // again, but this time using the value of 'min-width' as the computed value for 'width'.
            width=minWidth;
//			calculateWidth(width, marginLeft, marginRight);
        }

        if(mah.specified != SpecialLength.SL_NULL)
        {
            if(height.value > maxHeight.value)
            {
                height=maxHeight;
            }
        }

        if(height.value < minHeight.value)
        {
            height=minHeight;
//			calculateHeight(height, marginTop, marginBottom);
        }
//	}
    }

    @Override
    public void table_insert(TableMember cont, TableMember memb)
    {
        throw new BoxError(BoxExceptionType.BET_WRONGTYPE);
    }

    public boolean marginsCollapseThrough()
    {
        return false;
    }

    @Override
    public Direction direction()
    {
        return Direction.DIR_LTR;
    }//return _direction;}

    @Override
    public Box root()
    {
        Box up=this;

        while(up.container() != null)
            up=up.container();

        return up;
    }

    @Override
    public FlowContext get_flow_context()
    {
        return container().get_flow_context();
    }

    @Override
    public Box get_flow_context_box()
    {
        return container().get_flow_context_box();
    }

    @Override
    public boolean stretchy_cwidth()
    {
        if(container() != null)
            return container().stretchy_cwidth();

        return false;
    }

    @Override
	public boolean stretchy_cheight()
    {
        if(container() != null)
            return container().stretchy_cheight();

        return false;
	}

	@Override
	public int can_stretch_vert(int amount)
	{
		return 0;
	}

	@Override
    public int can_stretch(int amount)
    {
        if(stretchy_cwidth() == false)
            throw new BoxError(BoxExceptionType.BET_WRONGTYPE);

        return container().can_stretch(amount);
    }

    @Override
    public boolean stiffy_height()
    {
        return !_auto_height;
    }

    @Override
    public int max_stiffy_height()
    {
        if(_auto_height == true)
            throw new BoxError(BoxExceptionType.BET_WRONGTYPE);

        return contentHeight();
    }

    @Override
    public boolean isAutoWidth()
    {
        return _auto_width;
    }

    @Override
    public boolean isAutoHeight()
    {
        return _auto_height;
    }

    @Override
	public Box getContainer()
    {
		return _container;
	}

	@Override
    public int left()
    {
        return dimensions.left();
    }

    @Override
    public int top()
    {
        return dimensions.top();
    }

    @Override
    public int right()
    {
        return dimensions.right();
    }

    @Override
    public int bottom()
    {
        return dimensions.bottom();
    }

    @Override
    public int width()
    {
        return dimensions.width();
    }

    @Override
    public int height()
    {
        return dimensions.height();
    }

    @Override
    public void change_height(int difference)
    {
        dimensions.change_height(difference);
    }

    @Override
    public void change_width(int difference)
    {
        dimensions.change_width(difference);
    }

    @Override
    public int getRelTop()
    {
        return relTop;
    }

    @Override
    public int getRelLeft()
    {
        return relLeft;
    }

    @Override
    public boolean is_relative()
    {
        return relative;
    }

    @Override
    public void set_relative(boolean to)
    {
        relative=to;
    }

    @Override
    public TableBox table_root()
    {
        Layable layable=this;
        boolean found=false;

        while(found == false && layable != null)
        {
            if(BoxTypes.isBox(layable))
            {
                found=BoxTypes.isTableBox(layable);
                if(found)
                    return BoxTypes.toTableBox(layable);
            }

            layable=layable.container();
        }

        return null;
    }

    @Override
    public Box container()
    {
        return _container;
    }

    @Override
    public void set_position(int x, int y)
    {
        dimensions.set_position(x, y);
        if(replace_object() != null)
            replace_object().set_position(x, y);
    }
    
    @Override
	public void unsetPosition()
	{
    	dimensions.set_position(Dimensionable.INVALID, Dimensionable.INVALID);
        if(replace_object() != null)
            replace_object().set_position(Dimensionable.INVALID, Dimensionable.INVALID);
	}    

    @Override
    public List<Layable> getMembersAll()
    {
		return Collections.unmodifiableList(all);
	}

    @Override
    public List<Box> getMembersFlowing()
    {
		return null;
	}

    public List<Inline> getMembersInline()
    {
		return Collections.unmodifiableList(inlines);
	}

    @Override
    public List<FloatBox> getMembersFloating()
    {
		return Collections.unmodifiableList(float_list);
	}

    @Override
    public List<AbsoluteBox> getMembersPositioned()
    {
		return null;
	}

    @Override
	public Visual getVisual()
	{
		return visual;
	}

    @Override
	public boolean flows()
	{
		return true;
	}

    @Override
	public int getId()
	{
		return id;
	}

    @Override
    public void setId(int id)
    {
    	this.id=id;
    }    
    
	public int dummy_atomic_baseline(Line on, VerticalAlignment vert)
    {
        Text t=new Text("X", 0, SpaceFlag.SF_NOT_SPACE, _bl_shift);
        int baseline;
        SizeResult size;
        int bottom=0;

        t.set_container(this);
        size=t.compute_dimensions();
        t.set_dimensions(size.width.value, size.height.value);
        if(vert.specified == VerticalAlignmentSpecial.VAS_TOP)
            bottom=on.top() + t.height() - 1;
        else if(vert.specified == VerticalAlignmentSpecial.VAS_BOTTOM)
            bottom=on.bottom();
        baseline=baseline_from_bottom_for_bl(t, bottom);
        return baseline;
    }

    public int top_from_baseline(AtomicInline ainl, int baseline)
    {
        int gap=ainl.height() - ainl.content_height();
        int topgap=gap / 2;
        int bottomgap=gap - topgap;
        int bottom, top;

		logger.debug("top_from_baseline "+baseline+" "+visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent()+" "+bottomgap);
        bottom=baseline +visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent() + bottomgap;
        if(_bl_shift > 0)
            bottom+=Math.abs(_bl_shift);
        top=bottom - ainl.height() + 1;

		logger.debug("top_from_baseline "+top);

        return top;
    }

    public int baseline_from_middle(AtomicInline ainl, int middle)
    {
        int gap=ainl.height() - ainl.content_height();
        int topgap=gap / 2;
        int bottomgap=gap - topgap;
        int bottom, baseline;

		logger.debug("baseline_from_middle "+middle+" "+getFont()+" "+bottomgap);
        bottom=middle+(visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getHeight()/2)-1;
        if(_bl_shift > 0)
            bottom+=Math.abs(_bl_shift);

		baseline=baseline_from_bottom(ainl, bottom);

		logger.debug("baseline_from_middle "+baseline);

        return baseline;
    }


    public int bottom_from_baseline(AtomicInline ainl, int baseline)
    {
        int gap=ainl.height() - ainl.content_height();
        int topgap=gap / 2;
        int bottomgap=gap - topgap;
        int bottom;
        bottom=baseline + visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent() + bottomgap;
        if(_bl_shift > 0)
            bottom+=Math.abs(_bl_shift);
        return bottom;
    }



    public int baseline_from_bottom(AtomicInline ainl, int bottom)
    {
        int gap=ainl.height() - ainl.content_height();
        int topgap=gap / 2;
        int bottomgap=gap - topgap;
        int baseline;

		logger.debug("baseline_from_bottom "+bottom+" "+visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent()+" "+bottomgap);

        baseline=bottom - visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent() - bottomgap;
        if(_bl_shift < 0)
            baseline-=Math.abs(_bl_shift);

		logger.debug("baseline_from_bottom "+baseline);

        return baseline;
    }

	// should only be used for a new line with no atomics on it yet
    public int baseline_from_bottom_for_bl(AtomicInline ainl, int bottom)
    {
		logger.debug("baseline_from_bottom_for_bl "+bottom);

        int gap=ainl.height() - ainl.content_height();
        int topgap=gap / 2;
        int bottomgap=gap - topgap;
        int baseline;

		logger.debug("ainl.height() "+ainl.height()+" ainl.content_height() "+ainl.content_height());


		logger.debug("baseline_from_bottom_for_bl "+bottom+" "+visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent()+" "+Math.abs(bottomgap));

        baseline=bottom - visual.getGraphicsContext().fontMetrics(ainl.container().getFont()).getDescent() - Math.abs(bottomgap);
        if(_bl_shift < 0)
            baseline-=Math.abs(_bl_shift);

		logger.debug("baseline: "+baseline);

        return baseline;
    }

	@Override
	public void setFutureWidth(int futureWidth)
	{
		this.futureWidth=Optional.of(futureWidth);
	}
	
	@Override
	public void setFutureHeight(int futureHeight)
	{
		this.futureHeight=Optional.of(futureHeight);
	}

    @Override
    public void setFont(Font font)
    {
        this.font=font;
    }	
	
	@Override
	public Font getFont()
	{
		return font;
	}
	
	@Override
	public void setRelLeft(int relLeft)
	{
		this.relLeft=relLeft;
	}

	@Override
	public void setRelTop(int relTop)
	{
		this.relTop=relTop;
	}

	@Override
	public void setForegroundColour(Color foregroundColour)
	{
		this.foregroundColour=foregroundColour;
	}
	
	public Color getForegroundColour()
	{
		return foregroundColour;
	}

	@Override
	public void setLineHeight(int lineHeight)
	{
		this.lineHeight=lineHeight;
	}

	public int getLineHeight()
	{
		return lineHeight;
	}

	@Override
	public void setVerticalAlign(VerticalAlignment verticalAlign)
	{
		this.verticalAlign=verticalAlign;
	}
	
	public VerticalAlignment getVerticalAlign()
	{
		return verticalAlign;
	}

	@Override
	public void setWordSpacing(int wordSpacing)
	{
		this.wordSpacing=wordSpacing;
	}

	@Override
	public void setLetterSpacing(int letterSpacing)
	{
		this.letterSpacing=letterSpacing;
	}

	@Override
	public void setBorders(Border[] borders)
	{
		this.borders=borders;
	}

	@Override
	public void setPaddings(int paddingTopWidth, int paddingBottomWidth, int paddingLeftWidth, int paddingRightWidth)
	{
		this.paddingTopSize=paddingTopWidth;
		this.paddingBottomSize=paddingBottomWidth;
		this.paddingLeftSize=paddingLeftWidth;
		this.paddingRightSize=paddingRightWidth;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			       .add("id",id)
			       .toString();
	}
}
