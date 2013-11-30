package robot.tnk47;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class HomeHandler extends AbstractEventHandler<Robot> {

    public static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>天下統一クロニクル</title>");

    public HomeHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String input = this.robot.buildPath("/");
        final String html = this.robot.getHttpClient().get(input);
        final Matcher matcher = HomeHandler.HTML_TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            this.robot.dispatch("/mypage");
        } else {
            this.robot.dispatch("/login");
        }
    }

}
