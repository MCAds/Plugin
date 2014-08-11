package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.ChatColor;
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
	public static String firstLine;
	public static ArrayList<UUID> hidden = new ArrayList<UUID>();
	public static String image;
	public static int imageHeight;

	public ArrayList<String> ads(String type) throws ParserConfigurationException, IOException, SAXException {
		ads.clear();
		if (Main.getInstance().getConfig().getBoolean("featured")) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			String xmlFile = Main.getInstance().getDataFolder() + "/cache/featured/" + type + ".xml";
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("feature");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ads.add(eElement.getTextContent());
				}
			}
		}
		List<String> userAds = Main.getInstance().getConfig().getStringList(type + ".xml-files");
		for (String ad : userAds) {
			ads.add(ad);
		}
		return ads;
	}

	public void ad(String type, String tag) throws ParserConfigurationException, IOException, SAXException {
		lines.clear();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Random randomizer = new Random();
		String random = ads(type).get(randomizer.nextInt(ads(type).size()));
		String xmlFile = Main.getInstance().getDataFolder() + "/cache/" + (random).replace("http://", "").replace("https://", "").replace("/", ",").replace("..", "") + ".xml";
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		refLink = doc.getDocumentElement().getAttribute("reflink");
		firstLine = ChatColor.translateAlternateColorCodes("&".charAt(0), Main.getInstance().getConfig().getString(type + ".first-line"));
		if(type=="chat" && Main.getInstance().getConfig().getBoolean("images")){
			NodeList nodeList = doc.getElementsByTagName("image");
			for (int temp = 0; temp < nodeList.getLength(); temp++) {
				Node nNode = nodeList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
						File imageFile = new File(Main.getInstance().getDataFolder() + "/cache/images/" + eElement.getTextContent().replace("http://", "").replace("https://", "").replace("/", ",").replace("..", ""));
						if(!imageFile.exists()){
							Cache.image(eElement.getTextContent());
						}
						if (eElement.hasAttribute("height")) {
							imageHeight = Integer.parseInt(eElement.getAttribute("height"));
						} else {
							imageHeight = 8;
						}
						image = Main.getInstance().getDataFolder() + "/cache/images/" + eElement.getTextContent().replace("http://", "").replace("https://", "").replace("/", ",").replace("..", "");
				}
			} 
		}
		NodeList nList = doc.getElementsByTagName(tag);
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (eElement.hasAttribute("number")) {
					lines.put(Integer.parseInt(eElement.getAttribute("number")), ChatColor.translateAlternateColorCodes("&".charAt(0), eElement.getTextContent()));
				} else {
					lines.put(temp, eElement.getTextContent());
				}
			}
		}
	}
}
