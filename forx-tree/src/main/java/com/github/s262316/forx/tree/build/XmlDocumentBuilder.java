/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.tree.build;

import com.github.s262316.forx.tree.XmlAttribute;
import com.github.s262316.forx.tree.XmlComment;
import com.github.s262316.forx.tree.XmlDocument;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.tree.XmlText;

import java.net.URL;

public interface XmlDocumentBuilder
{
    public XmlElement createElement(ElementKey key);
    public XmlAttribute createAttr(AttributeKey key);
    public XmlComment createComment(CommentKey key);
    public XmlText createText(TextKey key);
    public XmlDocument createDocument(URL location);
}
