package robot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.CommonHttpClient;

public abstract class AbstractRobot implements Robot, Runnable {

    protected final Log log;
    private final String host;
    private final CommonHttpClient httpClient;
    private final File cookieFile;
    private EventHandler nextHandler = null;
    private final Map<String, EventHandler> handlerMapping;
    private final Properties session;

    public AbstractRobot(final String host, final String setup) {
        this.host = host;
        this.log = LogFactory.getLog(this.getClass());

        InputStream inputConfig = null;
        this.session = new Properties();
        try {
            inputConfig = FileUtils.openInputStream(new File(setup));
            this.session.load(inputConfig);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputConfig);
        }
        final String username = this.session.getProperty("LoginHandler.username");
        this.cookieFile = new File(username + ".cookie");
        this.httpClient = new CommonHttpClient();
        this.httpClient.loadCookie(this.cookieFile);
        this.handlerMapping = new HashMap<String, EventHandler>();
    }

    protected void registerHandler(final String path, final EventHandler handler) {
        this.handlerMapping.put(path, handler);
    }

    @Override
    public void run() {
        this.dispatch("/");
        try {
            while (this.nextHandler != null) {
                final EventHandler currEventHandler = this.nextHandler;
                this.nextHandler = null;
                currEventHandler.handle();
                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                }
            }
        } catch (final Exception e) {
            this.log.error("发生异常退出", e);
        }
        this.httpClient.saveCookie(this.cookieFile);
    }

    @Override
    public void dispatch(final String event) {
        this.nextHandler = this.handlerMapping.get(event);
        if (this.nextHandler == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("未知方法[%s]，返回主页", event));
            }
            this.dispatch("/mypage");
        }
    }

    @Override
    public String buildPath(final String path) {
        return this.host + path;
    }

    @Override
    public CommonHttpClient getHttpClient() {
        return this.httpClient;
    }

    @Override
    public Properties getSession() {
        return this.session;
    }
}
