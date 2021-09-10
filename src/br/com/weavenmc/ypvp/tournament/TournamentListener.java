package br.com.weavenmc.ypvp.tournament;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;

public class TournamentListener implements Listener {
	@EventHandler
	public void onTournamentUpdate(final UpdateEvent event) {
		event.getType();
	}
}
