package br.com.weavenmc.ypvp.tournament;

import org.bukkit.event.EventHandler;
import br.com.weavenmc.commons.bukkit.event.update.UpdateEvent;
import org.bukkit.event.Listener;

public class TournamentListener implements Listener
{
    @EventHandler
    public void onTournamentUpdate(final UpdateEvent event) {
        event.getType();
        final UpdateEvent.UpdateType minute = UpdateEvent.UpdateType.MINUTE;
    }
}
