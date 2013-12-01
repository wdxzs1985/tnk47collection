package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleAnimationHandler extends AbstractEventHandler<Robot> {

    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");

    public BattleAnimationHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String battleStartType = session.getProperty("battleStartType");
        final String enemyId = session.getProperty("enemyId");
        final String deckId = session.getProperty("deckId");
        final String attackType = session.getProperty("attackType");
        final String token = session.getProperty("token");
        final String path = "/battle/battle-animation";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
        nvps.add(new BasicNameValuePair("enemyId", enemyId));
        nvps.add(new BasicNameValuePair("deckId", deckId));
        nvps.add(new BasicNameValuePair("attackType", attackType));
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);

        final Matcher battleResultMatcher = BattleAnimationHandler.BATTLE_RESULT_PATTERN.matcher(html);
        if (battleResultMatcher.find()) {
            final String prefectureBattleId = battleResultMatcher.group(1);
            session.setProperty("prefectureBattleId", prefectureBattleId);
            this.robot.dispatch("/battle/prefecture-battle-result");
            return;
        }

        this.resolveInputToken(html);

        this.robot.dispatch("/battle/battle-result");
    }
}
