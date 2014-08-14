package net.MCAds.advertisements;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

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
	
	@Override
	public void onEnable() {
		plugin = this;
		instance = this;
		saveDefaultConfig();
		registerEvents(this, new Ad_Scoreboard(), new Ad_Bossbar(), new Ads(), new Ad_Chat(), new Ad_Hologram());
		this.getServer().getPluginManager().registerEvents(hgAd, this);
		this.getCommand("mcads").setExecutor(new Commands());
		this.getCommand("getlink").setExecutor(new Commands());
		try {
			ads.config();
			cache.delete();
			cache.create();
			cache.timer();
			if (isEnabled("scoreboard")) sbAd.timer(this);
			if (isEnabled("bossbar")) bbAd.timer(this);
			if (isEnabled("chat")) chatAd.timer(this);
			if (isEnabled("hologram")) {
				hgAd.timer(this);
				hgAd.load();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		try {
			hgAd.saveAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cache.delete();
		plugin = null;
	}

	public boolean isEnabled(String location) {
		if (this.getConfig().getBoolean(location + ".enabled")) {
//			if (location == "hologram") {
//				if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
//					getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
//					getLogger().severe("*** Download HolographicDisplays from http://dev.bukkit.org/bukkit-plugins/holographic-displays/ ***");
//					getLogger().severe("*** Holographic ads have been disabled ***");
//					return false;
//				} else {
//					return true;
//				}
//			}
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

	public static Main getInstance() {
		return instance;
	}

}