package tnk47collection.work2;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;

public class MakeJSON implements Runnable {

	public static final String INPUT_DIR = "data2/step3";
	public static final String OUTPUT = "data2/step4/card.json";

	public static void main(String[] args) {
		new MakeJSON().run();
	}

	@Override
	public void run() {
		System.out.printf("%s start\n", this.getClass().getSimpleName());

		Set<String> mergeLines = new HashSet<>();

		try {
			Collection<File> inputFiles = FileUtils.listFiles(new File(
					INPUT_DIR), FileFileFilter.FILE, null);
			for (File input : inputFiles) {
				List<String> inputLines = FileUtils.readLines(input);
				mergeLines.addAll(inputLines);
			}

			Map<String, JSONObject> mapping = new HashMap<>();
			for (String line : mergeLines) {
				String[] prop = StringUtils.splitPreserveAllTokens(line, ",");
				String ill = prop[0];
				String pref = prop[1];
				String rarilites = prop[2];
				String name = prop[3];
				rarilites = StringUtils.replace(rarilites, "ssrare", "SSR");
				rarilites = StringUtils.replace(rarilites, "srare", "SR");
				rarilites = StringUtils.replace(rarilites, "hrare", "HR");

				JSONObject card = null;
				if (mapping.containsKey(name)) {
					card = mapping.get(name);
					JSONArray illList = card.getJSONArray("ill");
					illList.add(ill);
				} else {
					card = new JSONObject();
					card.put("name", name);
					card.put("rarilites", rarilites);
					card.put("pref", StringUtils.defaultIfBlank(pref, "不明"));

					JSONArray illList = new JSONArray();
					illList.add(ill);
					card.put("ill", illList);

					mapping.put(name, card);
				}
			}

			JSONArray outputList = new JSONArray();
			outputList.addAll(mapping.values());

			FileUtils.write(new File(OUTPUT),
					"var cards = " + outputList.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.printf("%s end\n", this.getClass().getSimpleName());
	}
}
