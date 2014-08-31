package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class Phrases implements Listener {
	
	public static File file;
	public static FileConfiguration config;
	
	public static void enable() throws IOException {
		file = new File(Main.getInstance().getDataFolder(), "phrases.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			Main.getInstance().saveResource("phrases.yml", false);
		}
	}
}
