package robot.tnk47;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class UseItemHandler extends AbstractEventHandler<Robot> {

    public UseItemHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final Properties session = this.robot.getSession();
        final String itemId = session.getProperty("itemId");
        final String token = session.getProperty("token");
        final String name = session.getProperty("name");

        if (this.log.isInfoEnabled()) {
            this.log.info("药不能停！吃了一个" + name);
        }
        final String input = this.robot.buildPath("/item/ajax/put-item-use");
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("itemId", itemId));
        nvps.add(new BasicNameValuePair("token", token));

        final String html = this.robot.getHttpClient().post(input, nvps);

        if (this.log.isDebugEnabled()) {
            this.log.debug(html);
        }

        final JSONObject jsonResponse = JSONObject.fromObject(html);
        final String newToken = jsonResponse.getString("token");

        session.put("token", newToken);

        final String callback = session.getProperty("callback");
        this.robot.dispatch(callback);
    }

}
