package io.github.tavstaldev.yggraActionBar.bootstrap;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import io.github.tavstaldev.yggraActionBar.bootstrap.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("CallToPrintStackTrace")
public class YggraActionBar implements IActionBar {

    private IActionBar _bar;

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
