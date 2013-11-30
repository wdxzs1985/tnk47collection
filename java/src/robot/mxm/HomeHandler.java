package robot.mxm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class HomeHandler extends AbstractEventHandler<Robot> {

    public static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>トップ | フレンダリアと魔法の指輪</title>");

    public HomeHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String input = this.robot.buildPath("/");
        final String html = this.robot.getHttpClient().get(input);
        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }
        final Matcher matcher = HomeHandler.HTML_TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            this.robot.dispatch("/mypage");
        } else {
            this.robot.dispatch("/login");
        }
    }
}
