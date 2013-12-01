package robot.tnk47.gift;

import robot.AbstractEventHandler;
import robot.Robot;

public class GiftHandler extends AbstractEventHandler<Robot> {

    public GiftHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        this.httpGet("/gift");
        this.robot.dispatch("/mypage");
    }

}
