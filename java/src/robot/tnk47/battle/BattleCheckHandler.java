package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleCheckHandler extends AbstractEventHandler<Robot> {

    private static final Pattern PAGE_PARAMS_PATTERN = Pattern.compile("tnk.pageParams = (\\{.*\\});");
    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");

    public BattleCheckHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String battleStartType = session.getProperty("battleStartType");
        final String enemyId = session.getProperty("enemyId");
        final String prefectureBattleId = session.getProperty("prefectureBattleId");
        final String path = "/battle/battle-check";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        nvps.add(new BasicNameValuePair("enemyId", enemyId));
        nvps.add(new BasicNameValuePair("prefectureBattleId",
                                        prefectureBattleId));

        final String html = this.httpPost(path, nvps);

        final Matcher battleResultMatcher = BattleCheckHandler.BATTLE_RESULT_PATTERN.matcher(html);
        if (battleResultMatcher.find()) {
            session.setProperty("prefectureBattleId", prefectureBattleId);
            this.robot.dispatch("/battle/prefecture-battle-result");
            return;
        }

        this.resolveInputToken(html);

        final Matcher pageParamsMatcher = BattleCheckHandler.PAGE_PARAMS_PATTERN.matcher(html);
        if (pageParamsMatcher.find()) {
            final String pageParams = pageParamsMatcher.group(1);
            final JSONObject jsonPageParams = JSONObject.fromObject(pageParams);
            final int maxPower = jsonPageParams.getInt("maxPower");
            final int curPower = jsonPageParams.getInt("curPower");
            if (curPower >= maxPower / 5) {
                final String deckId = jsonPageParams.getString("selectedDeckId");
                session.setProperty("deckId", deckId);
                session.setProperty("attackType", "1");
                this.robot.dispatch("/battle/battle-animation");
                return;
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("攻pt不足");
                }
            }
        }
        this.robot.dispatch("/mypage");
    }
}
