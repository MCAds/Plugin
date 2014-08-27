package net.MCAds.advertisements;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.xml.sax.SAXException;

public class Ad_Bossbar implements Listener {
	public static Main plugin;
	public static String bBar;
	public static String bossbar;
	public static String refLink;
	
	public void bossbar() throws ParserConfigurationException, IOException,
			SAXException {
		if (Main.getInstance().isEnabled("bossbar")) {
			Ads ads = new Ads();
			ads.ad("bossbar", "line");
			for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
				bossbar = ChatColor.translateAlternateColorCodes("&".charAt(0),
						Ads.firstLine + line.getValue());
			}
		}
	}
	
	@EventHandler
	public void bbOnPlayerJoin(PlayerJoinEvent event)
			throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("bossbar")) {
			Player player = event.getPlayer();
			if (!player.hasPermission("mcads.bypass.bossbar")
					|| !Ads.hidden.contains(player.getUniqueId())) {
				if (bossbar == null) {
					bossbar();
				} else {
				}
				BarAPI.setMessage(
						player,
						bossbar.replace("{name}", player.getName()).replace(
								"{displayname}", player.getDisplayName()),
						Main.getInstance().getConfig().getInt("bossbar.delay"));
			}
		}
	}
	
	public void timer(Main plugin) throws IOException,
			ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler()
				.scheduleSyncRepeatingTask(
						plugin,
						new Runnable() {
							public void run() {
								if (Main.getInstance().isEnabled("bossbar")) {
									try {
										bossbar();
										for (Player player : Bukkit.getServer()
												.getOnlinePlayers()) {
											if (!player
													.hasPermission("mcads.bypass.bossbar")
													|| !Ads.hidden.contains(player
															.getUniqueId())) {
												BarAPI.setMessage(
														player,
														bossbar.replace(
																"{name}",
																player.getName())
																.replace(
																		"{displayname}",
																		player.getDisplayName()),
														Main.getInstance()
																.getConfig()
																.getInt("bossbar.delay"));
											}
										}
									} catch (IOException
											| ParserConfigurationException
											| SAXException e) {
										e.printStackTrace();
									}
								}
							}
						},
						Main.getInstance().getConfig().getInt("bossbar.delay") * 20,
						Main.getInstance().getConfig().getInt("bossbar.delay") * 20);
	}
	
}