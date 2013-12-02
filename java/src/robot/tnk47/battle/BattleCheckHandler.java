package robot.tnk47.battle;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.Robot;

public class BattleCheckHandler extends AbstractBattleHandler {

	public BattleCheckHandler(final Robot robot) {
		super(robot);
	}

	@Override
	protected String handleIt() {
		final Map<String, Object> session = this.robot.getSession();

		final String battleStartType = (String) session.get("battleStartType");
		final String enemyId = (String) session.get("enemyId");
		final String prefectureBattleId = (String) session
				.get("prefectureBattleId");
		final String path = "/battle/battle-check";
		final List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
		nvps.add(new BasicNameValuePair("enemyId", enemyId));
		nvps.add(new BasicNameValuePair("prefectureBattleId",
				prefectureBattleId));

		final String html = this.httpPost(path, nvps);

		if (this.isBattleResult(html)) {
			return ("/battle/prefecture-battle-result");
		}

		this.resolveInputToken(html);

		JSONObject jsonPageParams = this.resolvePageParams(html);
		if (jsonPageParams != null) {
			final int curPower = jsonPageParams.getInt("curPower");
			final JSONObject selectedDeckData = jsonPageParams
					.getJSONObject("selectedDeckData");
			final int spendAttackPower = selectedDeckData
					.getInt("spendAttackPower");
			if (curPower >= spendAttackPower || spendAttackPower == 0) {
				final String deckId = jsonPageParams
						.getString("selectedDeckId");
				session.put("deckId", deckId);
				session.put("attackType", "1");
				return ("/battle/battle-animation");
			} else {
				if (this.log.isInfoEnabled()) {
					this.log.info("攻pt不足");
				}
			}
		}
		return ("/mypage");
	}
}
