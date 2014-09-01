package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.mcstats.Metrics;

public class Main extends JavaPlugin implements Listener {
	public final Ad_Bossbar bbAd = new Ad_Bossbar();
	public final Ad_Scoreboard sbAd = new Ad_Scoreboard();
	public final Ad_Chat chatAd = new Ad_Chat();
	public final Ad_Hologram hgAd = new Ad_Hologram();
	public final Cache cache = new Cache();
	public final Ads ads = new Ads();
	public final Commands commands = new Commands();
	private static Main instance;
	public Scoreboard scoreboard;
	public static Plugin plugin;
	public final static String[] types = { "bossbar", "scoreboard", "chat", "hologram" };
	
	@Override
	public void onEnable() {
		plugin = this;
		instance = this;
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		Reader textResource = getTextResource("config.yml");
		getConfig().setDefaults(YamlConfiguration.loadConfiguration(textResource));
		saveConfig();
		registerEvents(this, new Ad_Scoreboard(), new Ad_Bossbar(), new Ads(), new Ad_Chat(), new Ad_Hologram());
		this.getServer().getPluginManager().registerEvents(hgAd, this);
		this.getCommand("mcads").setExecutor(new Commands());
		this.getCommand("getlink").setExecutor(new Commands());
		try {
			Phrases.enable();
			ads.config();
			cache.delete();
			cache.create();
			cache.timer();
			if (isEnabled("scoreboard")) sbAd.timer(this);
			if (isEnabled("bossbar")) bbAd.timer(this);
			if (isEnabled("chat")) chatAd.timer(this);
			if (isEnabled("hologram")) {
				hgAd.timer(this);
				hgAd.enable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		cache.delete();
		
		// Prevents memory leaks on /reload
		plugin = null;
	}
	
	// Used for JavaPlugin methods in Listener classes
	public static Main instance() {
		return instance;
	}
	
	// Some methods to use in other classes
	public static String version() {
		PluginDescriptionFile pdf = instance.getDescription();
		return pdf.getVersion();
	}
	
	public static FileConfiguration config() {
		return instance.getConfig();
	}
	
	public static File dataFolder() {
		return instance.getDataFolder();
	}
	
	public static Server server() {
		return instance.getServer();
	}
	
	public static Reader textResource(String string){
		return instance().getTextResource(string);
	}
	
	public static boolean isEnabled(String location) {
		if (config().getBoolean(location + ".enabled")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
	
}