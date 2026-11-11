package io.github.tavstaldev.yggraActionBar.impl.v1_8_8;

import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link IActionBar} implementation for Minecraft 1.8.x servers.
 *
 * <p>Since the Bukkit API does not expose action bar support before 1.16, this
 * implementation uses reflection to construct a version-specific NMS
 * {@code PacketPlayOutChat} packet with chat type {@code 2} (the action bar) and
 * sends it directly through the player's connection.</p>
 *
 * <p>Messages are parsed with MiniMessage first and then serialised to legacy
 * section codes so the result can be wrapped in an NMS chat component.</p>
 */
@SuppressWarnings("CallToPrintStackTrace")
public class ActionBar implements IActionBar {
    private static final String VERSION =
            Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Method getHandle;
    private static Field playerConnectionField;
    private static Method sendPacket;
    private static Constructor<?> chatComponentTextCtor;
    private static Constructor<?> packetCtor;
    private static boolean ready;

    static {
        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
            Class<?> entityPlayer = nms("EntityPlayer");
            Class<?> playerConnection = nms("PlayerConnection");
            Class<?> packet = nms("Packet");
            Class<?> baseComponent = nms("IChatBaseComponent");
            Class<?> chatPacket = nms("PacketPlayOutChat");

            getHandle = craftPlayer.getMethod("getHandle");
            playerConnectionField = entityPlayer.getField("playerConnection");
            sendPacket = playerConnection.getMethod("sendPacket", packet);
            chatComponentTextCtor = nms("ChatComponentText").getConstructor(String.class);

            packetCtor = chatPacket.getConstructor(baseComponent, byte.class);
            ready = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resolves an NMS class for the detected server version.
     *
     * @param name the simple NMS class name, for example {@code EntityPlayer}
     * @return the resolved class
     * @throws ClassNotFoundException if the class does not exist for this server version
     */
    private static Class<?> nms(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component component) {
        String message = LegacyComponentSerializer.legacySection().serialize(component);
        send(player, message);
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
        if (!ready)
            return;

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

        Component miniComp = MiniMessage.miniMessage().deserialize(msg.replace('§', '&'));
        msg = LegacyComponentSerializer.legacySection().serialize(miniComp);

        try {
            String colored = ChatColor.translateAlternateColorCodes('&', msg);
            Object component = chatComponentTextCtor.newInstance(colored);
            Object packet = packetCtor.newInstance(component, (byte) 2); // 2 = action bar

            Object handle = getHandle.invoke(player);
            Object connection = playerConnectionField.get(handle);
            sendPacket.invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
