package dso.tools.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * The class <code>Helpers</code> contains small functions and constants which
 * are used often, but did not found their own place.
 */
public class Helpers {
	public static final String PREFERENCES_DIR_NAME = "dsotools";
	public static final String PROPERTIES_FILE_NAME = "dsotools.properties.xml";

	private static File preferencesDirFile = null;

	public static File getPreferencesDirFile() {
		if (preferencesDirFile != null)
			return preferencesDirFile;
		String path;
		path = System.getProperty("user.home");
		if (path != null) {
			// using the home-dir under macOSx and linux
			preferencesDirFile = new File(path, "." + PREFERENCES_DIR_NAME);
		} else {
			path = System.getenv("APPDATA");
			if (path != null) {
				// using the folder for application data under win
				preferencesDirFile = new File(path, PREFERENCES_DIR_NAME);
			} else {
				// using the folder from where the application was started
				preferencesDirFile = new File(System.getProperty("user.dir"));
			}
		}
		if (!preferencesDirFile.exists())
			preferencesDirFile.mkdir();
		return preferencesDirFile;
	}

	/**
	 * Returns a properties value from the preferences.
	 * 
	 * @param key
	 *            the key of the property which should be returned.
	 * @return the value of the property.
	 */
	public static String getProperty(String key) {
		Properties properties = new Properties();
		try {
			File propFile = new File(getPreferencesDirFile(),
					PROPERTIES_FILE_NAME);
			if (propFile.exists())
				properties.loadFromXML(new FileInputStream(propFile));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.containsKey(key) ? properties.getProperty(key) : "";
	}

	/**
	 * Sets a property of the preferences.
	 * 
	 * @param key
	 *            the key of the property
	 * @param value
	 *            the value of the property
	 */
	public static void setProperty(String key, String value) {
		Properties properties = new Properties();
		try {
			File propFile = new File(getPreferencesDirFile(),
					PROPERTIES_FILE_NAME);
			if (!propFile.exists())
				propFile.createNewFile();
			else
				properties.loadFromXML(new FileInputStream(propFile));
			properties.setProperty(key, value);
			properties.storeToXML(new FileOutputStream(propFile),
					"properties of the "
							+ Helpers.class.getPackage().toString()
							+ " Application", "UTF-8");
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
