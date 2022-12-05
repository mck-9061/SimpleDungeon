package me.mckdev.SimpleDungeon.Listeners;

import me.mckdev.SimpleDungeon.DungeonSession;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {
    // cancel damage to players from mobs that aren't in their session
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (DungeonSession.isPlayerInDungeon(player)) {
            DungeonSession session = DungeonSession.getSession(player);
            LivingEntity e = null;

            if (!(event.getDamager() instanceof LivingEntity)) {
                if (event.getDamager() instanceof Projectile) {
                    e = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                }
            } else {
                e = (LivingEntity) event.getDamager();
            }

            // check if the attacker is a mob in a different session
            if (DungeonSession.getSession(e) != session) {
                event.setCancelled(true);
                DungeonSession mobSession = DungeonSession.getSession(e);
                if (mobSession == null) return;
                ((Creature) e).setTarget(mobSession.getPlayer());
            }
        }
    }
}
