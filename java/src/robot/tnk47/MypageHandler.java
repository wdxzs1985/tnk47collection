package robot.tnk47;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public class MypageHandler extends AbstractEventHandler<Robot> {

	private static final Pattern HTML_TITLE_PATTERN = Pattern
			.compile("<title>(.*)?</title>");
	private static final Pattern HTML_USER_STATUS_PATTERN = Pattern
			.compile("<div class=\"userStatusParams\">(.*?)</div>");
	private static final Pattern HTML_USER_NAME_PATTERN = Pattern
			.compile("<p class=\"userName\">(.*?)</p>");
	private static final Pattern HTML_USER_LEVEL_PATTERN = Pattern
			.compile("<dl class=\"userLevel\"><dt>Lv</dt><dd>(.*?)</dd></dl>");
	private boolean checkEventInfomation = true;
	private boolean checkStampGachaStatus = true;
	private boolean checkGift = true;
	private boolean quest = true;
	private boolean battle = true;

	public MypageHandler(final Robot robot) {
		super(robot);
	}

	@Override
	public void handleIt() {
		this.robot.getSession();

		final String html = this.httpGet("/mypage");
		final Matcher userStatusMatcher = MypageHandler.HTML_USER_STATUS_PATTERN
				.matcher(html);
		if (userStatusMatcher.find()) {
			String userStatusHtml = userStatusMatcher.group(1);
			this.printMyInfo(userStatusHtml);
		} else {
			// 登录奖励
			if (this.log.isInfoEnabled()) {
				Matcher titleMatcher = HTML_TITLE_PATTERN.matcher(html);
				if (titleMatcher.find()) {
					String title = titleMatcher.group(1);
					this.log.info(title);
				}
			}
			this.robot.dispatch("/mypage");
			return;
		}

		this.resolveInputToken(html);

		if (this.checkStampGachaStatus) {
			this.checkStampGachaStatus = false;
			this.robot.dispatch("/gacha/stamp-gacha");
			return;
		}

		if (this.checkGift) {
			this.checkGift = false;
			this.robot.dispatch("/gift");
			return;
		}

		if (this.checkEventInfomation) {
			this.checkEventInfomation = false;
			this.robot.dispatch("/event-infomation");
			return;
		}

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
		this.checkGift = true;
		this.quest = true;
		this.battle = true;

		final Properties session = this.robot.getSession();
		final long sleepTime = Long.valueOf(session.getProperty(
				"MypageHandler.sleepTime", "3600000"));
		try {
			Thread.sleep(sleepTime);
		} catch (final InterruptedException e) {
		}
		this.robot.dispatch("/mypage");
	}

	private void printMyInfo(String userStatusHtml) {
		if (this.log.isInfoEnabled()) {
			Matcher userNameMatcher = HTML_USER_NAME_PATTERN
					.matcher(userStatusHtml);
			if (userNameMatcher.find()) {
				String userName = userNameMatcher.group(1);
				this.log.info(String.format("hello, %s", userName));
			}
			Matcher useLevelMatcher = HTML_USER_LEVEL_PATTERN
					.matcher(userStatusHtml);
			if (useLevelMatcher.find()) {
				String userLevel = useLevelMatcher.group(1);
				this.log.info(userLevel);
			}
		}
	}
}
