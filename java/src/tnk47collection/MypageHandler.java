package tnk47collection;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MypageHandler implements EventHandler {

	public static final Pattern URL_EVENT_MARATHON_PATTERN = Pattern
			.compile("/event/marathon\\?token=([A-Za-z]+)");

	private static final boolean IS_DEFBUG = true;

	private final Robot robot;

	public MypageHandler(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void handle() {
		String input = Robot.HOST + "/mypage";
		String html = this.robot.getHttpClient().get(input);
		if (IS_DEFBUG) {
			System.out.println(html);
		}
		Matcher eventMatcher = URL_EVENT_MARATHON_PATTERN.matcher(html);
		if (eventMatcher.find()) {
			String token = eventMatcher.group(1);
			Map<String, String> model = new HashMap<String, String>();
			model.put("token", token);
			this.robot.dispatch("/event/marathon", model);
		}
	}

	@Override
	public void addAttribute(String name, String value) {
	}

}
