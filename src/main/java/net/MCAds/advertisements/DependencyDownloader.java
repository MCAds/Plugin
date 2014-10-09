package net.MCAds.advertisements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DependencyDownloader {
	
	public static void download() {
		File holoAPIDirectory = new File("plugins/HoloAPI*.jar");
		if (!holoAPIDirectory.exists()) {
			try {
				downloadFile("http://dev.bukkit.org/media/files/787/363/HoloAPI-v1.2.3.jar", "plugins/HoloAPI.jar");
			} catch (IOException e) {
				System.out.println("Downloading the dependency \"HoloAPI\" failed. please download it manually.");
			}
		}
		
		File barAPIDirectory = new File("plugins/BarAPI*.jar");
		if (!barAPIDirectory.exists()) {
			try {
				downloadFile("http://dev.bukkit.org/media/files/785/999/BarAPI.jar", "plugins/BarAPI.jar");
			} catch (IOException e) {
				System.out.println("Downloading the dependency \"HoloAPI\" failed. please download it manually.");
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
