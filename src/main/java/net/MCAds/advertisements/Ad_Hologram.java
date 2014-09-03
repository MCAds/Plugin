package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.xml.sax.SAXException;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.protocol.Action;

public class Ad_Hologram implements Listener {
	public static String refLink;
	
	public static void create(Location location) throws ParserConfigurationException, IOException, SAXException {
		if (Main.isEnabled("hologram")) {
			Hologram hologram = new HologramFactory(Main.plugin).withLocation(location).withText("").build();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (Ads.hidden.contains(player.getUniqueId())) {
					hologram.clear(player);
				}
			}
			update(hologram);
		}
	}
	
	public static void update(Hologram hologram) throws ParserConfigurationException, IOException, SAXException {
		if (Main.isEnabled("hologram")) {
			Ads ads = new Ads();
			ads.ad("hologram", "line");
			String id = hologram.getSaveId();
			HoloAPI.getManager().clearFromFile(hologram.getSaveId());
			HoloAPI.getManager().stopTracking(hologram);
			HologramFactory newHologram = new HologramFactory(Main.plugin).withLocation(hologram.getDefaultLocation()).withText(ChatColor.translateAlternateColorCodes("&".charAt(0), Ads.firstLine));
			for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
				if (line.getValue().contains("image:")) {
					if (Main.config().getBoolean("images")) {
						Integer height = line.getKey();
						File file = new File(line.getValue().replace("image:", ""));
						newHologram.withImage(new ImageGenerator("MCAds", file, height, ImageChar.BLOCK, false));
					}  
				} else {
					newHologram.withText(ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()).replace("{", "%").replace("}", "%"));
				}
			}
			Hologram builtHolo = newHologram.build();
			if (id.contains("MCAds")) {
				builtHolo.setSaveId(id);
			} else {
				builtHolo.setSaveId("MCAds-" + id);
			}
			builtHolo.addTouchAction(new TouchAction() {
				@Override
				public LinkedHashMap<String, Object> getDataToSave() {
					LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
					dataMap.put("reflink", Ads.refLink);
					return dataMap;
				}
				
				@Override
				public String getSaveKey() {
					return "message";
				}
				
				@Override
				public void onTouch(Player player, Action action) {
					for (String message : Phrases.config.getStringList("reflink")) {
						player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), message.replace("{link}", getDataToSave().get("reflink").toString())));
					}
				}
			});
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (Ads.hidden.contains(player.getUniqueId())) {
					builtHolo.clear(player);
				}
			}
			
		}
	}
	
	public static void delete(Location location, Player player, String radius) {
		Hologram closestHologram = null;
		if (radius == "closest") {
			for (Hologram hologram : HoloAPI.getManager().getHologramsFor(Main.plugin)) {
				double closestLocation = 999999999;
				hologram.getDefaultLocation();
				if (hologram.getDefaultLocation().getWorld().equals(player.getWorld()) && hologram.getDefaultLocation().distanceSquared(player.getLocation()) <= closestLocation) {
					closestLocation = hologram.getDefaultLocation().distanceSquared(player.getLocation());
					closestHologram = hologram;
				}
			}
			HoloAPI.getManager().stopTracking(closestHologram);
			HoloAPI.getManager().clearFromFile(closestHologram.getSaveId());
		} else {
			for (Hologram hologram : HoloAPI.getManager().getHologramsFor(Main.plugin)) {
				if (hologram.getDefaultLocation().getWorld().equals(player.getWorld()) && hologram.getDefaultLocation().distanceSquared(player.getLocation()) <= Double.parseDouble(radius)) {
					HoloAPI.getManager().stopTracking(hologram);
					HoloAPI.getManager().clearFromFile(hologram.getSaveId());
				}
			}
		}
		
	}
	
	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.server().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (Main.isEnabled("hologram")) {
					for (Map.Entry<Hologram, Plugin> h : HoloAPI.getManager().getAllHolograms().entrySet()) {
						if (h.getKey().getSaveId().contains("MCAds")) {
							try {
								update(h.getKey());
							} catch (ParserConfigurationException | IOException | SAXException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}, Main.config().getInt("hologram.delay") * 20, Main.config().getInt("hologram.delay") * 20);
	}
	
	public void enable() {
		if (Main.isEnabled("hologram")) {
			for (Map.Entry<Hologram, Plugin> h : HoloAPI.getManager().getAllHolograms().entrySet()) {
				if (h.getKey().getSaveId().contains("MCAds")) {
					try {
						update(h.getKey());
					} catch (ParserConfigurationException | IOException | SAXException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}