package bitalkclientfx.socket;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */
public class Constants {

    public final int DEFAULT_PORT = 2018;
    public final String DEFAULT_HOST = "127.0.0.1";

    private Constants() {}

    private static class LazyInstance {
        private static final Constants INSTANCE = new Constants();
    }

    public static Constants instance() {
        return LazyInstance.INSTANCE;
    }

}
