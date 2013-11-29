package tnk47collection;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class MarathonHandler implements EventHandler {

	public static final Pattern URL_EVENT_QUEST_PATTERN = Pattern
			.compile("/event/marathon/event-quest\\?eventId=(\\d)+");

	private static final boolean IS_DEFBUG = true;
	private final Robot robot;
	private final Map<String, String> attributeMap;

	public MarathonHandler(Robot robot) {
		this.robot = robot;
		this.attributeMap = new HashMap<String, String>();
	}

	@Override
	public void handle() {
		String token = this.attributeMap.get("token");
		if (StringUtils.isBlank(token)) {
			return;
		}
		String input = Robot.HOST + "/event/marathon?token=" + token;
		String html = this.robot.getHttpClient().get(input);
		if (IS_DEFBUG) {
			System.out.println(html);
		}
		Matcher eventIdMatcher = URL_EVENT_QUEST_PATTERN.matcher(html);
		if (eventIdMatcher.find()) {
			String eventId = eventIdMatcher.group(1);
			Map<String, String> model = new HashMap<String, String>();
			model.put("eventId", eventId);
			this.robot.dispatch("/event/marathon/event-quest", model);
		} else {
			this.robot.dispatch("/mypage");
		}

		this.attributeMap.clear();
	}

	@Override
	public void addAttribute(String name, String value) {
		this.attributeMap.put(name, value);
	}

}
