package net.MCAds.advertisements;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Ad_Bossbar implements Listener {
	public static Main plugin;
	public static String bBar;
	public static String bossbar;
	public static String refLink;

	public String bossbar() throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("bossbar")) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Random randomizer = new Random();
			Ads ads = new Ads();
			String random = ads.ads("bossbar").get(randomizer.nextInt(ads.ads("bossbar").size()));
			String xmlFile = Main.getInstance().getDataFolder() + "/cache/" + (random.replace("http://", "").replace("https://", "").replace("/", ",").replace("..", "")) + ".xml";
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("title");
			refLink = doc.getDocumentElement().getAttribute("reflink");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					bossbar = ChatColor.translateAlternateColorCodes("&".charAt(0), Main.getInstance().getConfig().getString("bossbar.before-ad") + ChatColor.RESET + eElement.getTextContent());
				}
			}
			return bossbar;
		}
		return bossbar;
	}

	@EventHandler
	public void bbOnPlayerJoin(PlayerJoinEvent event) throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("bossbar")) {
			Player player = event.getPlayer();
			if (!player.hasPermission("mcads.bypass.bossbar") && !Main.bypassDisable.contains(player)) {
				if (bBar == null) {
					bBar = bossbar();
				} else {
				}
				BarAPI.setMessage(player, bBar.replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName()).replace("{reflink}", refLink), Main.getInstance().getConfig().getInt("bossbar.delay"));
			}
		}
	}

	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (Main.getInstance().isEnabled("bossbar")) {
					try {
						bBar = bossbar();
					} catch (IOException | ParserConfigurationException | SAXException e) {
						e.printStackTrace();
					}
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						if (!player.hasPermission("mcads.bypass.bossbar") && !Main.bypassDisable.contains(player)) {
							BarAPI.setMessage(player, bBar.replace("{name}", player.getName()).replace("{displayname}", player.getDisplayName()).replace("{reflink}", refLink), Main.getInstance().getConfig().getInt("bossbar.delay"));
						}
					}
				}
			}
		}, Main.getInstance().getConfig().getInt("bossbar.delay") * 20, Main.getInstance().getConfig().getInt("bossbar.delay") * 20);
	}

}