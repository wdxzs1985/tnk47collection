package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleInviteHandler extends AbstractEventHandler<Robot> {

    public BattleInviteHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String token = session.getProperty("token");
        final String path = "/battle/ajax/put-prefecture-battle-invite";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        if (this.log.isInfoEnabled()) {
            final JSONObject data = jsonResponse.getJSONObject("data");
            final String resultMessage = data.getString("resultMessage");
            this.log.info(resultMessage);
        }

        this.robot.dispatch("/battle");
    }
}
