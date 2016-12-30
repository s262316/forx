/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.build;

import com.github.s262316.forx.tree.impl.XmlAttribute;
import com.github.s262316.forx.tree.impl.XmlComment;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.impl.XmlText;

public interface XmlDocumentBuilder
{
    public XmlElement createElement(ElementKey key);
    public XmlAttribute createAttr(AttributeKey key);
    public XmlComment createComment(CommentKey key);
    public XmlText createText(TextKey key);
    

    public void setDoc(XmlDocument doc);
}
