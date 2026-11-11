package io.github.tavstaldev.yggraActionBar.bootstrap;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import io.github.tavstaldev.yggraActionBar.bootstrap.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Default {@link IActionBar} facade that delegates to a server-version-specific implementation.
 *
 * <p>The concrete implementation is resolved once, when this object is constructed, by
 * comparing the running server version against the supported implementation modules.
 * All calls are forwarded to that implementation; if it could not be loaded, calls are
 * silently ignored so a missing implementation never crashes the consuming plugin.</p>
 *
 * <p>Supported implementations:</p>
 * <ul>
 *     <li>1.16.5 and newer: native Bukkit action bar support</li>
 *     <li>1.13 - 1.16.4: Adventure platform for Bukkit</li>
 *     <li>1.12 - 1.12.2: NMS {@code PacketPlayOutChat} with {@code ChatMessageType.GAME_INFO}</li>
 *     <li>1.8 - 1.11.2: NMS {@code PacketPlayOutChat} with chat type {@code 2}</li>
 * </ul>
 */
@SuppressWarnings("CallToPrintStackTrace")
public class YggraActionBar implements IActionBar {

    private IActionBar _bar;

    /**
     * Creates a new action bar facade and loads the implementation matching the running server.
     *
     * @param plugin the owning plugin instance; used by implementations that need a plugin
     *               for platform services (currently the 1.13 implementation)
     */
    public YggraActionBar(JavaPlugin plugin)  {
        String implClassName = getImplClassName();
        try {
            if (VersionUtils.getServerVersion().startsWith("1.13"))
                _bar = (IActionBar) Class.forName(implClassName)
                        .getDeclaredConstructor(JavaPlugin.class).newInstance(plugin);
            else
                _bar = (IActionBar) Class.forName(implClassName)
                        .getDeclaredConstructor().newInstance();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component component) {
        if (_bar == null)
            return;
        _bar.send(player, component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String message) {
        if (_bar == null)
            return;
        _bar.send(player, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters) {
        if (_bar == null)
            return;
        _bar.send(player, message, parameters);
    }

    /**
     * Resolves the fully qualified class name of the implementation for the running server.
     *
     * @return the fully qualified implementation class name, never {@code null}
     * @throws RuntimeException if the server version is older than 1.8 and therefore unsupported
     */
    private static @NotNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(1, 16, 5))
            implClassName = "io.github.tavstaldev.yggraActionBar.impl.v1_16_5.ActionBar";
        else if (VersionUtils.isAtLeast(1, 13))
            implClassName = "io.github.tavstaldev.yggraActionBar.impl.v1_13.ActionBar";
        else if (VersionUtils.isAtLeast(1, 12))
            implClassName = "io.github.tavstaldev.yggraActionBar.impl.v1_12.ActionBar";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "io.github.tavstaldev.yggraActionBar.impl.v1_8_8.ActionBar";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
