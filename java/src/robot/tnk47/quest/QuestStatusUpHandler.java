package robot.tnk47.quest;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class QuestStatusUpHandler extends AbstractEventHandler<Robot> {

    public QuestStatusUpHandler(final Robot robot) {
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
                attrPoints--;
            } else {
                break;
            }
        }
        while (attrPoints > 0) {
            if (attackPowerLimit > maxPower + attrPowerP) {
                attrPowerP++;
                attrPoints--;
            } else {
                break;
            }
        }

        if (attrStaminaP > 0 && attrPowerP > 0) {
            final String path = "/quest/ajax/put-apportion-attr-ability";
            final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("attrStaminaP",
                                            String.valueOf(attrStaminaP)));
            nvps.add(new BasicNameValuePair("attrPowerP",
                                            String.valueOf(attrPowerP)));

            this.httpPost(path, nvps);

            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("增加了%d体力，增加了%d攻防",
                                            attrStaminaP,
                                            attrPowerP));
            }
        }

        final String callback = session.getProperty("callback");
        this.robot.dispatch(callback);
    }

}