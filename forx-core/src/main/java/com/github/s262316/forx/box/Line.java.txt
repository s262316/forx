package com.github.s262316.forx.box;

import java.util.HashMap;
import java.util.Map;

import com.github.s262316.forx.box.util.TextAlign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Line extends Dimensionable
{
	private final static Logger logger=LoggerFactory.getLogger(Line.class);

    private Map<InlineBox, Integer> baselines=new HashMap<InlineBox, Integer>();
    private int id=BoxCounter.next();
    private Flowspace flowspace;
    private TextAlign horizAlignment;

    public Line(int left, int top, int width, int height, Flowspace flowspace, TextAlign horizAlignment)
    {
        super(left, top, width, height);
        this.flowspace=flowspace;
        this.horizAlignment=horizAlignment;
    }

    public int baseline(InlineBox aligned_subtree_root)
    {
        int bl;

		if(!baselines.containsKey(aligned_subtree_root))
		{
			set_baseline(aligned_subtree_root, Dimensionable.INVALID);
		}

		bl=baselines.get(aligned_subtree_root);

        return bl;
    }

    public void set_baseline(InlineBox aligned_subtree_root, int bl)
    {
		logger.debug("set_baseline : ("+aligned_subtree_root.getId()+") "+bl);

        baselines.put(aligned_subtree_root, bl);
    }

    public int getId()
    {
		return id;
	}

	public int alignmentAdjustment(AtomicInline atomic)
	{
		int right=flowspace.back_atomic(this).right();

		switch(horizAlignment)
		{
			case TA_LEFT:
				return 0;
			case TA_RIGHT:
				return width()-right;
			case TA_CENTER:
				return (width()-right) / 2;
			case TA_JUSTIFY:
				return 0;
		}

		return 0;
	}
}
