package robot.tnk47;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class MypageHandler extends AbstractEventHandler<Robot> {

    public static final Pattern URL_EVENT_MARATHON_PATTERN = Pattern.compile("event/marathon/event-marathon\\?eventId=([0-9]+)");
    public static final Pattern URL_STAMINA_PATTERN = Pattern.compile("event/marathon/event-marathon\\?eventId=([0-9]+)");

    public MypageHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String input = this.robot.buildPath("/mypage");
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }
        final Matcher eventMatcher = MypageHandler.URL_EVENT_MARATHON_PATTERN.matcher(html);
        if (eventMatcher.find()) {
            final String eventId = eventMatcher.group(1);
            this.robot.getSession().put("eventId", eventId);
            this.robot.dispatch("/event/marathon/event-stage-detail");
        }
    }

}
