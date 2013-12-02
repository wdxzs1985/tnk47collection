package robot.tnk47.quest;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.EventHandler;
import robot.Robot;

public class QuestStageDetailHandler extends AbstractEventHandler<Robot>
		implements EventHandler {

	private static final Pattern STAGE_NAME_PATTERN = Pattern
			.compile("<p>(\\d+章)&nbsp;(\\d+-\\d+)&nbsp;<span>(.*?)</span></p>");

	public QuestStageDetailHandler(final Robot robot) {
		super(robot);
	}

	@Override
	protected void handleIt() {
		final Properties session = this.robot.getSession();
		final String questId = session.getProperty("questId");
		final String areaId = session.getProperty("areaId");
		final String stageId = session.getProperty("stageId");
		final String path = String.format(
				"/quest/stage-detail?questId=%s&areaId=%s&stageId=%s", questId,
				areaId, stageId);
		final String html = this.httpGet(path);
		this.resolveInputToken(html);

		if (this.log.isInfoEnabled()) {
			Matcher stageNameMatcher = STAGE_NAME_PATTERN.matcher(html);
			if (stageNameMatcher.find()) {
				String charpter = stageNameMatcher.group(1);
				String section = stageNameMatcher.group(2);
				String region = stageNameMatcher.group(3);
				this.log.info(String.format("进入地图: %s %s %s", charpter,
						section, region));
			}
		}

		this.robot.dispatch("/quest/stage/forward");
	}

}
