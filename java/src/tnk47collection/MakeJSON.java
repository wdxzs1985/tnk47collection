package tnk47collection;

import java.io.File;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class MakeJSON implements Runnable {

	public static final String INPUT = "card.csv";
	public static final String OUTPUT = "card.json";

	public static void main(String[] args) {
		new MakeJSON().run();
	}

	@Override
	public void run() {
		System.out.printf("%s start\n", this.getClass().getSimpleName());

		try {
			final List<String> mergeLines = FileUtils.readLines(new File(
					"card.csv"));

			final JSONArray outputList = new JSONArray();
			for (final String line : mergeLines) {
				final String[] prop = StringUtils.splitPreserveAllTokens(line,
						",");
				final String name = prop[0];
				final String region = prop[1];
				final String pref = prop[2];
				final String rarilites = prop[3];
				final String type = prop[4];
				final String ill = prop[5];
				final int evo = Integer.valueOf(prop[6]);

				JSONObject card = new JSONObject();
				card.put("name", name);
				card.put("region", StringUtils.defaultIfBlank(region, "不明"));
				card.put("pref", StringUtils.defaultIfBlank(pref, "不明"));
				card.put("rarilites", rarilites);
				card.put("type", StringUtils.defaultIfBlank(type, "不明"));

				if (StringUtils.isNotBlank(ill)) {
					JSONArray illList = new JSONArray();
					for (int i = 1; i <= evo; i++) {
						StringBuilder illBuilder = new StringBuilder(ill);
						illBuilder.deleteCharAt(ill.length() - 1).append(i);
						illList.add(illBuilder.toString());
					}
					card.put("ill", illList);
				}
				outputList.add(card);
			}

			FileUtils.write(new File(OUTPUT),
					"var cards = " + outputList.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.printf("%s end\n", this.getClass().getSimpleName());
	}
}
