package com.github.s262316.forx.tree.visual;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.FileReader;

import javax.xml.bind.JAXB;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.tree.visual.mockbox.MockBlockBox;
import com.github.s262316.forx.tree.visual.mockbox.MockInlineBox;

/**
<root>
	<div>
		<p>
		    <b>
		        <-- insert newbox here
		    </b>
		</p>
	</div>
</root>

transforms to this:

<root>
	<div>
		<p>
		    <dummydiv>
		        <b>
		        </b>
		            <generateddiv> <newbox/>  </generateddiv>
		        <b>
		        </b>
		    </dummydiv>
		</p>
	</div>
</root>
 */
@ExtendWith(MockitoExtension.class)
public class InlineBoxParentLocatorTest
{
	@Nested
	class Simple
	{
		@Mock
		Visual rootVis, divVis, pVis, bVis, dummyContainerVis;
		@Mock
		Visual bPostSplitVis;
	
		MockBlockBox root;
		MockBlockBox div;
		MockBlockBox p;
		MockInlineBox b;
		MockBlockBox dummyContainer=new MockBlockBox();
		MockBlockBox newChild=new MockBlockBox();
		MockInlineBox bPostSplit=new MockInlineBox();
		
		@BeforeEach
		public void setup() throws Exception
		{
			RootNode r = JAXB.unmarshal(new FileReader("C:\\Users\\anmar\\git\\forx\\forx-core\\src\\test\\java\\com\\github\\s262316\\forx\\tree\\visual\\a.xml"), RootNode.class);
			
			root=SemiMockedBoxTree.f(r);
			div=(MockBlockBox)root.select(new int[]{0});
			p=(MockBlockBox)root.select(new int[]{0, 0});
			b=(MockInlineBox)root.select(new int[]{0, 0, 0});
	
			root.setVisual(rootVis);		
			div.setVisual(divVis);		
			p.setVisual(pVis);		
			b.setVisual(bVis);
			dummyContainer.setVisual(dummyContainerVis);
			bPostSplit.setVisual(bPostSplitVis);
	
			when(pVis.createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER)).thenReturn(dummyContainer);
			when(dummyContainerVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(bPostSplit);
		}
	
		@Test
		public void parentIsNewDummyContainer()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			assertThat(dummyContainer).isEqualTo(locator.locate(newChild));
		}
		
		@Test
		public void structureMatches()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			locator.locate(newChild);
					
			assertThat(root.getMembersAll())
				.containsExactly(div).inOrder();
			
			assertThat(div.getMembersAll())
				.containsExactly(p).inOrder();
	
			assertThat(p.getMembersAll())
				.containsExactly(dummyContainer).inOrder();
			
			assertThat(dummyContainer.getMembersAll())
				.containsExactly(b, bPostSplit).inOrder();
			
			assertThat(root.getContainer()).isNull();
			assertThat(div.getContainer()).isEqualTo(root);
			assertThat(p.getContainer()).isEqualTo(div);
			assertThat(dummyContainer.getContainer()).isEqualTo(p);
			assertThat(b.getContainer()).isEqualTo(dummyContainer);
			assertThat(bPostSplit.getContainer()).isEqualTo(dummyContainer);
		}
		
		@Test
		public void isPostSplitPopulated()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			locator.locate(newChild);
			verify(bVis).setPostSplit(bPostSplit);
		}
	}
	
	/**
<root>
	<div>
		<p>
			<span>
				<span>
					<span>
						<b>
							<----- insert block here
						</b>
					</span>
				</span>
			</span>
		</p>
	</div>
</root>

transforms to this:

<root>
	<div>
		<p>
		    <dummydiv>
				<span>
					<span>
						<span>
							<b>
							</b>
						</span>
					</span>
				</span>
				<------- new content goes here
				<span>
					<span>
						<span>
							<b>
							</b>
						</span>
					</span>
				</span>
			</dummydiv>
		</p>
	</div>
</root>
	 */
	
	@Nested
	class NestedInlines
	{
		@Mock
		Visual rootVis, divVis, pVis, bVis, dummyContainerVis, span1Vis, span2Vis, span3Vis;
		@Mock
		Visual bPostSplitVis, span1PostSplitVis, span2PostSplitVis, span3PostSplitVis;
	
		MockBlockBox root;
		MockBlockBox div;
		MockBlockBox p;
		MockInlineBox span1;
		MockInlineBox span2;
		MockInlineBox span3;
		MockInlineBox b;
		MockBlockBox dummyContainer=new MockBlockBox();
		MockInlineBox span1PostSplit=new MockInlineBox();
		MockInlineBox span2PostSplit=new MockInlineBox();
		MockInlineBox span3PostSplit=new MockInlineBox();
		MockInlineBox bPostSplit=new MockInlineBox();
		MockBlockBox newChild=new MockBlockBox();
		
		@BeforeEach
		public void setup() throws Exception
		{
			String s=IOUtils.toString(new FileReader("C:\\Users\\anmar\\git\\forx\\forx-core\\src\\test\\java\\com\\github\\s262316\\forx\\tree\\visual\\b.xml"));
			
			RootNode r = JAXB.unmarshal(new FileReader("C:\\Users\\anmar\\git\\forx\\forx-core\\src\\test\\java\\com\\github\\s262316\\forx\\tree\\visual\\b.xml"), RootNode.class);
			
			root=SemiMockedBoxTree.f(r);
			div=(MockBlockBox)root.select(new int[]{0});
			p=(MockBlockBox)root.select(new int[]{0, 0});
			span1=(MockInlineBox)root.select(new int[]{0, 0, 0});
			span2=(MockInlineBox)root.select(new int[]{0, 0, 0, 0});
			span3=(MockInlineBox)root.select(new int[]{0, 0, 0, 0, 0});
			b=(MockInlineBox)root.select(new int[]{0, 0, 0, 0, 0, 0});
	
			root.setVisual(rootVis);		
			div.setVisual(divVis);		
			p.setVisual(pVis);
			span1.setVisual(span1Vis);
			span2.setVisual(span2Vis);
			span3.setVisual(span3Vis);
			b.setVisual(bVis);
			dummyContainer.setVisual(dummyContainerVis);
			bPostSplit.setVisual(bPostSplitVis);
			span1PostSplit.setVisual(span1PostSplitVis);
			span2PostSplit.setVisual(span2PostSplitVis);
			span3PostSplit.setVisual(span3PostSplitVis);			
	
			when(pVis.createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER)).thenReturn(dummyContainer);
			when(dummyContainerVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(span1PostSplit);
			when(span1PostSplitVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(span2PostSplit);
			when(span2PostSplitVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(span3PostSplit);
			when(span3PostSplitVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(bPostSplit);
		}
	
		@Test
		public void parentIsNewDummyContainer()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			assertThat(dummyContainer).isEqualTo(locator.locate(newChild));
		}
		
		@Test
		public void structureMatches()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			locator.locate(newChild);
					
			assertThat(root.getMembersAll())
				.containsExactly(div).inOrder();
			
			assertThat(div.getMembersAll())
				.containsExactly(p).inOrder();
	
			assertThat(p.getMembersAll())
				.containsExactly(dummyContainer).inOrder();
			
			assertThat(dummyContainer.getMembersAll())
				.containsExactly(span1, span1PostSplit).inOrder();

			assertThat(span1.getMembersAll())
				.containsExactly(span2).inOrder();

			assertThat(span2.getMembersAll())
				.containsExactly(span3).inOrder();

			assertThat(span3.getMembersAll())
				.containsExactly(b).inOrder();

			assertThat(span1PostSplit.getMembersAll())
				.containsExactly(span2PostSplit).inOrder();
	
			assertThat(span2PostSplit.getMembersAll())
				.containsExactly(span3PostSplit).inOrder();
	
			assertThat(span3PostSplit.getMembersAll())
				.containsExactly(bPostSplit).inOrder();
			
			assertThat(root.getContainer()).isNull();
			assertThat(div.getContainer()).isEqualTo(root);
			assertThat(p.getContainer()).isEqualTo(div);
			assertThat(dummyContainer.getContainer()).isEqualTo(p);

			assertThat(span1.getContainer()).isEqualTo(dummyContainer);
			assertThat(span2.getContainer()).isEqualTo(span1);
			assertThat(span3.getContainer()).isEqualTo(span2);
			assertThat(b.getContainer()).isEqualTo(span3);
			
			assertThat(span1PostSplit.getContainer()).isEqualTo(dummyContainer);
			assertThat(span2PostSplit.getContainer()).isEqualTo(span1PostSplit);
			assertThat(span3PostSplit.getContainer()).isEqualTo(span2PostSplit);
			assertThat(bPostSplit.getContainer()).isEqualTo(span3PostSplit);
		}
		
		@Test
		public void isPostSplitPopulated()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			locator.locate(newChild);

			verify(span1Vis).setPostSplit(span1PostSplit);
			verify(span2Vis).setPostSplit(span2PostSplit);
			verify(span3Vis).setPostSplit(span3PostSplit);
			verify(bVis).setPostSplit(bPostSplit);
		}
	}
	
	/**
	<root>
		<div>
			<inlinecontainer>
				<i></i>
				<u></u>
			    <b>
			        <-- insert newbox here
			    </b>
			</inlinecontainer>
		</div>
	</root>

	transforms to this:
	<root>
		<div>
			<p>
				<dummydiv>
					<i></i>
					<u></u>
				    <b>
				    </b>
				    <------- new content goes here
				    <b>
				    </b>
				</dummydiv>
			</p>
		</div>
	</root>
	 */	
	@Nested
	class PreviousSiblings
	{
		@Mock
		Visual rootVis, divVis, pVis, bVis, iVis, uVis, dummyContainerVis;
		@Mock
		Visual bPostSplitVis;
	
		MockBlockBox root;
		MockBlockBox div;
		MockBlockBox p;
		MockInlineBox b;
		MockInlineBox i;
		MockInlineBox u;
		MockBlockBox dummyContainer=new MockBlockBox();
		MockBlockBox newChild=new MockBlockBox();
		MockInlineBox bPostSplit=new MockInlineBox();
		
		@BeforeEach
		public void setup() throws Exception
		{
			RootNode r = JAXB.unmarshal(new FileReader("C:\\Users\\anmar\\git\\forx\\forx-core\\src\\test\\java\\com\\github\\s262316\\forx\\tree\\visual\\c.xml"), RootNode.class);
			
			root=SemiMockedBoxTree.f(r);
			div=(MockBlockBox)root.select(new int[]{0});
			p=(MockBlockBox)root.select(new int[]{0, 0});
			i=(MockInlineBox)root.select(new int[]{0, 0, 0});
			u=(MockInlineBox)root.select(new int[]{0, 0, 1});
			b=(MockInlineBox)root.select(new int[]{0, 0, 2});
	
			root.setVisual(rootVis);		
			div.setVisual(divVis);		
			p.setVisual(pVis);		
			b.setVisual(bVis);
			i.setVisual(iVis);
			u.setVisual(uVis);
			dummyContainer.setVisual(dummyContainerVis);
			bPostSplit.setVisual(bPostSplitVis);
	
			when(pVis.createAnonBlockBox(AnonReason.BLOCK_INSIDE_INLINE_SPLIT_CONTAINER)).thenReturn(dummyContainer);
			when(dummyContainerVis.createAnonInlineBox(AnonReason.BLOCK_INSIDE_INLINE_POST_SPLIT_STRUCTURE)).thenReturn(bPostSplit);
		}
	
		@Test
		public void parentIsNewDummyContainer()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			assertThat(dummyContainer).isEqualTo(locator.locate(newChild));
		}
		
		/**
	<root>
		<div>
			<p>
				<dummydiv>
					<i></i>
					<u></u>
				    <b>
				    </b>
				    <------- new content goes here
				    <b>
				    </b>
				</dummydiv>
			</p>
		</div>
	</root>
		 */
		
		@Test
		public void structureMatches()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			locator.locate(newChild);
					
			assertThat(root.getMembersAll())
				.containsExactly(div).inOrder();
			
			assertThat(div.getMembersAll())
				.containsExactly(p).inOrder();
	
			assertThat(p.getMembersAll())
				.containsExactly(dummyContainer).inOrder();
			
			assertThat(dummyContainer.getMembersAll())
				.containsExactly(i, u, b, bPostSplit).inOrder();
			
			assertThat(root.getContainer()).isNull();
			assertThat(div.getContainer()).isEqualTo(root);
			assertThat(p.getContainer()).isEqualTo(div);
			assertThat(dummyContainer.getContainer()).isEqualTo(p);
			assertThat(i.getContainer()).isEqualTo(dummyContainer);
			assertThat(u.getContainer()).isEqualTo(dummyContainer);
			assertThat(b.getContainer()).isEqualTo(dummyContainer);
			assertThat(bPostSplit.getContainer()).isEqualTo(dummyContainer);
		}
		
		@Test
		public void isPostSplitPopulated()
		{
			InlineBoxParentLocator locator=new InlineBoxParentLocator(b, bVis);
			locator.locate(newChild);
			verify(bVis).setPostSplit(bPostSplit);
		}
	}
}

