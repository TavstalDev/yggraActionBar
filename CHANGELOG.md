# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-11-11

Initial public release.

### Added

- Version-independent action bar API for Spigot/Paper plugins running Minecraft 1.8 through 26.2+.
- `IActionBar` interface with overloads for Adventure `Component`, plain `String`, and `String` with
  `%key%` placeholder replacement.
- Automatic server version detection through `VersionUtils`.
- Version-specific implementations:
  - Minecraft 1.8 – 1.11.2 using NMS `PacketPlayOutChat` with chat type `2`.
  - Minecraft 1.12 – 1.12.2 using NMS `PacketPlayOutChat` with `ChatMessageType.GAME_INFO`.
  - Minecraft 1.13 – 1.16.4 using the Adventure platform for Bukkit.
  - Minecraft 1.16.5+ using the native `Player#sendActionBar(Component)` API.
- `YggraActionBar` bootstrap facade that selects the correct implementation at runtime and safely
  ignores calls when an implementation cannot be loaded.
- `AdventureUtils` helpers for parsing legacy and MiniMessage text and converting components to
  plain text, MiniMessage, legacy section codes, and JSON.
- Shaded distribution JAR containing the API and all version-specific implementations.
- Example plugin demonstrating runtime Adventure loading with Libby.
- Javadoc for the public API, bootstrap facade, utilities, and version-specific implementations.
- Gradle build targeting Java 8 for published modules, with a Java 25 toolchain for building.
- GitHub Actions workflows for releases and label synchronization, plus Dependabot configuration.
- MIT license.

[Unreleased]: https://github.com/TavstalDev/yggraActionBar/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/TavstalDev/yggraActionBar/releases/tag/v0.1.0
