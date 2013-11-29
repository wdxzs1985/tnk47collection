package tnk47collection;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

public class LoginHandler implements EventHandler {

	public static final String USERNAME = "bushing";
	public static final String PASSWORD = "wangjue";
	private static final boolean IS_DEFBUG = false;

	private final Robot robot;

	public LoginHandler(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void handle() {
		this.getLoginForm();
		this.postLogin();
	}

	private void getLoginForm() {
		String url = "https://dauth.user.ameba.jp/login/ameba";
		String html = this.robot.getHttpClient().get(url);
		if (IS_DEFBUG) {
			System.out.println(html);
		}
	}

	private boolean postLogin() {
		String url = "https://login.user.ameba.jp/web/login";
		final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("username", USERNAME));
		nvps.add(new BasicNameValuePair("password", PASSWORD));
		String html = this.robot.getHttpClient().post(url, nvps);

		if (StringUtils.isBlank(html)) {
			return true;
		} else {
			if (IS_DEFBUG) {
				System.out.println(html);
			}
			return false;
		}
	}

	@Override
	public void addAttribute(String name, String value) {

	}
}
