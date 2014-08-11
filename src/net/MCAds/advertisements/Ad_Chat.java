package net.MCAds.advertisements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.xml.sax.SAXException;

import com.bobacadodl.imgmessage.ImageChar;
import com.bobacadodl.imgmessage.ImageMessage;

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
					if(Ads.image != null){
						File file = new File(Ads.image);
						BufferedImage imageToSend = ImageIO.read(file);
						ImageMessage imageMessage = new ImageMessage(imageToSend, Ads.imageHeight, ImageChar.MEDIUM_SHADE.getChar());							
						ArrayList<String> lines = new ArrayList<String>();
						for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
							lines.add(ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()).replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName()));
						}
						imageMessage.appendText(lines.toArray(new String[lines.size()]));
						imageMessage.sendToPlayer(player);	
					}else{
						for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
							player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()).replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName()));
						}
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