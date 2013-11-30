package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestBossHandler extends AbstractEventHandler<Robot> {

    private static final Pattern BOSS_RESULT_PATTERN = Pattern.compile("var bossResult = '(.*)';");

    public QuestBossHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final Properties session = this.robot.getSession();
        final String token = session.getProperty("token");

        final String input = this.robot.buildPath("/quest/boss-animation");
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.robot.getHttpClient().post(input, nvps);
        String bossName = "???";
        final Matcher bossResultMatcher = QuestBossHandler.BOSS_RESULT_PATTERN.matcher(html);
        if (bossResultMatcher.find()) {
            final String jsonString = bossResultMatcher.group(1);
            final JSONObject bossResult = JSONObject.fromObject(jsonString)
                                                    .getJSONObject("bossResult");
            final JSONObject bossInfo = bossResult.getJSONObject("bossInfo");
            bossName = bossInfo.getString("name");
            if (this.log.isInfoEnabled()) {
                this.log.info("击败BOSS: " + bossName);
            }
        }
        this.robot.dispatch("/quest");
    }

}
