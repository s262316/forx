package com.github.s262316.forx.core.diagn;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class MyNode
{
	private int id;
	private String text;
	private String thisClass;
	private int left, width, top, height;
	private Collection<MyNode> members;
	private String tag;
	
	private Map<String, Object> myAtts;
	
	public Collection<MyNode> getMembers() {
		return members;
	}
	
	public void setMembers(Collection<MyNode> members) {
		this.members = members;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getThisClass() {
		return thisClass;
	}

	public void setThisClass(String thisClass) {
		this.thisClass = thisClass;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Map<String, Object> getMyAtts() {
		return myAtts;
	}

	public void setMyAtts(Map<String, Object> myAtts) {
		this.myAtts = myAtts;
	}

	
}


