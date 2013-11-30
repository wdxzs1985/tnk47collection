/**
 * 
 */
package robot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractEventHandler<T extends Robot> implements
        EventHandler {

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
}
