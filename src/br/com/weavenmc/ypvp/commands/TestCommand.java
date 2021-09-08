package br.com.weavenmc.ypvp.commands;

import br.com.weavenmc.commons.core.command.CommandFramework;
import org.bukkit.entity.Player;
import br.com.weavenmc.ypvp.jnbt.DataException;
import java.io.IOException;
import java.io.File;
import br.com.weavenmc.ypvp.yPvP;
import br.com.weavenmc.ypvp.jnbt.Schematic;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;

public class TestCommand implements CommandClass
{
    @CommandFramework.Command(name = "schematic")
    public void test(final BukkitCommandSender sender, final String label, final String[] args) {
        if (sender.isPlayer()) {
            final Player p = sender.getPlayer();
            if (args.length == 0) {
                p.sendMessage("§3§lSCHEMATIC§f Utilize: §b§l/schematic§f [file name ...]");
            }
            else {
                Schematic file = null;
                try {
                    file = Schematic.getInstance().loadSchematic(new File(yPvP.getPlugin().getDataFolder(), args[0]));
                }
                catch (IOException | DataException ex3) {
                    final Exception ex2;
                    final Exception ex = ex2;
                    p.sendMessage("§3§lSCHEMATIC§f Erro: §b" + ex.getMessage());
                }
                if (file != null) {
                    p.sendMessage("§3§lSCHEMATIC§f Spawnando o schematic...");
                    Schematic.spawn(p.getWorld(), p.getLocation(), file);
                }
            }
        }
        else {
            sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
        }
    }
}
