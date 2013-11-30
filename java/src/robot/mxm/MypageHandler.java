package robot.mxm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import robot.AbstractEventHandler;
import robot.Robot;

public class MypageHandler extends AbstractEventHandler<Robot> {

    private static final Pattern HTML_STAMINA_PATTERN = Pattern.compile("<div class=\"staminaGageOnTxt\"><span class=\"colorWhite\">(\\d+)</span><span class=\"colorDeepOrange\">/(\\d+)</span></div>");

    private final Log log = LogFactory.getLog(MypageHandler.class);

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
        final Matcher staminaMatcher = MypageHandler.HTML_STAMINA_PATTERN.matcher(html);
        if (staminaMatcher.find()) {
            final int stamina = Integer.valueOf(staminaMatcher.group(1));
            if (stamina > 0) {
                this.robot.dispatch("/user/user_list");
            }
        }
    }

}
