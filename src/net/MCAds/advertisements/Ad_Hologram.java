package net.MCAds.advertisements;

import java.io.File;
import java.io.IOException;
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

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.gmail.filoghost.holograms.api.TouchHandler;
import com.gmail.filoghost.holograms.exception.WorldNotFoundException;
import com.gmail.filoghost.holograms.utils.LocationUtils;

public class Ad_Hologram implements Listener {
	public static String refLink;
	public static TouchHandler touchhandler;

	public static void create(Location location) throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("hologram")) {
			Hologram hologram = HolographicDisplaysAPI.createHologram(Main.plugin, location, ChatColor.translateAlternateColorCodes("&".charAt(0), Main.getInstance().getConfig().getString("hologram.first-line")));
			update(hologram);
		}
	}

	public static void update(Hologram hologram) throws ParserConfigurationException, IOException, SAXException {
		if (Main.getInstance().isEnabled("hologram")) {
			Ads ads = new Ads();
			ads.ad("hologram", "line");
			hologram.addLine(ChatColor.translateAlternateColorCodes("&".charAt(0), Ads.firstLine));
			for (Map.Entry<Integer, String> line : ads.lines.entrySet()) {
				hologram.addLine(ChatColor.translateAlternateColorCodes("&".charAt(0), line.getValue()));
			}
			hologram.setTouchHandler(new Ad_Hologram_Touch_Manager());
			hologram.update();
		}
	}

	public static void delete(Location location, Player player, String radius) {
		Hologram closestHologram = null;
		if (radius == "closest") {
			for (Hologram hologram : HolographicDisplaysAPI.getHolograms(Main.plugin)) {
				double closestLocation = 999999999;
				if (hologram.getLocation().getWorld().equals(player.getWorld()) && hologram.getLocation().distanceSquared(player.getLocation()) <= closestLocation) {
					closestLocation = hologram.getLocation().distanceSquared(player.getLocation());
					closestHologram = hologram;
				}
			}
			closestHologram.delete();
		} else {
			for (Hologram hologram : HolographicDisplaysAPI.getHolograms(Main.plugin)) {
				if (hologram.getLocation().getWorld().equals(player.getWorld()) && hologram.getLocation().distanceSquared(player.getLocation()) <= Double.parseDouble(radius)) {
					hologram.delete();
				}
			}
		}

	}

	public static void save() throws IOException {
		if (Main.getInstance().isEnabled("hologram")) {
			File customYml = new File(Main.getInstance().getDataFolder() + "/holograms" + ".yml");
			FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
			customConfig.save(customYml);
			int num = 0;
			for (Hologram hologram : HolographicDisplaysAPI.getHolograms(Main.plugin)) {
				num++;
				customConfig.set("holograms." + num + ".location", LocationUtils.locationToString(hologram.getLocation()));
				customConfig.set("holograms." + num + ".lines", hologram.getLines());
				hologram.delete();
			}
			customConfig.save(customYml);
		}
	}

	public static void load() throws WorldNotFoundException, Exception {
		if (Main.getInstance().isEnabled("hologram")) {
			if (Main.getInstance().getConfig().get("holograms") != null) {
				File customYml = new File(Main.getInstance().getDataFolder() + "/holograms" + ".yml");
				FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
				for (String key : customConfig.getConfigurationSection("holograms").getKeys(false)) {
					Hologram hologram = null;
					String locationString = customConfig.getString("holograms." + key + ".location");
					String[] arg = locationString.split(",");
					double[] parsed = new double[3];
					for (int a = 0; a < 3; a++) {
						parsed[a] = Double.parseDouble(arg[a + 1]);
					}
					Location location = new Location(Main.plugin.getServer().getWorld(arg[0]), parsed[0], parsed[1], parsed[2]);
					hologram = HolographicDisplaysAPI.createHologram(Main.plugin, location);
					List<String> lines = customConfig.getStringList("holograms." + key + ".lines");
					for (String line : lines) {
						hologram.addLine(line);
					}
					hologram.setTouchHandler(new Ad_Hologram_Touch_Manager());
					hologram.update();
				}
				customYml.delete();
			}
		}
	}

	public void timer(Main plugin) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (Main.getInstance().isEnabled("hologram")) {
					for (Hologram hologram : HolographicDisplaysAPI.getHolograms(Main.plugin)) {
						hologram.clearLines();
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
