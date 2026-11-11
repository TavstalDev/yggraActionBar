package io.github.tavstaldev.yggraActionBar.impl.v1_16_5;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import io.github.tavstaldev.yggraActionBar.api.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link IActionBar} implementation for Minecraft 1.16.5 and newer servers.
 *
 * <p>Uses the native {@link Player#sendActionBar(Component)} API, which removes the
 * need for reflection or an additional platform library.</p>
 */
public class ActionBar implements IActionBar {

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component component) {
        player.sendActionBar(component);
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
