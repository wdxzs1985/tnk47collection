package robot.tnk47.battle;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleHandler extends AbstractEventHandler<Robot> {

	private static final Pattern BATTLE_RESULT_PATTERN = Pattern
			.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");

	public BattleHandler(final Robot robot) {
		super(robot);
	}

	@Override
	protected void handleIt() {
		final Properties session = this.robot.getSession();
		final String html = this.httpGet("/battle");

		final Matcher battleResultMatcher = BattleHandler.BATTLE_RESULT_PATTERN
				.matcher(html);
		if (battleResultMatcher.find()) {
			final String prefectureBattleId = battleResultMatcher.group(1);
			session.setProperty("prefectureBattleId", prefectureBattleId);
			this.robot.dispatch("/battle/prefecture-battle-result");
			return;
		}

		this.resolveInputToken(html);

		final JSONObject jsonPageParams = this.resolvePageParams(html);
		if (jsonPageParams != null) {
			final String battleStartType = jsonPageParams
					.getString("battleStartType");
			session.setProperty("battleStartType", battleStartType);

			if (StringUtils.equals(battleStartType, "1")) {
				if (this.log.isInfoEnabled()) {
					this.log.info("尚未参战");
				}
				final String prefectureId = jsonPageParams
						.getString("prefectureId");
				session.setProperty("prefectureId", prefectureId);
				this.robot.dispatch("/battle/prefecture-battle-list");
				return;
			} else {
				if (this.log.isInfoEnabled()) {
					this.log.info("合战中");
				}

				final String prefectureBattleId = jsonPageParams
						.getString("prefectureBattleId");
				session.setProperty("prefectureBattleId", prefectureBattleId);
				this.robot.dispatch("/battle/detail");
				return;
			}
		}

		this.robot.dispatch("/mypage");
	}
}
