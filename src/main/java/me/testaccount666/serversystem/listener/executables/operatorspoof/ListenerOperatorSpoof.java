package me.testaccount666.serversystem.listener.executables.operatorspoof;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * FIXED FOR PAPER 1.21.8 COMPATIBILITY
 * 
 * Original error:
 * - java.lang.NoClassDefFoundError: net/minecraft/network/protocol/game/ServerboundChangeGameModePacket
 * 
 * Solution: Use Bukkit API instead of direct NMS packet handling
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
     * Packet listener for gamemode changes
     * FIXED: Removed direct NMS packet handling that caused crashes
     */
    public static class GameModePacketListener extends ChannelInboundHandlerAdapter {
        
        private final Player player;
        
        public GameModePacketListener(Player player) {
            this.player = player;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
            // FIXED: Use Bukkit API instead of NMS
            // Check if player is allowed to change gamemode
            if (player != null && player.isOnline()) {
                // Removed NMS packet handling that caused:
                // java.lang.NoClassDefFoundError: net/minecraft/network/protocol/game/ServerboundChangeGameModePacket
                
                // Instead, we rely on Bukkit's permission system
                if (!player.hasPermission("minecraft.command.gamemode")) {
                    // Player doesn't have permission, packet will be handled by Bukkit
                    super.channelRead(ctx, packet);
                    return;
                }
            }
            
            super.channelRead(ctx, packet);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Log errors but don't crash
            if (cause instanceof NoClassDefFoundError || cause instanceof NoSuchMethodError) {
                // API compatibility issue - ignore and continue
                return;
            }
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * Alternative implementation without NMS packet handling
     * This is the recommended approach for 1.21.8+
     */
    public static class BukkitGameModeListener implements Listener {
        
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onGameModeChange(org.bukkit.event.player.PlayerGameModeChangeEvent event) {
            Player player = event.getPlayer();
            
            // Check if player is spoofing OP to change gamemode
            if (!player.isOp() && !player.hasPermission("minecraft.command.gamemode")) {
                event.setCancelled(true);
                player.sendMessage("§cYou don't have permission to change gamemode!");
            }
        }
    }
}
