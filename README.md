<img align="right" src="test.png">

# Arc
Arc is an anticheat built to support older PvP versions and newer game versions. The goal of Arc is to be extremely configurable, performant, compatible, and effective.

* Extensive configuration and permissions
* Supports 1.8, 1.12, 1.16, and 1.17!
* LiteBans support
* Performant
* API

### Installing

Arc is currently built with Java 8 and requires ProtocolLib to be present on the server. In the near future, builds using Java 16 will be available for 1.17+ game versions.

Head over to the [releases page](https://github.com/Vrekt/Arc/releases) and download the latest version. Then, place the jar file into your plugin directory.

##### ProtocolLib
You can find ProtocolLib [here](https://www.spigotmc.org/resources/protocollib.1997/).

##### Compatibility
Arc is compatible with either `Spigot` or `PaperSpigot`. Other software or forks are untested, so use at your own risk.

### Checks

Arc is still experimental regardless, improvements and features are worked on every day. As such, expect some things to be totally missing or broken.

* `Combat` checks
  * KillAura
  * Criticals
  * Reach
  * NoSwing
* `Moving` checks
  * Flight
  * NoFall
  * MorePackets
  * Jesus
* `Network` checks
  * Swing packet checking
  * Payload packet checking
* `Player` checks
  * Regeneration
  * FastUse
    * FastBow
    * FastConsume
* `Block` checks
  * Reach
    * Breaking
    * Placing
    * Interaction
  * NoSwing
    * Breaking
    * Placing
    * Interaction
  * Nuker

### Development

Visit the [projects board](https://github.com/Vrekt/Arc/projects) to see in-progress items and TODO.

* You can submit pull requests at any time to implement a feature or change.
  * Please ensure the code is quality and readable
  * Be sure to thoroughly test and make sure no other systems interfere.
  * Documentation should be included.

* To work on Arc yourself, the following things are required:
  * A PaperSpigot 1.8 jar with NMS.
  * A CraftBukkit 1.15.2 jar - which can be obtained from BuildTools using `--compile craftbukkit`
  * A CraftBukkit 1.16.5 jar - which can be obtained from BuildTools using `--compile craftbukkit`
  * Each CraftBukkit jar must be placed in their respective `bridgex_x` directory.

### Documentation

All the documentation is still work-in-progress and very incomplete.

Although, most things within the code are very clear and documented.

* Here are the various documentation links
  * [Commands](https://github.com/Vrekt/Arc/wiki/Commands) 
  * [API](https://github.com/Vrekt/Arc/wiki/API)
  * [Configuration](https://github.com/Vrekt/Arc/wiki/Configuration)
  * [Check Configuration](https://github.com/Vrekt/Arc/wiki/Check-configuration)
  * [Configuration Preview](https://github.com/Vrekt/Arc/wiki/Configuration-Preview)
