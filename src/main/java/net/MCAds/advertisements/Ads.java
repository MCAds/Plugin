package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Ads implements Listener {
	
	public static ArrayList<String> ads = new ArrayList<String>();
	public static String adRandom;
	public static String refLink;
	public HashMap<Integer, String> lines = new HashMap<Integer, String>();
	public HashMap<Integer, String> images = new HashMap<Integer, String>();
	public static String firstLine;
	public static ArrayList<UUID> hidden = new ArrayList<UUID>();
	public static String image;
	public static int imageHeight;
	public File adsFile;
	public FileConfiguration adsConfig;
	
	public ArrayList<String> adList(String type) throws ParserConfigurationException, IOException, SAXException {
		ads.clear();
		if (Main.getInstance().getConfig().getBoolean("featured")) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			String xmlFile = Main.getInstance().getDataFolder() + "/cache/featured/" + type + ".xml";
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(type);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ads.add(eElement.getTextContent());
					System.out.println(eElement.getTextContent());
				}
			}
		}
		adsFile = new File(Main.getInstance().getDataFolder() + "/ads" + ".yml");
		adsConfig = YamlConfiguration.loadConfiguration(adsFile);
		List<String> userAds = adsConfig.getStringList(type);
		for (String ad : userAds) {
			ads.add(ad);
		}
		return ads;
	}
	
	public void ad(String type, String tag) throws SAXException, ParserConfigurationException, IOException {
		Random randomizer = new Random();
		String random = adList(type).get(randomizer.nextInt(adList(type).size()));
		firstLine = Phrases.config.getString("first-line." + type);
		try {
			lines.clear();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			String xmlFile = Main.getInstance().getDataFolder() + "/cache/ads/" + (random).replace("http://", "").replace("https://", "").replace("..", "") + ".xml";
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			refLink = doc.getDocumentElement().getAttribute("reflink");
			if (Main.getInstance().getConfig().getBoolean("images")) {
				NodeList nodeList = doc.getElementsByTagName("image");
				for (int temp = 0; temp < nodeList.getLength(); temp++) {
					Node nNode = nodeList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						File imageFile = new File(Main.getInstance().getDataFolder() + "/cache/images/" + eElement.getTextContent().replace("http://", "").replace("https://", "").replace("..", ""));
						if (!imageFile.exists()) {
							Cache.image(eElement.getTextContent());
						}
						if (type == "chat") {
							if (eElement.hasAttribute("height")) {
								imageHeight = Integer.parseInt(eElement.getAttribute("height"));
							} else {
								imageHeight = 8;
							}
							image = Main.getInstance().getDataFolder() + "/cache/images/" + eElement.getTextContent().replace("http://", "").replace("https://", "").replace("..", "");
						}
						if (type == "hologram") {
							if (eElement.hasAttribute("height")) {
								lines.put(Integer.parseInt(eElement.getAttribute("height")), "image:" + Main.getInstance().getDataFolder() + "/cache/images/" + eElement.getTextContent().replace("http://", "").replace("https://", "").replace("..", ""));
							} else {
								lines.put(8, "image:" + Main.getInstance().getDataFolder() + "/cache/images/" + eElement.getTextContent().replace("http://", "").replace("https://", "").replace("..", ""));
							}
						}
					}
				}
			}
			NodeList nList = doc.getElementsByTagName(tag);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (eElement.hasAttribute("number")) {
						lines.put(Integer.parseInt(eElement.getAttribute("number")), eElement.getTextContent());
					} else {
						lines.put(temp, eElement.getTextContent());
					}
				}
			}
		} catch (Exception e) {
			lines.clear();
			System.out.println("There is an error with the ad file at " + random);
			if (type == "scoreboard") {
				int number = Phrases.config.getStringList("ad_error_scoreboard").size();
				for(String line : Phrases.config.getStringList("ad_error_scoreboard")){
					number--;
					lines.put(number, line);
				}
			} else {
				lines.put(1, Phrases.config.getString("ad_error"));
			}
		}
	}
	
	public void collections(String collection, String type) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		URL url = new URL(collection);
		URLConnection httpcon = url.openConnection();
		httpcon.setRequestProperty("User-Agent", "Mozilla/4.0");
		InputStream stream = httpcon.getInputStream();
		Document doc = dBuilder.parse(stream);
		stream.close();
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName(type);
		System.out.println(type);
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				ads.add(eElement.getTextContent());
			}
		}
	}
	
	public void config() throws IOException {
		adsFile = new File(Main.getInstance().getDataFolder() + "/ads.yml");
		adsConfig = YamlConfiguration.loadConfiguration(adsFile);
		if (!adsFile.exists()) {
			for (String type : Cache.types) {
				adsConfig.set(type, Arrays.asList("http://mcads.net/examples/" + type + "/1.xml"));
			}
			ArrayList<String> collections = new ArrayList<String>();
			collections.add("http://mcads.net/examples/collections/1.xml");
			adsConfig.set("collections", Arrays.asList(collections));
		}
		adsConfig.save(adsFile);
	}
	
}