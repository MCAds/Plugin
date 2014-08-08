package net.MCAds.advertisements;

import org.bukkit.entity.Player;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.TouchHandler;

public class Ad_Hologram_Touch_Manager implements TouchHandler {

	@Override
	public void onTouch(Hologram hologram, Player player) {
		player.sendMessage(Ad_Hologram.refLink);
	}

}
