package tnk47collection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeHandler implements EventHandler {

	public static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>天下統一クロニクル</title>");
	private static final boolean IS_DEFBUG = true;
	private final Robot robot;

	public HomeHandler(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void handle() {
		String input = Robot.HOST + "/";
		String html = this.robot.getHttpClient().get(input);
		if (IS_DEFBUG) {
			System.out.println(html);
		}
		Matcher matcher = HTML_TITLE_PATTERN.matcher(html);
		if (matcher.find()) {
			this.robot.dispatch("/mypage");
		} else {
			this.robot.dispatch("/login");
		}
	}

	@Override
	public void addAttribute(String name, String value) {
		// TODO Auto-generated method stub

	}
}
