package robot;

import java.util.Properties;

import common.CommonHttpClient;

public interface Robot {

    public CommonHttpClient getHttpClient();

    public void dispatch(String string);

    public String buildPath(String string);

    public Properties getSession();

}
