package io.github.tavstaldev.yggraActionBar.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Platform-independent entry point for displaying messages on a player's action bar.
 *
 * <p>Messages can be supplied either as an Adventure {@link Component} or as a
 * formatted string. String based messages support both legacy colour codes
 * (using {@code &} or {@code §}) and MiniMessage tags, depending on the server
 * implementation in use.</p>
 *
 * <p>The string based overloads also support named placeholders. A placeholder is
 * declared as {@code %name%} inside the message and supplied through the
 * {@code parameters} map. Keys may be provided with or without the surrounding
 * percent signs.</p>
 *
 * <p>Plugins normally obtain an instance through the {@code YggraActionBar} facade from the
 * bootstrap module rather than implementing this interface themselves.</p>
 */
@SuppressWarnings("unused")
public interface IActionBar {

    /**
     * Sends a pre-built Adventure component to the player's action bar.
     *
     * @param player    the player who should receive the message; must not be {@code null}
     * @param component the component to display; must not be {@code null}
     */
    void send(@NotNull Player player, @NotNull Component component);

    /**
     * Sends a formatted message to the player's action bar without any placeholders.
     *
     * @param player  the player who should receive the message; must not be {@code null}
     * @param message the message to display; must not be {@code null}
     */
    void send(@NotNull Player player, @NotNull String message);

    /**
     * Sends a formatted message to the player's action bar, replacing the supplied placeholders.
     *
     * @param player     the player who should receive the message; must not be {@code null}
     * @param message    the message template to display; must not be {@code null}
     * @param parameters placeholder names mapped to their replacement values; must not be {@code null}
     */
    void send(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters);
}
