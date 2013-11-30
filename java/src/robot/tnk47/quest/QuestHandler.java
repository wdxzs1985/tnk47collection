package robot.tnk47.quest;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestHandler extends AbstractEventHandler<Robot> {

    private static final Pattern STAGE_INTRODUCTION_PATTERN = Pattern.compile("<a href=\"/quest\\?introductionFinish=true\">");
    private static final Pattern STAGE_DETAIL_PATTERN = Pattern.compile("<a href=\"/quest/stage-detail\\?questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)\">(.*)</a>");
    private static final Pattern BOSS_PATTERN = Pattern.compile("<section class=\"questInfo infoBox boss\">");
    private static final Pattern INPUT_TOKEN_PATTERN = Pattern.compile("<input id=\"__token\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\" data-page-id=\".*\">");

    public QuestHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        String input = this.robot.buildPath("/quest");
        String html = this.robot.getHttpClient().get(input);

        final Matcher stageIntrodutionMatcher = QuestHandler.STAGE_INTRODUCTION_PATTERN.matcher(html);
        if (stageIntrodutionMatcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("进入新关卡");
            }
            input = this.robot.buildPath("/quest?introductionFinish=true");
            html = this.robot.getHttpClient().get(input);
        }

        final Matcher bossMatcher = QuestHandler.BOSS_PATTERN.matcher(html);
        if (bossMatcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("BOSS出现");
            }
            final Matcher tokenMatcher = QuestHandler.INPUT_TOKEN_PATTERN.matcher(html);
            if (tokenMatcher.find()) {
                final String token = tokenMatcher.group(1);
                session.put("token", token);
                this.robot.dispatch("/quest/boss-animation");
                return;
            }
        }

        final Matcher matcher = QuestHandler.STAGE_DETAIL_PATTERN.matcher(html);
        if (matcher.find()) {
            final String questId = matcher.group(1);
            final String areaId = matcher.group(2);
            final String stageId = matcher.group(3);
            final String stageName = matcher.group(4);

            if (this.log.isInfoEnabled()) {
                this.log.info("进入地图: " + stageName);
            }

            session.setProperty("questId", questId);
            session.setProperty("areaId", areaId);
            session.setProperty("stageId", stageId);

            this.robot.dispatch("/quest/stage/detail");
        }
    }
}
