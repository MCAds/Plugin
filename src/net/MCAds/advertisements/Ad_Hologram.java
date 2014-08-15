package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.xml.sax.SAXException;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;

public class Ad_Hologram implements Listener {
	public static String refLink;

	public static void create(Location location) throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("hologram")) {
			Hologram hologram = new HologramFactory(Main.plugin).withLocation(location).withText("").build();
			save(hologram);
			update(hologram);
		}
	}

	public static void update(Hologram hologram) throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("hologram")) {
			Ads ads = new Ads();
			ads.ad("hologram", "line");
			String id = hologram.getSaveId();
			HoloAPI.getManager().stopTracking(hologram);
			HoloAPI.getManager().clearFromFile(hologram.getSaveId());
			HologramFactory newHologram = new HologramFactory(Main.plugin).withLocation(hologram.getDefaultLocation()).withText(ChatColor.translateAlternateColorCodes("&".charAt(0), Ads.firstLine));
			for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
				if (line.getValue().contains("image:")) {
					Integer height = line.getKey();
					File file = new File(line.getValue().replace("image:", ""));
					newHologram.withImage(new ImageGenerator("MCAds", file, height, ImageChar.BLOCK, false));
				} else {
					newHologram.withText(ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()).replace("{", "%").replace("}", "%"));
				}
			}
			Hologram builtHolo = newHologram.build();
			builtHolo.setSaveId(id);;
			builtHolo.addTouchAction(new TouchAction() {

				@Override
				public LinkedHashMap<String, Object> getDataToSave() {
					return null;
				}

				@Override
				public String getSaveKey() {
					return "message";
				}

				@Override
				public void onTouch(Player player, com.dsh105.holoapi.protocol.Action action) {
//					player.sendMessage(ChatColor.BLUE + Ads.refLink);
					player.sendMessage("testing 13");
					
				}

			});
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

	public static void save(Hologram hologram) throws IOException {
		if (Main.getInstance().isEnabled("hologram")) {
			File customYml = new File(Main.getInstance().getDataFolder() + "/holograms" + ".yml");
			FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
			List<String> holograms = new ArrayList<String>();
			holograms.add(hologram.getSaveId());
			for (String configHologram : customConfig.getStringList("holograms")) {
				holograms.add(configHologram);
			}
			customConfig.set("holograms", holograms);
			customConfig.save(customYml);
			HoloAPI.getManager().stopTracking(hologram);
			HoloAPI.getManager().clearFromFile(hologram.getSaveId());
		}
	}

	public void saveAll() throws IOException {
		if (Main.getInstance().isEnabled("hologram")) {
			File customYml = new File(Main.getInstance().getDataFolder() + "/holograms" + ".yml");
			FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
			List<String> holograms = new ArrayList<String>();
			for (Hologram hologram : HoloAPI.getManager().getHologramsFor(Main.plugin)) {
				if (!customConfig.contains(hologram.getSaveId())) {
					holograms.add(hologram.getSaveId());
				}
			}
			for (String hologram : customConfig.getStringList("holograms")) {
				holograms.add(hologram);
			}
			customConfig.set("holograms", holograms);
			customConfig.save(customYml);

		}
	}

	public void load() throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("hologram")) {
			File customYml = new File(Main.getInstance().getDataFolder() + "/holograms" + ".yml");
			FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
			List<String> ids = customConfig.getStringList("holograms");
			for (String id : ids) {
				Hologram hologram = HoloAPI.getManager().getHologram(id);
				update(hologram);
			}
			customYml.delete();
		}
	}

	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (Main.getInstance().isEnabled("hologram")) {
					for (Hologram hologram : HoloAPI.getManager().getHologramsFor(Main.plugin)) {
						try {
							update(hologram);
						} catch (ParserConfigurationException | IOException | SAXException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, Main.getInstance().getConfig().getInt("hologram.delay") * 20, Main.getInstance().getConfig().getInt("hologram.delay") * 20);
	}

}
