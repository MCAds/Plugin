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
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.sendMessage(ChatColor.YELLOW + "----" + ChatColor.RED + "MCAds help" + ChatColor.YELLOW + "----");
					player.sendMessage(ChatColor.GREEN + "/mcads reload | holo [create | delete [radius | 'closest']");
				}
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
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
						sender.sendMessage("MCAds has been reloaded");
					}
				}
				if (args[0].equalsIgnoreCase("create")) {
					if (sender.hasPermission("mcads.create")) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							try {
								Ad_Hologram.create(player.getEyeLocation());
							} catch (ParserConfigurationException | IOException | SAXException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (args[0].equalsIgnoreCase("delete")) {
					if (sender.hasPermission("mcads.delete")) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							Ad_Hologram.delete(player.getLocation(), player, "closest");
						}
					}
				}
				if (args[0].equalsIgnoreCase("disablebypass")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (Main.bypassDisable.contains(player)) {
							Main.bypassDisable.remove(player);
						} else {
							Main.bypassDisable.add(player);
						}
					}
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("delete")) {
					if (sender.hasPermission("mcads.delete")) {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (args[1].equalsIgnoreCase("closest")) {
								Ad_Hologram.delete(player.getLocation(), player, "closest");
							} else {
								Ad_Hologram.delete(player.getLocation(), player, String.valueOf(Double.parseDouble(args[1]) * Double.parseDouble(args[1])));
							}
						}
					}
				}
			}
		}
		if (commandLabel.equalsIgnoreCase("getlink")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "You must specify a location to get the referral link from. The available options are scoreboard and bossbar");
				}
				if (args.length == 1) {
					if (Main.getInstance().getConfig().getBoolean("scoreboard.enabled")) {
						if (args[0].equalsIgnoreCase("scoreboard") || args[0].equalsIgnoreCase("sidebar") || args[0].equalsIgnoreCase("side") || args[0].equalsIgnoreCase("sbar") || args[0].equalsIgnoreCase("sb")) {
							sender.sendMessage(ChatColor.BLUE + Ad_Scoreboard.refLink);
						}
					}
					if (args[0].equalsIgnoreCase("bossbar") || args[0].equalsIgnoreCase("healthbar") || args[0].equalsIgnoreCase("boss") || args[0].equalsIgnoreCase("health") || args[0].equalsIgnoreCase("bar") || args[0].equalsIgnoreCase("hbar") || args[0].equalsIgnoreCase("hb") || args[0].equalsIgnoreCase("bb")) {
						if (Main.getInstance().getConfig().getBoolean("scoreboard.enabled")) {
							sender.sendMessage(ChatColor.BLUE + Ad_Scoreboard.refLink);
						}
					}
				}
			}
		}
		return true;
	}

}
