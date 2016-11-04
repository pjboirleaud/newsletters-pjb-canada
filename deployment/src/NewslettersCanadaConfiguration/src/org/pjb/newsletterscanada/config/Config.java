package org.pjb.newsletterscanada.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Config {

	private final static String DEFAULT_PATH = "configuration.properties";
	
	private static Config config;
	
	private Properties p = new Properties();
	
	@SuppressWarnings("unused")
	private Config() {
	}
	
	public Config(String path) {
		try {
			p.load(Config.class.getClassLoader().getResourceAsStream(path));
		} catch (Exception e) {
			try {
				p.load(new FileInputStream(new File(path)));
			} catch(Exception e2) {
				throw new RuntimeException("Could not load configuration from class path : " + path, e);
			}
		}
	}
	
	public final static Config getConfig() {
		if(config == null) {
			config = new Config(DEFAULT_PATH);
		}
		return config;
	}
	
	public String getString(String key) {
		return p.getProperty(key);
	}
	
	public String getString(String key, String defaultValue) {
		String value = p.getProperty(key);
		return value == null ? defaultValue : value;
	}
	
	public List<String> getStringList(String key) {
		return getStringList(key, null);
	}
	
	public List<String> getStringList(String key, String defaultValue) {
		String value = getString(key);
		if(value == null) {
			if(defaultValue == null) {
				return null;
			}
			return Arrays.asList(defaultValue.split(",")).stream().map(elt -> elt.trim()).collect(Collectors.toList());
		}
		return Arrays.asList(value.split(",")).stream().map(elt -> elt.trim()).collect(Collectors.toList());
	}
}
