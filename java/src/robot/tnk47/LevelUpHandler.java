package robot.tnk47;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class LevelUpHandler extends AbstractEventHandler<Robot> {

    public LevelUpHandler(final Robot robot) {
        super(robot);
    }

    @Override
    public void handleIt() {
        final Properties session = this.robot.getSession();
        final int staminaLimit = Integer.valueOf(session.getProperty("LevelUpHandler.stamina.limit",
                                                                     "0"));
        final int attackPowerLimit = Integer.valueOf(session.getProperty("LevelUpHandler.power.limit",
                                                                         "0"));
        final int maxStamina = Integer.valueOf(session.getProperty("maxStamina",
                                                                   "0"));
        final int maxPower = Integer.valueOf(session.getProperty("maxPower",
                                                                 "0"));
        int attrPoints = Integer.valueOf(session.getProperty("attrPoints", "0"));

        int attrStaminaP = 0;
        int attrPowerP = 0;
        while (attrPoints > 0) {
            if (staminaLimit > maxStamina + attrStaminaP) {
                attrStaminaP++;
            }
            attrPoints--;
        }
        while (attrPoints > 0) {
            if (attackPowerLimit > maxPower + attrPowerP) {
                attrPowerP++;
            }
            attrPoints--;
        }

        final String input = this.robot.buildPath("/quest/ajax/put-apportion-attr-ability");
        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("attrStaminaP",
                                        String.valueOf(attrStaminaP)));
        nvps.add(new BasicNameValuePair("attrPowerP",
                                        String.valueOf(attrPowerP)));

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
