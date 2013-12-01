package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestStageForwardHandler extends AbstractEventHandler<Robot> {

    public QuestStageForwardHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final Properties session = this.robot.getSession();
        final String questId = session.getProperty("questId");
        final String areaId = session.getProperty("areaId");
        final String stageId = session.getProperty("stageId");
        final String token = session.getProperty("token");
        final String input = this.robot.buildPath("/quest/ajax/put-stage-forward");
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("questId", questId));
        nvps.add(new BasicNameValuePair("areaId", areaId));
        nvps.add(new BasicNameValuePair("stageId", stageId));
        nvps.add(new BasicNameValuePair("token", token));

        final String html = this.robot.getHttpClient().post(input, nvps);

        final JSONObject jsonResponse = JSONObject.fromObject(html);
        final String newToken = jsonResponse.getString("token");
        session.put("token", newToken);

        final JSONObject data = jsonResponse.getJSONObject("data");
        // 通关
        if (data.getBoolean("clearStage")) {
            this.robot.dispatch("/quest");
            return;
        }
        // 升级
        if (data.getBoolean("levelUp")) {
            this.onLevelUp(data);
            return;
        }
        final String questMessage = data.getString("questMessage");
        if (!StringUtils.equals(questMessage, "null")) {
            if (this.log.isInfoEnabled()) {
                this.log.info(questMessage);
            }
            if (StringUtils.equals("行動Ptが足りません", questMessage)) {
                this.onStaminaOut(data);
                return;
            }
        } else {
            final String needExpForNextLevel = data.getString("needExpForNextLevel");
            session.setProperty("needExpForNextLevel", needExpForNextLevel);
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("什么都没有发现，还有[%s]经验升级。",
                                            needExpForNextLevel));
            }
        }
        this.robot.dispatch("/quest/stage/forward");
    }

    private void onLevelUp(final JSONObject data) {
        final Properties session = this.robot.getSession();
        final JSONObject userData = data.getJSONObject("userData");
        final String maxStamina = userData.getString("maxStamina");
        final String maxPower = userData.getString("maxPower");
        final String attrPoints = userData.getString("attrPoints");
        final String level = userData.getString("level");
        if (this.log.isInfoEnabled()) {
            this.log.info("升到了" + level + "级");
        }
        session.put("maxStamina", maxStamina);
        session.put("maxPower", maxPower);
        session.put("attrPoints", attrPoints);
        session.put("callback", "/quest/stage/forward");
        this.robot.dispatch("/status-up");
    }

    private void onStaminaOut(final JSONObject data) {
        boolean useItem = false;
        final Properties session = this.robot.getSession();
        final JSONObject userData = data.getJSONObject("userData");
        final int maxStamina = userData.getInt("maxStamina");
        if (data.containsKey("regenStaminaItems")) {
            final boolean useStamina50 = Boolean.valueOf(session.getProperty("QuestStageForwardHandler.useStamina50",
                                                                             "false"));
            final boolean useStamina100 = Boolean.valueOf(session.getProperty("QuestStageForwardHandler.useStamina100",
                                                                              "false"));
            final int needExpForNextLevel = Integer.valueOf(session.getProperty("needExpForNextLevel",
                                                                                "0"));
            final JSONArray regenStaminaItems = data.getJSONArray("regenStaminaItems");
            for (int i = 0; i < regenStaminaItems.size(); i++) {
                final JSONObject regenStamina = (JSONObject) regenStaminaItems.get(i);
                final String code = regenStamina.getString("code");
                final String name = regenStamina.getString("name");
                final String itemId = regenStamina.getString("itemId");
                if (StringUtils.contains(name, "当日")) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    useItem = true;
                    break;
                } else if (StringUtils.contains(code, "stamina50") && useStamina50
                           && needExpForNextLevel > maxStamina / 2) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    useItem = true;
                    break;
                } else if (StringUtils.contains(code, "stamina100") && useStamina100
                           && needExpForNextLevel > maxStamina) {
                    session.put("itemId", itemId);
                    session.put("name", name);
                    useItem = true;
                    break;
                }
            }
        }
        if (useItem) {
            session.put("callback", "/quest/stage/forward");
            this.robot.dispatch("/use-item");
        } else {
            final boolean regenerate = Boolean.valueOf(session.getProperty("QuestStageForwardHandler.regenerate",
                                                                           "false"));
            if (regenerate) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("等回血");
                }
                this.robot.dispatch("/mypage");
            } else {
                if (this.log.isInfoEnabled()) {
                    this.log.info("放弃治疗了");
                }
            }
        }
    }
}