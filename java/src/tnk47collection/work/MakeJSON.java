package tnk47collection.work;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;

public class MakeJSON implements Runnable {

	public static final String INPUT_DIR = "data/step3";
	public static final String OUTPUT = "data/step4/card.json";

	@Override
	public void run() {
		System.out.printf("%s start\n", this.getClass().getSimpleName());

		List<String> mergeLines = new ArrayList<>();

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
				String name = prop[1];
				String kana = prop[2];
				String region = prop[3];
				String pref = prop[4];
				String type = prop[5];

				String rarilites = prop[7];
				rarilites = StringUtils.replace(rarilites, "SSレア", "SSR");
				rarilites = StringUtils.replace(rarilites, "Sレア", "SR");
				rarilites = StringUtils.replace(rarilites, "ハイレア", "HR");
				rarilites = StringUtils.replace(rarilites, "レア", "R");
				rarilites = StringUtils.replace(rarilites, "ハイノーマル", "HN");
				rarilites = StringUtils.replace(rarilites, "ノーマル", "N");
				rarilites = StringUtils.replace(rarilites, "スペシャル", "SP");

				JSONObject card = null;
				if (mapping.containsKey(name)) {
					card = mapping.get(name);
					JSONArray illList = card.getJSONArray("ill");
					illList.add(ill);
				} else {
					card = new JSONObject();
					card.put("name", name);
					card.put("kana", kana);
					card.put("rarilites", rarilites);
					card.put("region", StringUtils.defaultIfBlank(region, "不明"));
					card.put("pref", StringUtils.defaultIfBlank(pref, "不明"));
					card.put("type", StringUtils.defaultIfBlank(type, "不明"));

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
