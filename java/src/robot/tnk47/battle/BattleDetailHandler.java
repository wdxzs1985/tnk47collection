package robot.tnk47.battle;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import robot.AbstractEventHandler;
import robot.Robot;

public class BattleDetailHandler extends AbstractEventHandler<Robot> {

    private static final Pattern PAGE_PARAMS_PATTERN = Pattern.compile("tnk.pageParams = (\\{.*\\});");
    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");

    public BattleDetailHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String prefectureBattleId = session.getProperty("prefectureBattleId");
        final String path = String.format("/battle?prefectureBattleId=%s",
                                          prefectureBattleId);
        final String html = this.httpGet(path);

        final Matcher battleResultMatcher = BattleDetailHandler.BATTLE_RESULT_PATTERN.matcher(html);
        if (battleResultMatcher.find()) {
            session.setProperty("prefectureBattleId", prefectureBattleId);
            this.robot.dispatch("/battle/prefecture-battle-result");
            return;
        }

        this.resolveInputToken(html);

        final Matcher pageParamsMatcher = BattleDetailHandler.PAGE_PARAMS_PATTERN.matcher(html);
        if (pageParamsMatcher.find()) {
            final String pageParams = pageParamsMatcher.group(1);
            final JSONObject jsonPageParams = JSONObject.fromObject(pageParams);
            final String battleStartType = jsonPageParams.getString("battleStartType");
            session.setProperty("battleStartType", battleStartType);
            final JSONObject userData = jsonPageParams.getJSONObject("userData");
            final JSONObject data = userData.getJSONObject("data");
            final JSONArray friendData = data.getJSONArray("friendData");
            int maxUserLoseCount = 0;
            JSONObject supportFriend = null;
            for (int i = 0; i < friendData.size(); i++) {
                final JSONObject friend = friendData.getJSONObject(i);
                if (friend.getBoolean("canSupport")) {
                    final int userLoseCount = friend.getInt("userLoseCount");
                    if (maxUserLoseCount < userLoseCount) {
                        supportFriend = friend;
                        maxUserLoseCount = userLoseCount;
                    }
                }
            }
            if (supportFriend != null) {
                final String userId = supportFriend.getString("userId");
                session.setProperty("supportUserId", userId);
                this.robot.dispatch("/battle/battle-support");
                return;
            }

            final JSONArray enemyData = data.getJSONArray("enemyData");
            JSONObject battleEnemy = null;
            int maxBattlePoint = 0;
            for (int i = 0; i < enemyData.size(); i++) {
                final JSONObject enemy = enemyData.getJSONObject(i);
                final int getBattlePoint = enemy.getInt("getBattlePoint");
                if (maxBattlePoint < getBattlePoint) {
                    battleEnemy = enemy;
                    maxBattlePoint = getBattlePoint;
                }
            }
            if (battleEnemy != null) {
                final String userId = battleEnemy.getString("userId");
                session.setProperty("enemyId", userId);
                this.robot.dispatch("/battle/battle-check");
                return;
            }
        }
        this.robot.dispatch("/mypage");
    }
}
