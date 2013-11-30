package robot.tnk47;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class MarathonStageForwardHandler extends AbstractEventHandler<Robot> {

    public MarathonStageForwardHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final Properties session = this.robot.getSession();
        final String eventId = session.getProperty("eventId");
        final String token = session.getProperty("token");
        if (StringUtils.isBlank(eventId)) {
            return;
        }
        if (StringUtils.isBlank(token)) {
            return;
        }
        final String input = this.robot.buildPath("/event/marathon/event-stage-detail?eventId=" + eventId);
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("eventId", eventId));
        nvps.add(new BasicNameValuePair("token", token));

        final String html = this.robot.getHttpClient().post(input, nvps);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }
    }

}
