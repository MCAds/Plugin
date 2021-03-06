**The current version only supports 1.7.9+**

[Ad database](http://mcads.net) | [Bukkit](http://dev.bukkit.org/bukkit-plugins/mcads) | [Advertiser Documentation](https://github.com/MCAds/Plugin/wiki/Advertiser-documentation)

## Description ##
MCAds is a new way for servers to make money without begging players for donations. We do not take any commission on ads, so server owners can make exactly what advertisers pay (minus taxes and PayPal fees). MCAds is based on XML for the ads. Advertisers simply create XML files containing the contents of their ads. Server owners can add those files to their config. Servers get paid a specified amount by the advertiser based on the amount of clicks on a generated referral link. We are also currently working on a website where advertisers will be able to post their ads.

## Dependencies ##
* [HoloAPI](http://dev.bukkit.org/bukkit-plugins/holoapi/) (for versions 2.0+)
* [Holographic Displays](http://dev.bukkit.org/bukkit-plugins/holographic-displays) (for versions below 2.0)
* [BarAPI](http://dev.bukkit.org/bukkit-plugins/bar-api/)

## Development builds ##
Development builds for MCAds are available at http://ci.mcads.net/job/MCAds/. These builds are unstable and should NEVER be used on a production server.

## Commands ##
* /mcads reload - reloads plugin
* /mcads create - creates hologram ad at your location
* /mcads delete <radius>- deletes all holograms in specified location. Use "closest" to delete the closest hologram to you.

## Ad locations##
* Sidebar/scoreboard
* Bossbar /enderdragon health bar
* Chat
* Holograms/floating text
Ads in any location can be disabled from the config.

## Screenshots ##
### Scoreboard ###
![Scoreboard](https://examples.mcads.net/images/scoreboard.png)
### Chat ###
![Chat](https://examples.mcads.net/images/chat.png)
### Bossbar ###
![Bossbar](https://examples.mcads.net/images/bossbar.png)
### Hologram ###
![Hologram](https://examples.mcads.net/images/hologram.png)

## Credits ##
MCAds uses [ImageMessage](http://forums.bukkit.org/threads/lib-imagemessage-v2-1-send-images-to-players-via-the-chat.204902/) by [bobacadodl](http://forums.bukkit.org/members/bobacadodl.90595184/) for sending images in chat.

## To-dos ##
* Website that advertisers can upload links to their ads to
* ~~Support for multiple language~~
* ~~Images in holograms~~
* ~~Touch screen holograms~~

## Metrics ##
To determine popularity and usage of MCAds, plugin installs are automatically tracked by the Metrics plugin tracking system. Your Java version, OS, player count, server country location and plugin & server versions are collected. This is used to determine what environments are using the plugin to ensure full compatibility. This collection is anonymous. If you don't want this tracking, edit plugins/PluginMetrics/config.yml and set opt-out to true. 
[![Metrics](http://api.mcstats.org/signature/MCAds.png)](http://mcstats.org/plugin/MCAds)
