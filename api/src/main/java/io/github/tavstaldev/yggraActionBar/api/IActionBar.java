package io.github.tavstaldev.yggraActionBar.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("unused")
public interface IActionBar {

    void send(@NotNull Player player, @NotNull Component component);

    void send(@NotNull Player player, @NotNull String message);

    void send(@NotNull Player player, @NotNull String message, @NotNull Map<String, Object> parameters);
}
