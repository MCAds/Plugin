package net.MCAds.advertisements;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.xml.sax.SAXException;

public class Ad_Chat implements Listener {
	public static Main plugin;
	public static String chat;
	public static String refLink;

	public void chat() throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("chat")) {
			Ads ads = new Ads();
			ads.ad("chat", "line");
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (!player.hasPermission("mcads.bypass.chat") || !Ads.hidden.contains(player.getUniqueId())) {
					player.sendMessage(Ads.firstLine);
					for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
						player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()).replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName()));
					}
				}
			}
		}
	}

	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				try {
					chat();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, Main.getInstance().getConfig().getInt("chat.delay") * 20, Main.getInstance().getConfig().getInt("chat.delay") * 20);
	}

}