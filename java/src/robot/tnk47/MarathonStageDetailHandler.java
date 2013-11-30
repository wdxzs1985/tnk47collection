package robot.tnk47;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import robot.AbstractEventHandler;
import robot.Robot;

public class MarathonStageDetailHandler extends AbstractEventHandler<Robot> {

    public static final Pattern INPUT_TOKEN_PATTERN = Pattern.compile("<input id=\"__token\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\" data-page-id=\"eventStageDetail\">");

    public MarathonStageDetailHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String eventId = this.robot.getSession().getProperty("eventId");
        if (StringUtils.isBlank(eventId)) {
            return;
        }
        final String input = this.robot.buildPath("/event/marathon/event-stage-detail?eventId=" + eventId);
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }
        final Matcher tokenMatcher = MarathonStageDetailHandler.INPUT_TOKEN_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String token = tokenMatcher.group(1);
            this.robot.getSession().put("token", token);
            this.robot.dispatch("/event/marathon/ajax/put-event-stage-forward");
        } else {
            this.robot.dispatch("/mypage");
        }
    }

}
