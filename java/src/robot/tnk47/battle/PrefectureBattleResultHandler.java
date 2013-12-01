package robot.tnk47.battle;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import robot.AbstractEventHandler;
import robot.Robot;

public class PrefectureBattleResultHandler extends AbstractEventHandler<Robot> {

    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("<section id=\"groupBattleResult\" class=\"(win|lose)\">");

    public PrefectureBattleResultHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String prefectureBattleId = session.getProperty("prefectureBattleId");
        final String token = session.getProperty("token");
        final String input = this.robot.buildPath(String.format("/battle/prefecture-battle-result?prefectureBattleId=%s&token=&s",
                                                                prefectureBattleId,
                                                                token));
        final String html = this.robot.getHttpClient().get(input);

        if (this.log.isInfoEnabled()) {
            final Matcher battleResultMatcher = PrefectureBattleResultHandler.BATTLE_RESULT_PATTERN.matcher(html);
            if (battleResultMatcher.find()) {
                final String result = battleResultMatcher.group(1);
                if (StringUtils.equals(result, "win")) {
                    this.log.info("合战胜利");
                } else {
                    this.log.info("合战败北");
                }
            }
        }

        this.robot.dispatch("/battle");
    }
}
