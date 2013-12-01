package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleResultHandler extends AbstractEventHandler<Robot> {

    private static final Pattern SINGLE_RESULT_PATTERN = Pattern.compile("<h1 class=\"pageName\">対戦結果</h1><p><span>(.*)に<em>(.*)</em>のダメージ!</span></p>");
    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");

    public BattleResultHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String token = session.getProperty("token");
        final String path = "/battle/battle-result";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);

        final Matcher battleResultMatcher = BattleResultHandler.BATTLE_RESULT_PATTERN.matcher(html);
        if (battleResultMatcher.find()) {
            final String prefectureBattleId = battleResultMatcher.group(1);
            session.setProperty("prefectureBattleId", prefectureBattleId);
            this.robot.dispatch("/battle/prefecture-battle-result");
            return;
        }

        if (this.log.isInfoEnabled()) {
            final Matcher singleResultMatcher = BattleResultHandler.SINGLE_RESULT_PATTERN.matcher(html);
            if (singleResultMatcher.find()) {
                final String name = singleResultMatcher.group(1);
                final String damage = singleResultMatcher.group(2);
                this.log.info(String.format("对%s造成%s的伤害", name, damage));
            }
        }

        this.robot.dispatch("/battle");
    }
}
