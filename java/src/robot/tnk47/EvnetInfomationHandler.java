package robot.tnk47;

import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class EvnetInfomationHandler extends AbstractEventHandler<Robot> {

    public EvnetInfomationHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final String input = this.robot.buildPath("/event/ajax/get-current-event-information");
        final List<BasicNameValuePair> nvps = Collections.emptyList();
        final String html = this.robot.getHttpClient().post(input, nvps);
        final JSONObject currentEventInfomation = JSONObject.fromObject(html);
        final JSONObject data = currentEventInfomation.getJSONObject("data");
        if (data.containsKey("currentEventInfoDto")) {
            final JSONObject currentEventInfoDto = data.getJSONObject("currentEventInfoDto");
            final int rank = currentEventInfoDto.getInt("rank");
            final int score = currentEventInfoDto.getInt("score");
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("获得总分: %d，排名: %d", score, rank));
            }

            final String linkUrl = currentEventInfoDto.getString("linkUrl");
            this.robot.dispatch(linkUrl);
            return;
        }
        this.robot.dispatch("/mypage");
    }

}
