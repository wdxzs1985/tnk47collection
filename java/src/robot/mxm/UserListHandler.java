package robot.mxm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.EventHandler;
import robot.Robot;

public class UserListHandler extends AbstractEventHandler<Robot> implements
        EventHandler {

    private static final Pattern HTML_USER_PATTERN = Pattern.compile("/user/(\\d+)/room");

    public UserListHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String input = this.robot.buildPath("/user/user_list");
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }

        final Matcher userMatcher = UserListHandler.HTML_USER_PATTERN.matcher(html);
        if (userMatcher.find()) {
            final String userId = userMatcher.group(1);
            this.robot.getSession().put("userId", userId);
            this.robot.dispatch("/user/room");
        }
    }

}
