package robot.tnk47;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import robot.AbstractRobot;
import robot.LoginHandler;

public class MarathonRobot extends AbstractRobot {

    private static Log slog = LogFactory.getLog(MarathonRobot.class);

    public static void main() {
        final Thread thread = new Thread(new MarathonRobot());
        try {
            thread.start();
            thread.join();
        } catch (final InterruptedException e) {
            MarathonRobot.slog.error(e.getMessage(), e);
        }
    }

    public static final String HOST = "http://tnk47.ameba.jp";

    public MarathonRobot() {
        super(MarathonRobot.HOST);
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));
        this.registerHandler("/event/marathon/event-stage-detail",
                             new MarathonStageDetailHandler(this));
        this.registerHandler("/event/marathon/ajax/put-event-stage-forward",
                             new MarathonStageForwardHandler(this));
        this.dispatch("/");
    }

}
