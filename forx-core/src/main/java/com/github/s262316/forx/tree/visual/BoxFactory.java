package com.github.s262316.forx.tree.visual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.AbsoluteBox;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.ColGroup;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBlockBox;
import com.github.s262316.forx.box.InlineBlockRootBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.ReplaceableBoxPlugin;
import com.github.s262316.forx.box.ReplacedInline;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.RowGroup;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableMember;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.box.properties.Visual;


public class BoxFactory
{
	private final static Logger logger=LoggerFactory.getLogger(BoxFactory.class);

	private BoxFactory()
	{}

	public static Box createFloatBox(Visual element, ReplaceableBoxPlugin p)
	{
		FloatBox box=new FloatBox(element, p);

//		System.out.println("newfloatbox " + box.id);

		return box;
	}

	public static Box createRelativeBox(Visual element)
	{
	//	VisualProxy *vp=new VisualProxy<*>(attached);
		return null;
	}

	public static Box createAbsoluteBox(Visual element)
	{
		Box box=new AbsoluteBox(element);

//		System.out.println("newabsbox " + box.id);

		return box;
	}

	public static Box createBlockFlowBox(Visual element, ReplaceableBoxPlugin plugin)
	{
	//	ContentDrawer *cd=new ContentDrawer;
	//	BorderDrawer *bd=new BorderDrawer(cd);
	  //  FillDrawer *fd=new FillDrawer(bd);

		BlockBox box=new BlockBox(element, null, plugin);

//		System.out.println("newblock " + box.id);


		return box;
	}

	public static Box createInlineFlowBox(Visual element)
	{
		InlineBox box=new InlineBox(element, null, null, null);

//		System.out.println("newinline " + box.id);


		return box;
	}

	public static Inline createReplacedInlineFlowBox(Visual element, ReplaceableBoxPlugin p)
	{
		ReplacedInline ri=new ReplacedInline(element, p);

		return ri;
	}

	public static InlineBox createAnonymousInlineFlowBox(AnonVisual anon)
	{
		InlineBox box;

		box=new InlineBox(anon, null, null, null);

//		System.out.println("newanoninlinebox " + box.id);


		return box;
	}

	public static BlockBox createAnonymousBlockFlowBox(AnonVisual anon)
	{
		BlockBox box;

		box=new BlockBox(anon, null, null);

//		System.out.println("newanonblockbox " + box.id);


		return box;
	}

	public static InlineBlockRootBox createAnonInlineBlockRootBox(AnonVisual anon)
	{
		InlineBlockRootBox ibrb;

		ibrb=new InlineBlockRootBox(anon);

		return ibrb;
	}

	public static Box createInlineBlockFlowBox(Visual element)
	{
		InlineBlockBox ibb=new InlineBlockBox(element, null);

		return ibb;
	}

	public static Box createFixedBox(Visual element)
	{
	//	VisualProxy *vp=new VisualProxy(attached);
	//	return new FixedBox;
		return null;
	}

	public static Box createRootBox(Visual element)
	{
	//	ContentDrawer *cd=new ContentDrawer;
	//	BorderDrawer *bd=new BorderDrawer(cd);
	//	FillDrawer *fd=new FillDrawer(bd);

		RootBox box=new RootBox(element, null);
		box.set_container(null);
		box.computeProperties();

		box.selfCalculatePosition();

		logger.debug("createRootBox");

		return box;
	}

	public static Box createTableBox(Visual element)
	{
		TableBox tb=new TableBox(element, null, false);

		return tb;
	}

	public static CellBox createTableCell(Visual element, int col_span, int row_span)
	{
		CellBox tc=new CellBox(element, null, col_span, row_span, false);

		return tc;
	}

	public static TableMember createTableRow(Visual element)
	{
		TableRow tr=new TableRow(element, null, false);

		return tr;
	}

	public static TableMember createTableRowGroup(Visual element)
	{
		RowGroup trg=new RowGroup(element, null, false);

		return trg;
	}

	public static TableMember createTableColumn(Visual element)
	{
		Column c=new Column(element, null, false);

		return c;
	}

	public static TableMember createTableColGroup(Visual element)
	{
		ColGroup cg=new ColGroup(element, null, false);

		return cg;
	}

	public static TableRow createAnonRowBox(AnonVisual element)
	{
		TableRow tr=new TableRow(element, null, true);

		return tr;
	}

	public static Column createAnonColBox(Visual element)
	{
		Column tr=new Column(element, null, true);

		return tr;
	}

	public static TableBox createAnonTableBox(Visual element)
	{
		TableBox tb=new TableBox(element, null, true);

		return tb;
	}

	public static CellBox createAnonCellBox(Visual element)
	{
		CellBox cb=new CellBox(element, null, 1, 1, true);

		return cb;
	}
}


