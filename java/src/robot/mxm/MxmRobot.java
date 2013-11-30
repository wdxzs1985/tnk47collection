package robot.mxm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import robot.AbstractRobot;
import robot.LoginHandler;

public class MxmRobot extends AbstractRobot {

    private static Log slog = LogFactory.getLog(MxmRobot.class);

    public static void main(final String[] args) {
        final String username = args[0];
        final String password = args[1];
        final Thread thread = new Thread(new MxmRobot(username, password));
        try {
            thread.start();
            thread.join();
        } catch (final InterruptedException e) {
            MxmRobot.slog.error(e.getMessage(), e);
        }
    }

    public static final String HOST = "http://mxm.ameba.jp";

    public MxmRobot(final String username, final String password) {
        super(MxmRobot.HOST, "mxm", username, password);
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new MypageHandler(this));
        this.registerHandler("/user/user_list", new UserListHandler(this));
        this.registerHandler("/user/room", new UserRoomHandler(this));
        this.dispatch("/");
    }

}
