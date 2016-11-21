package com.github.s262316.forx.box;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Line extends Dimensionable
{
	private final static Logger logger=LoggerFactory.getLogger(Line.class);

    private Map<InlineBox, Integer> baselines=new HashMap<InlineBox, Integer>();
    private int id=BoxCounter.next();

    public Line(int left, int top, int width, int height)
    {
        super(left, top, width, height);

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
}
