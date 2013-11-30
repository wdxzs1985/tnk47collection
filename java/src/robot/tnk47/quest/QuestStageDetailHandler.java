package robot.tnk47.quest;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.EventHandler;
import robot.Robot;

public class QuestStageDetailHandler extends AbstractEventHandler<Robot>
        implements EventHandler {

    public static final Pattern INPUT_TOKEN_PATTERN = Pattern.compile("<input id=\"__token\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\" data-page-id=\".*\">");

    public QuestStageDetailHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String questId = session.getProperty("questId");
        final String areaId = session.getProperty("areaId");
        final String stageId = session.getProperty("stageId");
        final String input = this.robot.buildPath(String.format("/quest/stage-detail?questId=%s&areaId=%s&stageId=%s",
                                                                questId,
                                                                areaId,
                                                                stageId));
        final String html = this.robot.getHttpClient().get(input);
        final Matcher tokenMatcher = QuestStageDetailHandler.INPUT_TOKEN_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String token = tokenMatcher.group(1);
            session.put("token", token);
            this.robot.dispatch("/quest/stage/forward");
        } else {
            this.robot.dispatch("/quest");
        }
    }

}
