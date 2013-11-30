package robot.tnk47.quest;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestIntroductionHandler extends AbstractEventHandler<Robot> {

    public QuestIntroductionHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String input = this.robot.buildPath("/quest?introductionFinish=true");
        this.robot.getHttpClient().get(input);
        this.robot.dispatch("/quest");
    }

}
