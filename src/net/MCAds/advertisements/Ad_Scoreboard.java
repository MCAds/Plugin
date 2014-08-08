package net.MCAds.advertisements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Ad_Scoreboard implements Listener {
	public static Main plugin;

	public static Scoreboard scoreboard;
	public static Objective objective;
	public static HashMap<OfflinePlayer, Score> scores = new HashMap<OfflinePlayer, Score>();
	public static Scoreboard sBoard;
	public static String refLink;

	public void enable() {

	}

	ScoreboardManager manager;

	public Scoreboard scoreboard() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		if (Main.getInstance().isEnabled("scoreboard")) {
			ScoreboardManager manager = Main.getInstance().getServer().getScoreboardManager();
			Scoreboard scoreboard = manager.getNewScoreboard();
			Objective objective = scoreboard.registerNewObjective(ChatColor.translateAlternateColorCodes("&".charAt(0), Main.getInstance().getConfig().getString("scoreboard.first-line")), "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Random randomizer = new Random();
			Ads ads = new Ads();
			String random = ads.ads("scoreboard").get(randomizer.nextInt(ads.ads("scoreboard").size()));
			String xmlFile = Main.getInstance().getDataFolder() + "/cache/" + (random.replace("http://", "").replace("https://", "").replace("/", ",").replace("..", "")) + ".xml";
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("line");
			refLink = doc.getDocumentElement().getAttribute("reflink");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (eElement.getTextContent().length() <= 16) {
						objective.getScore(ChatColor.translateAlternateColorCodes("&".charAt(0), eElement.getTextContent()).replace("{reflink}", refLink)).setScore(Integer.parseInt(eElement.getAttribute("number")));
					}
				}
			}
			return scoreboard;
		}
		return scoreboard;
	}

	@EventHandler
	public void sbOnJoin(PlayerJoinEvent event) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		if (Main.getInstance().isEnabled("scoreboard")) {
			Player player = event.getPlayer();
			if (!player.hasPermission("mcads.bypass.scoreboard") && !Main.bypassDisable.contains(player)) {
				if (sBoard == null) sBoard = scoreboard();
				player.setScoreboard(sBoard);
			}
		}
	}

	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (Main.getInstance().isEnabled("scoreboard")) {
					try {
						sBoard = scoreboard();
						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							if (!player.hasPermission("mcads.bypass.scoreboard") && !Main.bypassDisable.contains(player)) {
								player.setScoreboard(sBoard);
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