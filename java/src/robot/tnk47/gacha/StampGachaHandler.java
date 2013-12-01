package robot.tnk47.gacha;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class StampGachaHandler extends AbstractEventHandler<Robot> {

    private static final Pattern DISABLE_PATTHERN = Pattern.compile("<p class=\"actBtn remainingTime disable\"><span>(.*?)</span></p>");
    private static final Pattern ACTBTN_PATTHERN = Pattern.compile("<a href=\"(/gacha/gacha-free-animation\\?.*)\" class=\"actBtn \">");

    public StampGachaHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        final String html = this.httpPost("/gacha/stamp-gacha", nvps);
        final Matcher actBtnMatcher = StampGachaHandler.ACTBTN_PATTHERN.matcher(html);
        if (actBtnMatcher.find()) {
            String url = actBtnMatcher.group(1);
            url = StringEscapeUtils.unescapeHtml4(url);
            this.openGachaAnimation(url);
            if (this.log.isInfoEnabled()) {
                this.log.info("签到成功");
            }
        } else {
            if (this.log.isInfoEnabled()) {
                this.log.info("签到失败");
            }
            final Matcher disableMatcher = StampGachaHandler.DISABLE_PATTHERN.matcher(html);
            if (disableMatcher.find()) {
                if (this.log.isInfoEnabled()) {
                    final String reason = disableMatcher.group(1);
                    this.log.info(reason);
                }
            }
        }
        this.robot.dispatch("/mypage");
    }

    private void openGachaAnimation(final String path) {
        this.httpGet(path);
    }

}
