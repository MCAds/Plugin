package net.MCAds.advertisements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DependencyDownloader {
	
	public static void download() {
		boolean holoAPI = false;
		boolean barAPI = false;
		String[] pluginsList = new File("plugins").list();
		for (String file : pluginsList) {
			// HoloAPI is most likely installed
			if (file.contains("HoloAPI")) {
				holoAPI = true;
			}
			// BarAPI is most likely installed
			if (file.contains("BarAPI")) {
				barAPI = true;
			}
		}
		
		// Download HoloAPI
		if (holoAPI == false) {
			try {
				downloadFile("http://dev.bukkit.org/media/files/787/363/HoloAPI-v1.2.3.jar", "plugins/HoloAPI.jar");
				Main.server().getPluginManager().loadPlugin(new File("plugins/HoloAPI.jar"));
				Main.server().reload();
			} catch (Exception e) {
				System.out.println("Downloading the dependency \"HoloAPI\" failed. please download it manually.");
			}
		}
		
		// Download BarAPI
		if (barAPI == false) {
			try {
				downloadFile("http://dev.bukkit.org/media/files/785/999/BarAPI.jar", "plugins/BarAPI.jar");
				Main.server().getPluginManager().loadPlugin(new File("plugins/BarAPI.jar"));
				Main.server().reload();
			} catch (Exception e) {
				System.out.println("Downloading the dependency \"BarAPI\" failed. please download it manually.");
			}
		}
	}
	
	public static void downloadFile(String link, String directory) throws IOException {
		URL url = new URL(link);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(directory);
		
		byte[] b = new byte[2048];
		int length;
		
		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}
		
		is.close();
		os.close();
	}
	
}
