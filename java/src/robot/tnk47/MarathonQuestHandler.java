package robot.tnk47;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.AbstractEventHandler;
import robot.Robot;

public class MarathonQuestHandler extends AbstractEventHandler<Robot> {

    public static final Pattern URL_EVENT_STAGE_DETAIL_PATTERN = Pattern.compile("/event/marathon/event-stage-detail\\?eventId=(\\d+)");

    public MarathonQuestHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String eventId = this.robot.getSession().getProperty("eventId");
        if (StringUtils.isBlank(eventId)) {
            return;
        }
        final String input = this.robot.buildPath("/event/marathon/event-quest?eventId=" + eventId);
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }
        final Matcher eventIdMatcher = MarathonQuestHandler.URL_EVENT_STAGE_DETAIL_PATTERN.matcher(html);
        if (eventIdMatcher.find()) {
            this.robot.getSession().put("eventId", eventId);
            this.robot.dispatch("/event/marathon/event-stage-detail");
        } else {
            this.robot.dispatch("/mypage");
        }
    }

}
