package tnk47collection.work;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import tnk47collection.common.CommonHttpClient;
import tnk47collection.common.SystemConstants;

public class DownloadHTML implements Runnable {

	private CommonHttpClient httpClient;
	private int number;

	public DownloadHTML(int number) {
		httpClient = new CommonHttpClient();
		this.number = number;
	}

	@Override
	public void run() {
		System.out.printf("%s(%d) start\n",this.getClass().getSimpleName(), number);

		String input = String.format("http://tnk47.com/rarities/%d", number);
		String output = String.format("data/step1/%d.html", number);

		try {
			String html = httpClient.get(input);
			FileUtils.write(new File(output), html, SystemConstants.ENCODING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("%s(%d) end\n",this.getClass().getSimpleName(), number);
	}

}
