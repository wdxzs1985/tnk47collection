package robot.tnk47.quest;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestHandler extends AbstractEventHandler<Robot> {

    private static final Pattern STAGE_INTRODUCTION_PATTERN = Pattern.compile("<a href=\"/quest\\?introductionFinish=true\">");
    private static final Pattern STAGE_DETAIL_PATTERN = Pattern.compile("<a href=\"/quest/stage-detail\\?questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)\">(.*)</a>");
    private static final Pattern BOSS_PATTERN = Pattern.compile("<section class=\"questInfo infoBox boss\">");

    public QuestHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        String html = this.httpGet("/quest");

        final Matcher stageIntrodutionMatcher = QuestHandler.STAGE_INTRODUCTION_PATTERN.matcher(html);
        if (stageIntrodutionMatcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("进入新关卡");
            }
            html = this.httpGet("/quest?introductionFinish=true");
        }

        final Matcher bossMatcher = QuestHandler.BOSS_PATTERN.matcher(html);
        if (bossMatcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("BOSS出现");
            }
            this.resolveInputToken(html);
            this.robot.dispatch("/quest/boss-animation");
            return;
        }

        this.resolveInputToken(html);

        final Matcher matcher = QuestHandler.STAGE_DETAIL_PATTERN.matcher(html);
        if (matcher.find()) {
            final String questId = matcher.group(1);
            final String areaId = matcher.group(2);
            final String stageId = matcher.group(3);

            if (this.log.isInfoEnabled()) {
                String stageName = matcher.group(4);
                stageName = StringEscapeUtils.unescapeHtml4(stageName);
                this.log.info("进入地图: " + stageName);
            }

            session.setProperty("questId", questId);
            session.setProperty("areaId", areaId);
            session.setProperty("stageId", stageId);

            this.robot.dispatch("/quest/stage/forward");
        }
    }
}
