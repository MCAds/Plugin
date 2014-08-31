package net.MCAds.advertisements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.xml.sax.SAXException;

public class Ad_Scoreboard implements Listener {
	public static Main plugin;
	
	public static Scoreboard scoreboard;
	public static Objective objective;
	public static HashMap<OfflinePlayer, Score> scores = new HashMap<OfflinePlayer, Score>();
	public static Scoreboard sBoard;
	public static String refLink;
	private HashMap<Integer, String> objectives = new HashMap<Integer, String>();
	private ScoreboardManager manager;
	
	public void enable() {
		
	}
	
	public HashMap<Integer, String> scoreboard() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		if (Main.getInstance().isEnabled("scoreboard")) {
			objectives.clear();
			Ads ads = new Ads();
			ads.ad("scoreboard", "line");
			manager = Main.getInstance().getServer().getScoreboardManager();
			scoreboard = manager.getNewScoreboard();
			objective = scoreboard.registerNewObjective(ChatColor.translateAlternateColorCodes("&".charAt(0), Ads.firstLine), "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
				if (line.getValue().length() <= 16) {
					objectives.put(line.getKey(), ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()));
				}
			}
			refLink = Ads.refLink;
			return objectives;
		}
		return objectives;
	}
	
	@EventHandler
	public void sbOnJoin(PlayerJoinEvent event) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		if (Main.getInstance().isEnabled("scoreboard")) {
			if (objectives.isEmpty()) scoreboard();
			Player player = event.getPlayer();
			if (!player.hasPermission("mcads.bypass.scoreboard") || !Ads.hidden.contains(player.getUniqueId())) {
				for (Map.Entry<Integer, String> obj : objectives.entrySet()) {
					String message = obj.getValue().replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName());
					if (message.length() >= 16) {
						objective.getScore(message.substring(0, 16)).setScore(obj.getKey());
					} else {
						objective.getScore(message).setScore(obj.getKey());
					}
				}
				player.setScoreboard(scoreboard);
				objectives.clear();
				scoreboard();
			}
		}
	}
	
	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (Main.getInstance().isEnabled("scoreboard")) {
					try {
						scoreboard();
						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							if (!player.hasPermission("mcads.bypass.scoreboard") || !Ads.hidden.contains(player.getUniqueId())) {
								for (Map.Entry<Integer, String> obj : objectives.entrySet()) {
									String message = obj.getValue().replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName());
									if (message.length() >= 16) {
										objective.getScore(message.substring(0, 16)).setScore(obj.getKey());
									} else {
										objective.getScore(message).setScore(obj.getKey());
									}
								}
								player.setScoreboard(scoreboard);
								objectives.clear();
								scoreboard();
							}
						}
					} catch (IOException | ParserConfigurationException | SAXException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, Main.getInstance().getConfig().getInt("scoreboard.delay") * 20, Main.getInstance().getConfig().getInt("scoreboard.delay") * 20);
	}
}