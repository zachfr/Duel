package me.zachary.duel.Arenas;

import me.zachary.duel.Duel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ArenaListeners implements Listener {

    private Duel main;

    public ArenaListeners(Duel duel) {
        this.main = duel;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            Arena victimArena = main.getArenaManager().getArenaByPlayer(victim);

            if (victimArena == null || !victimArena.getPlayers().contains(damager)) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onKill(PlayerDeathEvent event){

        if (event.getEntity().getKiller() instanceof Player) {
            event.getDrops().clear();
            Player victim = event.getEntity();
            Player killer = (Player) victim.getKiller();
            Arena arena = main.getArenaManager().getArenaByPlayer(killer);

            main.getArenaManager().restoreInventory(killer);

            if(arena != null) {
                arena.eliminate(victim);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player victim = event.getPlayer();
        main.getArenaManager().restoreInventory(victim);
        main.getArenaManager().ClearMap(victim);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        Player leaver = event.getPlayer();
        Arena arena = main.getArenaManager().getArenaByPlayer(leaver);

        if(arena != null) {
            arena.eliminate(leaver);
        }

    }

}
