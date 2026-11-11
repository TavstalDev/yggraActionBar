# yggraActionBar

![Release (latest by date)](https://img.shields.io/github/v/release/TavstalDev/yggraActionBar?style=plastic-square)
![Workflow Status](https://img.shields.io/github/actions/workflow/status/TavstalDev/yggraActionBar/release.yml?branch=stable&label=build&style=plastic-square)
![License](https://img.shields.io/github/license/TavstalDev/yggraActionBar?style=plastic-square)
![Downloads](https://img.shields.io/github/downloads/TavstalDev/yggraActionBar/total?style=plastic-square)
![Stars](https://img.shields.io/github/stars/TavstalDev/yggraActionBar?style=plastic-square)
![Issues](https://img.shields.io/github/issues/TavstalDev/yggraActionBar?style=plastic-square)
![Forks](https://img.shields.io/github/forks/TavstalDev/yggraActionBar?style=plastic-square)

A small, version-independent library for sending **action bar** messages from Spigot/Paper plugins.
It picks the right implementation for the running server at runtime, so your plugin does not need
its own NMS or version-specific code.

## Features

- One API for Minecraft **1.8 through 26.2+**.
- Send either a pre-built Adventure `Component` or a formatted `String`.
- Built-in placeholder replacement using `%key%` syntax.
- Automatic server version detection with a graceful fallback when no implementation is available.
- A single shaded JAR that contains every supported implementation.
- No need to shade Adventure: load it at runtime with Libby or a similar dependency manager.
- Ready-to-use example plugin for manual testing.

## Supported versions

| Minecraft version | Implementation | How it works |
| --- | --- | --- |
| 1.16.5+ | `impl_v1_16_5` | Native `Player#sendActionBar(Component)` API |
| 1.13 – 1.16.4 | `impl_v1_13` | Adventure platform for Bukkit |
| 1.12 – 1.12.2 | `impl_v1_12` | NMS `PacketPlayOutChat` with `ChatMessageType.GAME_INFO` |
| 1.8 – 1.11.2 | `impl_v1_8_8` | NMS `PacketPlayOutChat` with chat type `2` |

The implementation is selected once, when `YggraActionBar` is constructed. If the server version is
not supported, or the implementation cannot be loaded, calls are ignored instead of crashing your
plugin.

## Project structure

| Module | Purpose |
| --- | --- |
| `api` | The public `IActionBar` interface and `AdventureUtils` helpers |
| `bootstrap` | Version detection and the `YggraActionBar` facade |
| `implementation/impl_v1_8_8` | 1.8 – 1.11.2 implementation |
| `implementation/impl_v1_12` | 1.12 implementation |
| `implementation/impl_v1_13` | 1.13 – 1.16.4 implementation |
| `implementation/impl_v1_16_5` | 1.16.5+ implementation |
| `plugin-test` | Example plugin showing Libby runtime loading |

## Requirements

- A Spigot or Paper server running Minecraft **1.8 or newer**.
- **Java 8+** at runtime. All published modules target Java 8.
- **JDK 25** with the included Gradle wrapper to build the project.

## Building from source

```bash
./gradlew build
```

The library JAR is written to:

```text
bootstrap/build/libs/yggraActionBar-0.1.0.jar
```

This JAR already contains the API and all version-specific implementations. Adventure is not
bundled; it is loaded at runtime (see [Usage](#usage)).

To build the example plugin:

```bash
./gradlew :plugin-test:shadowJar
```

The result is `plugin-test/build/libs/plugin-test-1.0.0.jar`.

## Usage

### 1. Add the library to your plugin

Shade the produced library JAR into your plugin. If you build this repository as part of your
project, you can depend on the shadow configuration directly:

```kotlin
dependencies {
    implementation(project(":bootstrap", configuration = "shadow")) {
        isTransitive = false
    }
}
```

If you use the pre-built JAR, add it as a local dependency:

```kotlin
dependencies {
    implementation(files("libs/yggraActionBar-0.1.0.jar"))
}
```

### 2. Load Adventure at runtime

Do **not** shade Adventure into your plugin. It is a shared library and bundling it can cause class
loader conflicts with other plugins. Instead, declare it as `compileOnly` and load it at runtime
with a dependency manager such as [Libby](https://github.com/AlessioDP/libby) (SlimJar, Quark and
similar tools work the same way).

Add Libby and the Adventure artifacts to your build:

```kotlin
repositories {
    maven("https://repo.alessiodp.com/releases")
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.26.1")
    // Required only when you need to support Minecraft 1.13 – 1.16.4.
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")

    implementation("net.byteflux:libby-bukkit:1.3.2")
}

tasks.shadowJar {
    // Always relocate Libby to avoid conflicts with other plugins.
    relocate("net.byteflux", "your.plugin.libs.libby")
}
```

Then load the Adventure modules during `onEnable`, before creating `YggraActionBar`:

```java
import io.github.tavstaldev.yggraActionBar.api.IActionBar;
import io.github.tavstaldev.yggraActionBar.bootstrap.YggraActionBar;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    private IActionBar actionBar;

    private static final String[][] ADVENTURE_LIBRARIES = {
            {"examination-api", "1.3.0"},
            {"examination-string", "1.3.0"},
            {"adventure-key", "4.26.1"},
            {"adventure-api", "4.26.1"},
            {"adventure-text-serializer-legacy", "4.26.1"},
            {"adventure-text-minimessage", "4.26.1"},
            {"adventure-text-serializer-plain", "4.26.1"},
            {"adventure-text-serializer-gson", "4.26.1"}
    };

    @Override
    public void onEnable() {
        BukkitLibraryManager libraries = new BukkitLibraryManager(this);
        libraries.addMavenCentral();
        libraries.addSonatype();
        libraries.addRepository("https://jitpack.io");

        for (String[] artifact : ADVENTURE_LIBRARIES) {
            libraries.loadLibrary(Library.builder()
                    // "{}" prevents the string from being rewritten during relocation.
                    .groupId("net{}kyori")
                    .artifactId(artifact[0])
                    .version(artifact[1])
                    .build());
        }

        actionBar = new YggraActionBar(this);
    }
}
```

Libby loads each artifact individually and does not resolve transitive dependencies. For Minecraft
1.13 – 1.16.4 support, also load `adventure-platform-bukkit`.

### 3. Create the action bar facade

Once the Adventure libraries are loaded, create one `YggraActionBar` instance during `onEnable` and
reuse it for the lifetime of the plugin:

```java
actionBar = new YggraActionBar(this);
```

### 4. Send messages

```java
import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public void greet(Player player) {
    // Pre-built Adventure component.
    actionBar.send(player, Component.text("Welcome!", NamedTextColor.GOLD));

    // Plain string message.
    actionBar.send(player, "&6Welcome to the server!");

    // String message with placeholders.
    Map<String, Object> placeholders = new HashMap<>();
    placeholders.put("player", player.getName());
    placeholders.put("coins", 120);

    actionBar.send(player, "&e%player% &7| &6%coins% coins", placeholders);
}
```

Placeholder keys may be written with or without the surrounding percent signs, so both
`player` and `%player%` work as map keys.

## API overview

```java
public interface IActionBar {

    void send(Player player, Component component);

    void send(Player player, String message);

    void send(Player player, String message, Map<String, Object> parameters);
}
```

| Method | Description |
| --- | --- |
| `send(Player, Component)` | Displays a pre-built Adventure component. |
| `send(Player, String)` | Displays a formatted string without placeholders. |
| `send(Player, String, Map)` | Displays a formatted string after replacing `%key%` placeholders. |

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

## Contact

For issues or feature requests, use the
[GitHub issue tracker](https://github.com/TavstalDev/yggraActionBar/issues).
