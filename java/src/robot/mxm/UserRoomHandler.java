package robot.mxm;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import robot.AbstractEventHandler;
import robot.EventHandler;
import robot.Robot;

public class UserRoomHandler extends AbstractEventHandler<Robot> implements
        EventHandler {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("mxm.token = \"([0-9a-zA-Z]{6})\";");

    public UserRoomHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String userId = this.robot.getSession().get("userId");
        if (StringUtils.isBlank(userId)) {
            return;
        }

        final String input = this.robot.buildPath("/user/" + userId + "/room");
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }

        final Map<String, String> model = new HashMap<String, String>();

        final Matcher tokenMatcher = UserRoomHandler.TOKEN_PATTERN.matcher(input);
        if (tokenMatcher.find()) {
            final String token = tokenMatcher.group(1);
            model.put("token", token);
        }

    }

}
