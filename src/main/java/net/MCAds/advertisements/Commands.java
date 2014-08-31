package net.MCAds.advertisements;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.xml.sax.SAXException;

public class Commands implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("mcads")) {
			if (args.length == 0) {
				for (String message : Phrases.config.getStringList("help")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), message));
				}
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender instanceof Player) {
						if (sender.hasPermission("mcads.reload")) {
							Main.getInstance().reloadConfig();
							Cache cache = new Cache();
							try {
								Main.getInstance().reloadConfig();
								cache.timer();
								cache.delete();
								cache.create();
								String[] types = { "bossbar", "scoreboard", "chat", "hologram" };
								for (String type : types) {
									Cache.featured(type);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("reloaded")));
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("no_permission")));
						}
					} else {
						Main.getInstance().reloadConfig();
						Cache cache = new Cache();
						try {
							Main.getInstance().reloadConfig();
							cache.timer();
							cache.delete();
							cache.create();
							String[] types = { "bossbar", "scoreboard", "chat", "hologram" };
							for (String type : types) {
								Cache.featured(type);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("reloaded")));
					}
				}
				if (args[0].equalsIgnoreCase("create")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (player.hasPermission("mcads.create")) {
							try {
								Ad_Hologram.create(player.getEyeLocation());
								player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("hologram_create")));
							} catch (ParserConfigurationException | IOException | SAXException e) {
								e.printStackTrace();
							}
						} else {
							sender.sendMessage(Phrases.config.getString("no_permission"));
						}
					}
				}
				if (args[0].equalsIgnoreCase("delete")) {
					if (sender instanceof Player) {
						if (sender.hasPermission("mcads.delete")) {
							Player player = (Player) sender;
							Ad_Hologram.delete(player.getLocation(), player, "closest");
							player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("hologram_delete_closest")));
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("no_permission")));
						}
					}
				}
				if (args[0].equalsIgnoreCase("hide")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (player.hasPermission("mcads.hide")) {
							if (Ads.hidden.contains(player.getUniqueId())) {
								player.sendMessage(Phrases.config.getString("already_hidden"));
							} else {
								Ads.hidden.add(player.getUniqueId());
								player.sendMessage(Phrases.config.getString("hide_ads"));
							}
						}
					}
					if (args[0].equalsIgnoreCase("show")) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (player.hasPermission("mcads.show")) {
								if (Ads.hidden.contains(player.getUniqueId())) {
									Ads.hidden.remove(player.getUniqueId());
									player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("show_ads")));
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("already_shown")));
								}
							}
						}
					}
					if (args.length == 2) {
						if (args[0].equalsIgnoreCase("delete")) {
							if (sender instanceof Player) {
								Player player = (Player) sender;
								if (player.hasPermission("mcads.delete")) {
									if (args[1].equalsIgnoreCase("closest")) {
										Ad_Hologram.delete(player.getLocation(), player, "closest");
										player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("hologram_delete_closest")));
									} else {
										Ad_Hologram.delete(player.getLocation(), player, String.valueOf(Double.parseDouble(args[1]) * Double.parseDouble(args[1])));
										player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("hologram_delete_radius")).replace("{radius}", args[1]));
									}
								}
								player.sendMessage(ChatColor.RED + "You do not have permission!");
							}
						}
					}
				}
			}
		}
		if (commandLabel.equalsIgnoreCase("getlink")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Phrases.config.getString("reflink_no_location")));
				}
				if (args.length == 1) {
					if (Main.getInstance().getConfig().getBoolean("scoreboard.enabled")) {
						for (String message : Phrases.config.getStringList("reflink")) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), message.replace("{link}", Ad_Scoreboard.refLink)));
						}
					}
					if (args[0].equalsIgnoreCase("bossbar") || args[0].equalsIgnoreCase("healthbar") || args[0].equalsIgnoreCase("boss") || args[0].equalsIgnoreCase("health") || args[0].equalsIgnoreCase("bar") || args[0].equalsIgnoreCase("hbar") || args[0].equalsIgnoreCase("hb") || args[0].equalsIgnoreCase("bb")) {
						if (Main.getInstance().getConfig().getBoolean("scoreboard.enabled")) {
							for (String message : Phrases.config.getStringList("reflink")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), message.replace("{link}", Ad_Bossbar.refLink)));
							}
						}
					}
				}
			}
		}
		return true;
	}
}
