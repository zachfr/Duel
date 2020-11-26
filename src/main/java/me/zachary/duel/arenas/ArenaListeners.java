package me.zachary.duel.arenas;

import me.zachary.duel.Duel;
import me.zachary.duel.Translation;
import me.zachary.duel.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.theprogramsrc.supercoreapi.spigot.utils.ReflectionUtils;

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

            if(main.getConfig().getBoolean("Should_PvP_Is_Only_Enable_During_Duel")){
                if (victimArena == null || !victimArena.getPlayers().contains(damager)) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onKill(PlayerDeathEvent event){

        if (event.getEntity().getKiller() instanceof Player) {
            Player victim = event.getEntity();
            Player killer = (Player) victim.getKiller();
            Arena arenas = main.getArenaManager().getArenaByPlayer(killer);
            if(!main.getConfig().getBoolean("Should_Killed_Player_Should_Drop_There_Stuff")){
                event.getDrops().clear();
            }
            if (arenas != null){
                event.setKeepLevel(true);
                Bukkit.broadcastMessage(Utils.chat(Translation.Broadcast_Duel_Win.toString().replace("<winner>", killer.getName()).replace("<loser>", victim.getName())));
                Arena arena = main.getArenaManager().getArenaByPlayer(killer);

                if(main.getConfig().getBoolean("Particle_When_Player_Win_Duel.Enable") && !(ReflectionUtils.VERSION.contains("1_8") || ReflectionUtils.VERSION.contains("1_9"))){
                    com.cryptomorin.xseries.particles.XParticle.circle(3, 5,com.cryptomorin.xseries.particles.ParticleDisplay.display(killer.getLocation(), com.cryptomorin.xseries.particles.XParticle.getParticle(main.getConfig().getString("Particle_When_Player_Win_Duel.Particle"))));
                }

                Bukkit.getScheduler().runTaskLater(main.getMain(), new Runnable() {
                    @Override
                    public void run() {
                        if(!main.getConfig().getBoolean("Player_Should_PVP_With_Their_Own_Stuff")){
                            main.getArenaManager().restoreInventory(killer);
                        }
                        killer.setHealth(20);
                        main.getArenaManager().restoreLocations(killer);
                    }
                }, 100L);
            }

            /*if(arena != null) {
                arena.eliminate(victim);
            }*/
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player victim = event.getPlayer();
        Arena arena = main.getArenaManager().getArenaByPlayer(victim);
        if (arena != null) {
            if (arena.isStarted()) {
                Bukkit.getScheduler().runTaskLater(main.getMain(), new Runnable() {
                    @Override
                    public void run() {
                        if (!main.getConfig().getBoolean("Player_Should_PVP_With_Their_Own_Stuff")) {
                            main.getArenaManager().restoreInventory(victim);
                        }
                        main.getArenaManager().restoreLocations(victim);
                    }
                }, 5L);
                Bukkit.getScheduler().runTaskLater(main.getMain(), new Runnable() {
                    @Override
                    public void run() {
                        main.getArenaManager().ClearMap(victim);
                    }
                }, 125L);
                arena.eliminate(victim);
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        Arena arena = main.getArenaManager().getArenaByPlayer(player);
        if(arena != null){
            event.setCancelled(true);
        }
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
