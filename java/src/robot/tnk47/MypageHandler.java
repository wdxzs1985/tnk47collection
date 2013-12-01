package robot.tnk47;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class MypageHandler extends AbstractEventHandler<Robot> {

    private static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>天クロ｜マイページ</title>");

    private boolean checkEventInfomation = true;
    private boolean checkStampGachaStatus = true;
    private boolean quest = true;
    private boolean battle = true;

    public MypageHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        this.robot.getSession();

        final String html = this.httpGet("/mypage");
        final Matcher matcher = MypageHandler.HTML_TITLE_PATTERN.matcher(html);
        if (!matcher.find()) {
            // 登录奖励
            this.robot.dispatch("/mypage");
            return;
        }

        this.resolveInputToken(html);

        if (this.checkStampGachaStatus) {
            this.checkStampGachaStatus = false;
            this.robot.dispatch("/gacha/stamp-gacha");
            return;
        }

        if (this.checkEventInfomation) {
            this.checkEventInfomation = false;
            this.robot.dispatch("/event-infomation");
            return;
        }

        // if (this.checkGift) {
        // this.checkGift = false;
        // this.robot.dispatch("/gift");
        // return;
        // }

        if (this.quest) {
            this.quest = false;
            this.robot.dispatch("/quest");
            return;
        }

        if (this.battle) {
            this.battle = false;
            this.robot.dispatch("/battle");
            return;
        }

        this.log.info("休息一会 _(:3_ ");
        this.checkStampGachaStatus = true;
        this.checkEventInfomation = true;
        this.quest = true;
        this.battle = true;

        final Properties session = this.robot.getSession();
        final long sleepTime = Long.valueOf(session.getProperty("MypageHandler.sleepTime",
                                                                "3600000"));
        try {
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
        }
        this.robot.dispatch("/mypage");
    }

}
