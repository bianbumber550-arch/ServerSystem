package me.testaccount666.serversystem.listener.executables.operatorspoof;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * FIXED FOR PAPER 1.21.8 COMPATIBILITY
 * 
 * Original error:
 * - java.lang.NoClassDefFoundError: net/minecraft/network/protocol/game/ServerboundChangeGameModePacket
 * 
 * Solution: Use Bukkit API instead of direct NMS packet handling
 * Removed all Netty/NMS dependencies
 */
public class ListenerOperatorSpoof implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is trying to spoof OP status
        if (!player.isOp() && player.hasPermission("minecraft.command.op")) {
            // Remove the permission that allows OP commands
            player.getEffectivePermissions().removeIf(perm -> 
                perm.getPermission().startsWith("minecraft.command.op")
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Cleanup if needed
    }

    /**
     * Listener for gamemode changes using Bukkit Events
     * This replaces the packet-based approach
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is spoofing OP to change gamemode
        if (!player.isOp() && !player.hasPermission("minecraft.command.gamemode")) {
            event.setCancelled(true);
            player.sendMessage("§cYou don't have permission to change gamemode!");
        }
    }
}
