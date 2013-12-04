package tnk47collection.work2;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

import common.CommonHttpClient;
import common.SystemConstants;

public class DownloadHTML implements Runnable {
	private final CommonHttpClient httpClient;
	private final int start;
	private final int end;
	private final Log log;

	public static void main(String[] args) {
		DownloadHTML worker = new DownloadHTML(3849, 7284);
		worker.run();
	}

	public DownloadHTML(int start, int end) {
		this.log = LogFactory.getLog(DownloadHTML.class);
		this.httpClient = new CommonHttpClient();
		this.start = start;
		this.end = end;
	}

	@Override
	public void run() {
		this.httpGet("/");
		if (this.login()) {
			this.httpGet("/mypage");
			for (int i = this.start; i <= this.end; i++) {
				String html = this.httpGet(String.format(
						"/gacha/gacha-detail?gachaId=%d", i));
				String output = String.format("data2/step1/%d.html", i);
				try {
					File file = new File(output);
					FileUtils.write(file, html, SystemConstants.ENCODING);
				} catch (IOException e) {
					this.log.error(e.getMessage(), e);
				}
				try {
					int sleepTime = 1000 + RandomUtils.nextInt(1000);
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public boolean login() {
		final String url = "https://login.user.ameba.jp/web/login";
		final String username = "bushing";
		final String password = "wangjue";

		final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("password", password));
		final String html = this.httpClient.post(url, nvps);

		if (StringUtils.isNotBlank(html)) {
			return false;
		}
		return true;
	}

	protected String httpGet(final String path) {
		final String url = this.buildPath(path);
		final String html = this.httpClient.get(url);
		this.httpClient.setReferer(url);
		return html;
	}

	protected String httpPost(final String path,
			final List<BasicNameValuePair> nvps) {
		final String url = this.buildPath(path);
		final String html = this.httpClient.post(url, nvps);
		this.httpClient.setReferer(url);
		return html;
	}

	private String buildPath(String path) {
		return "http://tnk47.ameba.jp" + path;
	}

}
