package robot.tnk47.marathon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import robot.AbstractEventHandler;
import robot.Robot;

public class MarathonStageDetailHandler extends AbstractEventHandler<Robot> {


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

        this.resolveInputToken(html);

    }

}
