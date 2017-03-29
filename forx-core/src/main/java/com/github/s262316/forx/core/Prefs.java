package com.github.s262316.forx.core;

public class Prefs
{
	private static Prefs instance;
	private String _language;
	private String _serif;
	private String _sans_serif;
	private String _cursive;
	private String _fantasy;
	private String _monospace;
	private String _default_font;

	private Prefs()
	{
		_language="en-gb";
		_serif="Times New Roman";
		_sans_serif="Helvetica";
		_cursive="Zapf-Chancery";
		_fantasy="Western";
		_monospace="Courier";
		_default_font="Times New Roman";
	}

	public static Prefs getPrefs()
	{
		if(instance==null)
			instance=new Prefs();
		return instance;
	}

	public String language()
	{
		return _language;
	}

	public String serif()
	{
		return _serif;
	}

	public String sans_serif()
	{
		return _sans_serif;
	}

	public String cursive()
	{
		return _cursive;
	}

	public String fantasy()
	{
		return _fantasy;
	}

	public String monospace()
	{
		return _monospace;
	}

	public String default_font()
	{
		return _default_font;
	}
}


