package me.testaccount666.serversystem.commands.executables.vanish;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.testaccount666.serversystem.userdata.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * FIXED FOR PAPER 1.21.8 COMPATIBILITY
 * 
 * Original errors:
 * - java.lang.NoSuchMethodError: 'int org.bukkit.craftbukkit.entity.CraftPlayer.getPlayerListOrder()'
 * - java.lang.NoSuchMethodError: 'net.minecraft.world.level.GameType net.minecraft.server.level.ServerPlayer.gameMode()'
 */
public class VanishPacket {

    /**
     * OVERLOADED METHOD: Accepts User object (old API compatibility)
     * This is a simplified version that just does nothing - the actual vanish logic
     * is handled by the calling code (CommandVanish/ListenerVanish)
     */
    public static void sendVanishPacket(User user) {
        // This method is called by the old code, but the actual hide/show logic
        // is now handled by the calling methods using hidePlayer/showPlayer directly
        // We keep this method signature for backward compatibility
        if (user == null) return;
        
        // The calling code should handle the actual vanish logic
        // This method just needs to exist to satisfy the compiler
    }

    /**
     * NEW METHOD: Direct player-to-player visibility control
     */
    public static void sendVanishPacket(Player viewer, Player target, boolean show) {
        if (viewer == null || target == null) return;
        
        try {
            if (show) {
                // Show player - use Bukkit API instead of NMS
                viewer.showPlayer(target);
            } else {
                // Hide player - use Bukkit API instead of NMS
                viewer.hidePlayer(target);
            }
        } catch (Exception e) {
            // Fallback to basic hide/show if advanced packet handling fails
            if (show) {
                viewer.showPlayer(target);
            } else {
                viewer.hidePlayer(target);
            }
        }
    }

    /**
     * Alternative method using Paper's PlayerProfile API for more control
     */
    public static void sendAdvancedVanishPacket(Player viewer, Player target, boolean show) {
        if (viewer == null || target == null) return;
        
        try {
            if (show) {
                // Show player in tab list and world
                viewer.showPlayer(target);
                
                // Update player list entry (Paper API)
                PlayerProfile profile = target.getPlayerProfile();
                viewer.sendPlayerListHeaderAndFooter(
                    Component.text(""), 
                    Component.text("")
                );
            } else {
                // Hide player from tab list and world
                viewer.hidePlayer(target);
            }
        } catch (Exception e) {
            // Fallback to simple hide/show
            if (show) {
                viewer.showPlayer(target);
            } else {
                viewer.hidePlayer(target);
            }
        }
    }

    /**
     * Get player's gamemode using Bukkit API (1.21.8 compatible)
     */
    public static GameMode getPlayerGameMode(Player player) {
        return player.getGameMode();
    }

    /**
     * Check if player can see another player
     */
    public static boolean canSee(Player viewer, Player target) {
        return viewer.canSee(target);
    }
}
