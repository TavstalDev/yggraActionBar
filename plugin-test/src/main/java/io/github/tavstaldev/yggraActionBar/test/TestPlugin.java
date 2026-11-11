package io.github.tavstaldev.yggraActionBar.test;

import io.github.tavstaldev.yggraActionBar.bootstrap.YggraActionBar;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {

    private YggraActionBar _bar;

    @Override
    public void onEnable() {
        _bar = new YggraActionBar(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("The plugin has been loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has been unloaded.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        _bar.send(event.getPlayer(), "&4Welcome §8| <blue>" + event.getPlayer().getDisplayName());
    }
}
