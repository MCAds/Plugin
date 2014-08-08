package net.MCAds.advertisements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.event.Listener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Ads implements Listener {

	public static ArrayList<String> ads = new ArrayList<String>();
	public static String adRandom;

	public ArrayList<String> ads(String type) throws ParserConfigurationException, IOException, SAXException {
		ads = new ArrayList<String>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(Cache.featured(type));
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("feature");
		ArrayList<String> ads = new ArrayList<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				ads.add(eElement.getTextContent());
			}
		}
		List<String> userAds = Main.getInstance().getConfig().getStringList(type + ".xml-files");
		for (String ad : userAds) {
			ads.add(ad);
		}
		return ads;
	}

}
