package io.github.tavstaldev.yggraActionBar.impl.v1_13;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
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

public class ActionBar implements IActionBar {
    private final BukkitAudiences adventure;

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

        msg = msg.replace('§', '&');
        Component component = LegacyComponentSerializer.legacySection().deserialize(msg);
        send(player, component);
    }
}
