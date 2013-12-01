/**
 * 
 */
package robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

public abstract class AbstractEventHandler<T extends Robot> implements
        EventHandler {
    protected static final Pattern INPUT_TOKEN_PATTERN = Pattern.compile("<input id=\"__token\" type=\"hidden\" value=\"([a-zA-Z0-9]{6})\" data-page-id=\".*\">");

    protected final Log log;
    protected final T robot;

    public AbstractEventHandler(final T robot) {
        this.robot = robot;
        this.log = LogFactory.getLog(this.getClass());
    }

    @Override
    public final void handle() {
        this.before();
        this.handleIt();
        this.after();
    }

    protected void before() {
    }

    protected abstract void handleIt();

    protected void after() {
    }

    protected void resolveInputToken(final String html) {
        final Properties session = this.robot.getSession();
        final Matcher tokenMatcher = AbstractEventHandler.INPUT_TOKEN_PATTERN.matcher(html);
        if (tokenMatcher.find()) {
            final String newToken = tokenMatcher.group(1);
            session.setProperty("token", newToken);
        }
    }

    protected String httpGet(final String path) {
        final String url = this.robot.buildPath(path);
        final String html = this.robot.getHttpClient().get(url);
        this.robot.getHttpClient().setReferer(url);
        return html;
    }

    protected String httpPost(final String path,
                              final List<BasicNameValuePair> nvps) {
        final String url = this.robot.buildPath(path);
        final String html = this.robot.getHttpClient().post(url, nvps);
        this.robot.getHttpClient().setReferer(url);
        return html;
    }

    protected JSONObject httpGetJSON(final String path) {
        final String url = this.robot.buildPath(path);
        final String html = this.robot.getHttpClient().get(url);
        return JSONObject.fromObject(html);
    }

    protected JSONObject httpPostJSON(final String path,
                                      final List<BasicNameValuePair> nvps) {
        final String url = this.robot.buildPath(path);
        final String html = this.robot.getHttpClient().post(url, nvps);
        return JSONObject.fromObject(html);
    }

    protected List<BasicNameValuePair> createNameValuePairs() {
        return new LinkedList<BasicNameValuePair>();
    }
}