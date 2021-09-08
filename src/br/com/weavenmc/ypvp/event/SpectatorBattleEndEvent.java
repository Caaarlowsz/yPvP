package br.com.weavenmc.ypvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class SpectatorBattleEndEvent extends Event
{
    private static final HandlerList handlers;
    private Player player1;
    private Player player2;
    
    static {
        handlers = new HandlerList();
    }
    
    public HandlerList getHandlers() {
        return SpectatorBattleEndEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return SpectatorBattleEndEvent.handlers;
    }
    
    public SpectatorBattleEndEvent(final Player player1, final Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }
    
    public Player getPlayer1() {
        return this.player1;
    }
    
    public Player getPlayer2() {
        return this.player2;
    }
}
