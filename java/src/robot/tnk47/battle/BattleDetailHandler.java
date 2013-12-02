package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleDetailHandler extends AbstractEventHandler<Robot> {

	private static final Pattern BATTLE_RESULT_PATTERN = Pattern
			.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");
	private static final Pattern BATTLE_INVITE_PATTERN = Pattern
			.compile("救援依頼を出す");

	public BattleDetailHandler(final Robot robot) {
		super(robot);
	}

	@Override
	protected void handleIt() {
		final Properties session = this.robot.getSession();
		final String prefectureBattleId = session
				.getProperty("prefectureBattleId");
		final String path = String.format("/battle?prefectureBattleId=%s",
				prefectureBattleId);
		final String html = this.httpGet(path);

		final Matcher battleResultMatcher = BattleDetailHandler.BATTLE_RESULT_PATTERN
				.matcher(html);
		if (battleResultMatcher.find()) {
			session.setProperty("prefectureBattleId", prefectureBattleId);
			this.robot.dispatch("/battle/prefecture-battle-result");
			return;
		}

		this.resolveInputToken(html);

		final Matcher inviteMatcher = BATTLE_INVITE_PATTERN.matcher(html);
		if (inviteMatcher.find()) {
			this.sendInvite();
		}
		JSONObject jsonPageParams = this.resolvePageParams(html);
		if (jsonPageParams != null) {
			final String battleStartType = jsonPageParams
					.getString("battleStartType");
			session.setProperty("battleStartType", battleStartType);
			final JSONObject userData = jsonPageParams
					.getJSONObject("userData");
			final JSONObject data = userData.getJSONObject("data");
			final JSONArray friendData = data.getJSONArray("friendData");
			int maxUserLoseCount = 0;
			JSONObject supportFriend = null;
			for (int i = 0; i < friendData.size(); i++) {
				final JSONObject friend = friendData.getJSONObject(i);
				if (friend.getBoolean("canSupport")) {
					final int userLoseCount = friend.getInt("userLoseCount");
					if (maxUserLoseCount <= userLoseCount) {
						supportFriend = friend;
						maxUserLoseCount = userLoseCount;
					}
				}
			}
			if (supportFriend != null) {
				final String supportUserId = supportFriend.getString("userId");
				this.sendSupport(supportUserId);
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
				if (this.log.isInfoEnabled()) {
					String userName = battleEnemy.getString("userName");
					String userLevel = battleEnemy.getString("userLevel");
					this.log.info(String.format("attack to %s(%s)", userName,
							userLevel));
				}
				this.robot.dispatch("/battle/battle-check");
				return;
			}
		}
		this.robot.dispatch("/mypage");
	}

	private void sendInvite() {
		final Properties session = this.robot.getSession();
		final String token = session.getProperty("token");
		final String path = "/battle/ajax/put-prefecture-battle-invite";
		final List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("token", token));
		final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
		this.resolveJsonToken(jsonResponse);
		if (this.log.isInfoEnabled()) {
			final JSONObject data = jsonResponse.getJSONObject("data");
			final String resultMessage = data.getString("resultMessage");
			this.log.info(resultMessage);
		}
	}

	private void sendSupport(String supportUserId) {
		final Properties session = this.robot.getSession();
		final String token = session.getProperty("token");
		final String path = "/battle/ajax/put-battle-support";
		final List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("supportUserId", supportUserId));
		nvps.add(new BasicNameValuePair("token", token));
		final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
		this.resolveJsonToken(jsonResponse);
		if (this.log.isInfoEnabled()) {
			final JSONObject data = jsonResponse.getJSONObject("data");
			final String supportUserName = data.getString("supportUserName");
			this.log.info(String.format("给%s发送了应援", supportUserName));
		}
	}
}
