package robot.tnk47.quest;

import robot.AbstractRobot;
import robot.LoginHandler;
import robot.tnk47.HomeHandler;
import robot.tnk47.LevelUpHandler;
import robot.tnk47.UseItemHandler;

public class QuestRobot extends AbstractRobot {

    public static void main(final String[] args) {
        final Thread thread = new Thread(new QuestRobot());
        try {
            thread.start();
            thread.join();
        } catch (final InterruptedException e) {
        }
    }

    public static final String HOST = "http://tnk47.ameba.jp";

    public QuestRobot() {
        super(QuestRobot.HOST);
        this.registerHandler("/", new HomeHandler(this));
        this.registerHandler("/login", new LoginHandler(this));
        this.registerHandler("/mypage", new QuestHandler(this));
        this.registerHandler("/quest", new QuestHandler(this));
        this.registerHandler("/quest/stage/detail",
                             new QuestStageDetailHandler(this));
        this.registerHandler("/quest/stage/forward",
                             new QuestStageForwardHandler(this));
        this.registerHandler("/quest/boss-animation",
                             new QuestBossHandler(this));
        this.registerHandler("/quest/introduction",
                             new QuestIntroductionHandler(this));
        this.registerHandler("/use-item", new UseItemHandler(this));
        this.registerHandler("/level-up", new LevelUpHandler(this));
    }
}
