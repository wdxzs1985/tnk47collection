package tnk47collection;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import tnk47collection.common.CommonHttpClient;

public class GetInformation implements Runnable {

	public static void main(String[] args) {
		Thread thread = new Thread(new GetInformation());
		try {
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final CommonHttpClient httpClient;
	private final File cookieFile;

	public GetInformation() {
		this.httpClient = new CommonHttpClient();
		this.cookieFile = new File("cookie");
		this.httpClient.loadCookie(this.cookieFile);
	}

	@Override
	public void run() {
		boolean isDebug = false;
		this.getHome(isDebug);
		this.getLoginForm(isDebug);
		boolean isLogin = this.postLogin(isDebug);
		if (isLogin) {
			this.getHome(true);
		}

		this.httpClient.saveCookie(this.cookieFile);
	}

	public void getHome(boolean isDebug) {
		String input = "http://tnk47.ameba.jp";
		String html = this.httpClient.get(input);
		if (isDebug) {
			System.out.println(html);
		}
	}

	public void getLoginForm(boolean isDebug) {
		String url = "https://dauth.user.ameba.jp/login/ameba";
		String html = this.httpClient.get(url);
		if (isDebug) {
			System.out.println(html);
		}
	}

	public boolean postLogin(boolean isDebug) {
		String url = "https://login.user.ameba.jp/web/login";
		final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("username", "bushing"));
		nvps.add(new BasicNameValuePair("password", "wangjue"));
		String html = this.httpClient.post(url, nvps);

		if (StringUtils.isBlank(html)) {
			return true;
		} else {
			if (isDebug) {
				System.out.println(html);
			}
			return false;
		}
	}

}
