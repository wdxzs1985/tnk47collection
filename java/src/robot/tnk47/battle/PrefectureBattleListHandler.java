package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class PrefectureBattleListHandler extends AbstractEventHandler<Robot> {

    public PrefectureBattleListHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String prefectureId = session.getProperty("prefectureId");
        final String path = "/battle/ajax/get-prefecture-battle-list";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("prefectureId", prefectureId));
        nvps.add(new BasicNameValuePair("searchType", "3"));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        final String token = jsonResponse.getString("token");
        session.setProperty("token", token);
        final JSONObject data = jsonResponse.getJSONObject("data");
        final JSONObject prefectureBattleSystemDto = data.getJSONObject("prefectureBattleSystemDto");
        final String prefectureBattleSystemStatus = prefectureBattleSystemDto.getString("prefectureBattleSystemStatus");
        if (StringUtils.equals(prefectureBattleSystemStatus, "ACTIVE")) {
            final JSONArray prefectureBattleOutlines = data.getJSONArray("prefectureBattleOutlines");
            if (prefectureBattleOutlines.size() > 0) {
                final JSONObject battle = this.filterInviteBattle(prefectureBattleOutlines);
                final String prefectureBattleId = battle.getString("prefectureBattleId");
                if (this.log.isInfoEnabled()) {
                    final String otherPrefectureName = battle.getString("otherPrefectureName");
                    this.log.info(String.format("加入和%s的合战", otherPrefectureName));
                }
                session.setProperty("prefectureBattleId", prefectureBattleId);
                this.robot.dispatch("/battle/detail");
                return;
            }
            if (this.log.isInfoEnabled()) {
                this.log.info("没有合战情报");
            }
            final JSONArray prefectureBattleUsers = data.getJSONArray("prefectureBattleUsers");
            if (prefectureBattleUsers.size() > 0) {
                final JSONObject user = this.filterLowPowerUser(prefectureBattleUsers);
                final String enemyId = user.getString("userId");
                if (this.log.isInfoEnabled()) {
                    final String name = user.getString("name");
                    this.log.info("向" + name + "发动攻击");
                }
                session.setProperty("battleStartType", "1");
                session.setProperty("enemyId", enemyId);
                this.robot.dispatch("/battle/battle-check");
                return;
            }
        }
        this.robot.dispatch("/mypage");
    }

    private JSONObject filterInviteBattle(final JSONArray outlines) {
        for (int i = 0; i < outlines.size(); i++) {
            final JSONObject battle = outlines.getJSONObject(i);
            if (battle.getBoolean("isInvite")) {
                return battle;
            }
        }
        return outlines.getJSONObject(0);
    }

    private JSONObject filterLowPowerUser(final JSONArray users) {
        int minDefencePower = Integer.MAX_VALUE;
        JSONObject minDefencePowerUser = null;
        for (int i = 0; i < users.size(); i++) {
            final JSONObject user = users.getJSONObject(i);
            final int defencePower = user.getInt("defencePower");
            if (minDefencePower > defencePower) {
                minDefencePowerUser = user;
                minDefencePower = defencePower;
            }
        }
        return minDefencePowerUser;
    }
}
