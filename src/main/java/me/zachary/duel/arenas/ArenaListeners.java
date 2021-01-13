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
        Player victim = event.getEntity();
        Arena arenas = main.getArenaManager().getArenaByPlayer(event.getEntity());
        Player killer = null;
        if(arenas != null){
            if(victim == arenas.getPlayers().get(0)){
                killer = arenas.getPlayers().get(1);
            }else{
                killer = arenas.getPlayers().get(0);
            }
            if(!main.getConfig().getBoolean("Should_Killed_Player_Should_Drop_There_Stuff")){
                event.getDrops().clear();
            }
            event.setKeepLevel(true);
            Bukkit.broadcastMessage(Utils.chat(Translation.Broadcast_Duel_Win.toString().replace("<winner>", killer.getName()).replace("<loser>", victim.getName())));
            Arena arena = main.getArenaManager().getArenaByPlayer(killer);

            if(main.getConfig().getBoolean("Particle_When_Player_Win_Duel.Enable") && !(ReflectionUtils.VERSION.contains("1_8") || ReflectionUtils.VERSION.contains("1_9"))){
                com.cryptomorin.xseries.particles.XParticle.circle(3, 5,com.cryptomorin.xseries.particles.ParticleDisplay.display(killer.getLocation(), com.cryptomorin.xseries.particles.XParticle.getParticle(main.getConfig().getString("Particle_When_Player_Win_Duel.Particle"))));
            }

            Player finalKiller = killer;
            Bukkit.getScheduler().runTaskLater(main.getMain(), new Runnable() {
                @Override
                public void run() {
                    if(!main.getConfig().getBoolean("Player_Should_PVP_With_Their_Own_Stuff")){
                        main.getArenaManager().restoreInventory(finalKiller);
                    }
                    finalKiller.setHealth(20);
                    main.getArenaManager().restoreLocations(finalKiller);
                }
            }, 100L);
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
        Player otherPlayer = null;
        Arena arena = main.getArenaManager().getArenaByPlayer(leaver);

        if(arena != null) {
            if(leaver == arena.getPlayers().get(0)){
                otherPlayer = arena.getPlayers().get(1);
            }else{
                otherPlayer = arena.getPlayers().get(0);
            }
            main.getArenaManager().restoreInventory(leaver);
            main.getArenaManager().restoreInventory(otherPlayer);
            if (!main.getConfig().getBoolean("Player_Should_PVP_With_Their_Own_Stuff")) {
                main.getArenaManager().restoreInventory(leaver);
                main.getArenaManager().restoreInventory(otherPlayer);
            }
            otherPlayer.sendMessage(Utils.chat("&e" + leaver.getName() + " &6leave the server. So, duel is cancelled."));
            arena.eliminate(leaver);
        }

    }

}
