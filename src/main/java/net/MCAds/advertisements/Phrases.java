package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class Phrases implements Listener {
	
	public static File file;
	public static FileConfiguration config;
	public static HashMap<String, Object> configMap = new HashMap<String, Object>();
	
	public static void enable() throws IOException {
		file = new File(Main.dataFolder(), "phrases.yml");
		config = YamlConfiguration.loadConfiguration(file);
		config.options().copyDefaults(true);
		Reader textResource = Main.textResource("phrases.yml");
		config.setDefaults(YamlConfiguration.loadConfiguration(textResource));
		config.save(file);
	}
	
}
