package tnk47collection;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MarathonQuestHandler implements EventHandler {

	private static final boolean IS_DEFBUG = true;
	private final Robot robot;
	private final Map<String, String> attributeMap;

	public MarathonQuestHandler(Robot robot) {
		this.robot = robot;
		this.attributeMap = new HashMap<String, String>();
	}

	@Override
	public void handle() {
		String eventId = this.attributeMap.get("eventId");
		if (StringUtils.isBlank(eventId)) {
			return;
		}
		String input = Robot.HOST + "/event/marathon/event-quest?eventId="
				+ eventId;
		String html = this.robot.getHttpClient().get(input);
		if (IS_DEFBUG) {
			System.out.println(html);
		}
	}

	@Override
	public void addAttribute(String name, String value) {
		this.attributeMap.put(name, value);
	}

}
