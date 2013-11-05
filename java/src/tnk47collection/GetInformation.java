package tnk47collection;

import java.io.File;

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

	private CommonHttpClient httpClient;
	private File cookieFile;

	public GetInformation() {
		httpClient = new CommonHttpClient();
		cookieFile = new File("cookie");
		httpClient.loadCookie(cookieFile);
	}

	@Override
	public void run() {
		login();

//		String input = "http://tnk47.ameba.jp/information?infomationId=624";
//		String html = httpClient.get(input);
//		System.out.println(html);
		
		httpClient.saveCookie(cookieFile);
	}

	public void login() {
		String url = "https://dauth.user.ameba.jp/login/ameba";
		String html = httpClient.get(url);
		System.out.println(html);
	}

}
