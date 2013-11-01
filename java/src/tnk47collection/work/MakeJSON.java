package tnk47collection.work;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.HTMLReader.TagAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import tnk47collection.common.CommonHttpClient;
import tnk47collection.common.SystemConstants;

public class MakeJSON implements Runnable {

	public static final String OUTPUT = "data/step4/card.json";

	@Override
	public void run() {
		System.out.printf("%s start\n", this.getClass().getSimpleName());

		List<String> mergeLines = new ArrayList<>();

		try {
			for (int i = 1; i <= 7; i++) {
				String input = String.format("data/step3/%d.csv", i);
				List<String> inputLines = FileUtils.readLines(new File(input));
				mergeLines.addAll(inputLines);
			}
			
			Map<String, JSONObject> mapping = new HashMap<>();
			for (String line : mergeLines) {
				String[] prop = StringUtils.splitPreserveAllTokens(line,",");
				String ill = prop[0];
				String name = prop[1];
				String kana = prop[2];
				String region = prop[3];
				String pref=prop[4];
				String type=prop[5];
				String rarilites=prop[7];
				
				JSONObject card = null;
				if(mapping.containsKey(name)){
					card = mapping.get(name);
					JSONArray illList =card.getJSONArray("ill");
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
			
			FileUtils.writeLines(new File(OUTPUT), mapping.values());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.printf("%s end\n", this.getClass().getSimpleName());
	}
}
