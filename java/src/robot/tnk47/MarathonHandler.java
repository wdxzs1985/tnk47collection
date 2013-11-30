package robot.tnk47;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import robot.AbstractEventHandler;

public class MarathonHandler extends AbstractEventHandler<MarathonRobot> {

    public static final Pattern URL_EVENT_QUEST_PATTERN = Pattern.compile("/event/marathon/event-quest\\?eventId=(\\d+)");

    public MarathonHandler(final MarathonRobot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String token = this.robot.getSession().getProperty("token");
        if (StringUtils.isBlank(token)) {
            return;
        }
        final String input = this.robot.buildPath("/event/marathon?token=" + token);
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }
        final Matcher eventIdMatcher = MarathonHandler.URL_EVENT_QUEST_PATTERN.matcher(html);
        if (eventIdMatcher.find()) {
            final String eventId = eventIdMatcher.group(1);
            this.robot.getSession().put("eventId", eventId);
            this.robot.dispatch("/event/marathon/event-stage-detail");
        } else {
            this.robot.dispatch("/mypage");
        }

    }
}
