package tnk47collection.work;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.HTMLReader.TagAction;

import org.apache.commons.io.FileUtils;

import tnk47collection.common.CommonHttpClient;
import tnk47collection.common.SystemConstants;

public class MakeCSV implements Runnable {

	public static final Pattern PATTERN_IMG = Pattern
			.compile("(ill_\\d+_[a-z_\\-]+\\d{2})");

	private int number;

	public MakeCSV(int number) {
		this.number = number;
	}

	@Override
	public void run() {
		System.out.printf("%s(%d) start\n", this.getClass().getSimpleName(),
				number);

		String input = String.format("data/step2/%d.txt", number);
		String output = String.format("data/step3/%d.csv", number);

		try {
			List<String> inputLines = FileUtils.readLines(new File(input));
			List<String> outputLines = new ArrayList<>();
			StringWriter stringWriter = null;
			for (int i = 0; i < inputLines.size(); i++) {
				String line = inputLines.get(i);
				switch (i % 8) {
				case 0:
					if (stringWriter != null) {
						outputLines.add(stringWriter.toString());
					}
					stringWriter = new StringWriter();
					stringWriter.append(line);
					break;
				default:
					stringWriter.append("," + line);
					break;
				}
			}

			FileUtils.writeLines(new File(output), outputLines);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.printf("%s(%d) end\n", this.getClass().getSimpleName(),
				number);
	}

}
