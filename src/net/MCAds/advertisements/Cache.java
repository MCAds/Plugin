package net.MCAds.advertisements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.xml.sax.SAXException;

public class Cache implements Listener {
	
	public static String[] types = { "bossbar", "scoreboard", "chat",
			"hologram" };
	
	public void create() throws ParserConfigurationException, IOException,
			SAXException {
		Ads ads = new Ads();
		File file = new File(Main.getInstance().getDataFolder() + "/cache");
		if (!file.exists()) {
			file.mkdir();
		}
		ArrayList<String> urls = new ArrayList<String>();
		for (String type : types) {
			featured(type);
			for (String ad : ads.adList(type)) {
				urls.add(ad);
				URL website;
				if (ad.contains("?")) {
					website = new URL(ad
							+ "&paypal="
							+ Main.getInstance().getConfig()
									.getString("paypal"));
				} else {
					website = new URL(ad
							+ "?paypal="
							+ Main.getInstance().getConfig()
									.getString("paypal"));
				}
				HttpURLConnection httpcon = (HttpURLConnection) website
						.openConnection();
				httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
				ReadableByteChannel rbc = Channels.newChannel(httpcon
						.getInputStream());
				String uid = ad.replace("http://", "").replace("https://", "")
						.replace("..", "");
				String uidFileString = Main.getInstance().getDataFolder()
						+ "/cache/ads/" + uid;
				uidFileString = uidFileString.substring(0,
						uidFileString.lastIndexOf("/"));
				File uidFile = new File(uidFileString);
				uidFile.mkdirs();
				FileOutputStream fos = new FileOutputStream(Main.getInstance()
						.getDataFolder() + "/cache/ads/" + uid + ".xml");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
				rbc.close();
			}
		}
		File collectionsFile = new File(Main.getInstance().getDataFolder()
				+ "/cache/collections");
		if (!collectionsFile.exists()) {
			collectionsFile.mkdir();
		}
		File adsFile = new File(Main.getInstance().getDataFolder() + "/ads"
				+ ".yml");
		YamlConfiguration adsConfig = YamlConfiguration
				.loadConfiguration(adsFile);
		List<String> collections = new ArrayList<String>();
		collections = adsConfig.getStringList("collections");
		for (String collection : collections) {
			URL imageUrl = new URL(collection);
			HttpURLConnection httpcon = (HttpURLConnection) imageUrl
					.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
			ReadableByteChannel rbc = Channels.newChannel(httpcon
					.getInputStream());
			String uid = collection.replace("http://", "")
					.replace("https://", "").replace("..", "");
			String uidFileString = Main.getInstance().getDataFolder()
					+ "/cache/collections/" + uid;
			uidFileString = uidFileString.substring(0,
					uidFileString.lastIndexOf("/"));
			File uidFile = new File(uidFileString);
			uidFile.mkdirs();
			FileOutputStream fos = new FileOutputStream(Main.getInstance()
					.getDataFolder() + "/cache/collections/" + uid);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		}
	}
	
	public static void featured(String type) throws IOException {
		if (Main.getInstance().getConfig().getBoolean("featured")) {
			File file = new File(Main.getInstance().getDataFolder()
					+ "/cache/featured");
			if (!file.exists()) {
				file.mkdir();
			}
			URL featuredUrl = new URL("http://mcads.net/featured/" + type
					+ ".xml");
			HttpURLConnection httpcon = (HttpURLConnection) featuredUrl
					.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
			ReadableByteChannel rbc = Channels.newChannel(httpcon
					.getInputStream());
			FileOutputStream fos = new FileOutputStream(Main.getInstance()
					.getDataFolder() + "/cache/featured/" + type + ".xml");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		}
	}
	
	public static void image(String urlString) throws IOException {
		File file = new File(Main.getInstance().getDataFolder()
				+ "/cache/images");
		if (!file.exists()) {
			file.mkdir();
		}
		URL imageUrl = new URL(urlString);
		HttpURLConnection httpcon = (HttpURLConnection) imageUrl
				.openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
		ReadableByteChannel rbc = Channels.newChannel(httpcon.getInputStream());
		String uid = urlString.replace("http://", "").replace("https://", "")
				.replace("..", "");
		String uidFileString = Main.getInstance().getDataFolder()
				+ "/cache/images/" + uid;
		uidFileString = uidFileString.substring(0,
				uidFileString.lastIndexOf("/"));
		File uidFile = new File(uidFileString);
		uidFile.mkdirs();
		FileOutputStream fos = new FileOutputStream(Main.getInstance()
				.getDataFolder() + "/cache/images/" + uid);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	public void delete() {
		File file = new File(Main.getInstance().getDataFolder() + "/cache");
		deleteDirectory(file);
	}
	
	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	public void timer() {
		Main.getInstance().getServer().getScheduler()
				.scheduleSyncRepeatingTask(
						Main.getInstance(),
						new Runnable() {
							public void run() {
								try {
									delete();
									create();
									for (String type : types) {
										featured(type);
									}
								} catch (ParserConfigurationException
										| IOException | SAXException e) {
									e.printStackTrace();
								}
							}
						},
						Main.getInstance().getConfig().getInt("cache-expiry") * 20 * 60 * 60,
						Main.getInstance().getConfig().getInt("cache-expiry") * 20 * 60 * 60);
	}
	
}