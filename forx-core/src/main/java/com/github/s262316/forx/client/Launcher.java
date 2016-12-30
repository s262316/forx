package com.github.s262316.forx.client;

import javax.swing.JFrame;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.gui.WebView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Launcher implements CommandLineRunner
{
	final static Logger logger=LoggerFactory.getLogger(Launcher.class);

	@Autowired
	WebView webView;

	@Override
	public void run(String... args) throws Exception
	{
		JFrame frame=new JFrame(FilenameUtils.getName(args[0]));
		frame.getContentPane().add(webView);
		frame.setSize(400, 400);
		frame.setVisible(true);
		webView.load(args[0]);
    }
}
