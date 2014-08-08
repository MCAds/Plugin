package net.MCAds.advertisements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R3.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	public final Ad_Bossbar bbAd = new Ad_Bossbar();
	public final Ad_Scoreboard sbAd = new Ad_Scoreboard();
	public final Ad_Chat chatAds = new Ad_Chat();
	public final Ad_Hologram hgAds = new Ad_Hologram();
	public final Cache cache = new Cache();
	public final Ads ads = new Ads();
	public final Commands commands = new Commands();
	private static Main instance;
	public Scoreboard scoreboard;
	public static List<Player> bypassDisable = new ArrayList<Player>();
	public static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		instance = this;
		saveDefaultConfig();
		registerEvents(this, new Ad_Scoreboard(), new Ad_Bossbar(), new Ads(), new Ad_Chat(), new Ad_Hologram());
		this.getCommand("mcads").setExecutor(new Commands());
		this.getCommand("getlink").setExecutor(new Commands());
		try {
			cache.delete();
			cache.create();
			cache.timer();
			if (isEnabled("scoreboard")) sbAd.timer(this);
			if (isEnabled("bossbar")) bbAd.timer(this);
			if (isEnabled("chat")) chatAds.timer(this);
			if (isEnabled("hologram")) {
				Ad_Hologram.load();
				hgAds.timer(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		cache.delete();
		if (isEnabled("hologram")) {
			try {
				Ad_Hologram.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		plugin = null;
	}

	public boolean isEnabled(String location) {
		if (this.getConfig().getBoolean(location + ".enabled")) {
			if (location == "bossbar") {
				if (!Bukkit.getPluginManager().isPluginEnabled("BarAPI")) {
					getLogger().severe("*** BarAPI is not installed or not enabled. ***");
					getLogger().severe("*** Download BarAPI from http://dev.bukkit.org/bukkit-plugins/bar-api/ ***");
					getLogger().severe("*** Bossbar ads have been disabled ***");
					return false;
				} else {
					return true;
				}
			}
			if (location == "hologram") {
				if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
					getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
					getLogger().severe("*** Download HolographicDisplays from http://dev.bukkit.org/bukkit-plugins/holographic-displays/ ***");
					getLogger().severe("*** Holographic ads have been disabled ***");
					return false;
				} else {
					return true;
				}
			}
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