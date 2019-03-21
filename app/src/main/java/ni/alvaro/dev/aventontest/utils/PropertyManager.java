package ni.alvaro.dev.aventontest.utils;

import android.app.Application;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

    private static final String MAP_KEY = "MAPBOX_KEY";

    private static final String PROP_FILE_NAME = "aventon.properties";
    private static final PropertyManager ourInstance = new PropertyManager();
    private static Properties props;

    public static void initialize(Application application) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = application.getAssets();
        InputStream inputStream = null;
        inputStream = assetManager.open(PROP_FILE_NAME);
        properties.load(inputStream);

        props = properties;

    }

    public static PropertyManager getInstance() {
        if (props == null) throw new RuntimeException("Properties file has not been loaded");
        return ourInstance;
    }


    public Properties getProps(){
        return props;
    }

    public String getSecureMapKey(){
        return props.getProperty(MAP_KEY);
    }

    private PropertyManager() {

    }
}
