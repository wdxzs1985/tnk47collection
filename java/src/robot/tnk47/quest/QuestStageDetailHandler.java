package robot.tnk47.quest;

import java.util.Properties;

import robot.AbstractEventHandler;
import robot.EventHandler;
import robot.Robot;

public class QuestStageDetailHandler extends AbstractEventHandler<Robot>
        implements EventHandler {

    public QuestStageDetailHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String questId = session.getProperty("questId");
        final String areaId = session.getProperty("areaId");
        final String stageId = session.getProperty("stageId");
        final String path = String.format("/quest/stage-detail?questId=%s&areaId=%s&stageId=%s",
                                          questId,
                                          areaId,
                                          stageId);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        this.robot.dispatch("/quest/stage/forward");
    }

}
