package net.MCAds.advertisements;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Ad_Chat implements Listener {
	public static Main plugin;
	public static String chat;
	public static String refLink;

	public String chat() throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("chat")) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Random randomizer = new Random();
			Ads ads = new Ads();
			String random = ads.ads("chat").get(randomizer.nextInt(ads.ads("chat").size()));
			String xmlFile = Main.getInstance().getDataFolder() + "/cache/" + (random).replace("http://", "").replace("https://", "").replace("/", ",").replace("..", "") + ".xml";
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			refLink = doc.getDocumentElement().getAttribute("reflink");
			NodeList nList = doc.getElementsByTagName("line");
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Main.getInstance().getConfig().getString("scoreboard.first-line")));
			}
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						if (!player.hasPermission("mcads.bypass.chat") && !Main.bypassDisable.contains(player)) {
							player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), eElement.getTextContent()).replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName()).replace("{reflink}", refLink));
						}
					}
				}
			}
			return chat;
		}
		return chat;
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