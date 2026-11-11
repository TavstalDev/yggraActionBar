package io.github.tavstaldev.yggraActionBar.impl.v1_13;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import io.github.tavstaldev.yggraActionBar.api.utils.AdventureUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link IActionBar} implementation for Minecraft 1.13 - 1.16.4 servers.
 *
 * <p>Delegates to Adventure's Bukkit platform, which handles the version-specific
 * packet format for the action bar. Messages are deserialised from legacy section
 * codes before being handed to Adventure.</p>
 */
public class ActionBar implements IActionBar {
    private final BukkitAudiences adventure;

    /**
     * Creates the implementation and initialises the Adventure platform for the owning plugin.
     *
     * @param plugin the plugin instance used to create the {@link BukkitAudiences} instance
     */
    public ActionBar(JavaPlugin plugin) {
        this.adventure = BukkitAudiences.create(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component component) {
        adventure.player(player).sendActionBar(component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String message) {
        send(player, message, new HashMap<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters) {
        String msg = message;

        // Get the keys
        Set<String> keys = parameters.keySet();
        for (@RegExp String dirKey : keys) {
            @RegExp String finalKey;
            if (dirKey.startsWith("%"))
                finalKey = dirKey;
            else
                finalKey = "%" + dirKey + "%";
            msg = msg.replace(finalKey, parameters.get(dirKey).toString());
        }

        Component component = AdventureUtils.parse(msg);
        send(player, component);
    }
}
