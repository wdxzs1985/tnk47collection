package robot.tnk47.battle;

import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class BattleSupportHandler extends AbstractEventHandler<Robot> {

    public BattleSupportHandler(final Robot robot) {
        super(robot);
    }

    @Override
    protected void handleIt() {
        final Properties session = this.robot.getSession();
        final String supportUserId = session.getProperty("supportUserId");
        final String token = session.getProperty("token");
        final String path = "/battle/ajax/put-battle-support";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("supportUserId", supportUserId));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        if (this.log.isInfoEnabled()) {
            final JSONObject data = jsonResponse.getJSONObject("data");
            final String supportUserName = data.getString("supportUserName");
            this.log.info(String.format("给%s发送了应援", supportUserName));
        }

        this.robot.dispatch("/battle");
    }
}
