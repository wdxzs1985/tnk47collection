package tnk47collection.work;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.HTMLReader.TagAction;

import org.apache.commons.io.FileUtils;

import tnk47collection.common.CommonHttpClient;
import tnk47collection.common.SystemConstants;

public class MakeRawText implements Runnable {

	public static final Pattern TAG_TD = Pattern.compile("<td>(.*)</td>");
	public static final Pattern TAG_IMG = Pattern.compile("<img .* src=\"(.*)\" .* />");
	public static final Pattern TAG_IMG2 = Pattern.compile("(ill_\\d+_[a-z_\\-]+\\d{2})");
	public static final Pattern TAG_A = Pattern.compile("<a .*>(.*)</a>");
	
	private int number;

	public MakeRawText(int number) {
		this.number = number;
	}

	@Override
	public void run() {
		System.out.printf("%s(%d) start\n",this.getClass().getSimpleName(), number);
		
		String input = String.format("data/step1/%d.html", number);
		String output = String.format("data/step2/%d.txt", number);

		try {
			List<String> inputLines = FileUtils.readLines(new File(input));
			List<String> outputLines = new ArrayList<>();
			for(String line:inputLines){
				Matcher matcher = TAG_TD.matcher(line);
				if(matcher.find()){
					String found = matcher.group(1);
					Matcher matcher2 = TAG_IMG.matcher(found);
					if(matcher2.find()){
						found = matcher2.group(1);
						Matcher matcher3 = TAG_IMG2.matcher(found);
						if(matcher3.find()){
							found = matcher3.group(1);
						}
					}else {
						Matcher matcher3 = TAG_A.matcher(found);
						if(matcher3.find()){
							found = matcher3.group(1);
						}
					}
					outputLines.add(found);
				}
			}
			FileUtils.writeLines(new  File(output), outputLines);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("%s(%d) end\n",this.getClass().getSimpleName(), number);
	}

}
