<img align="right" src="test.png">

# Arc
Arc is an anticheat built to support older PvP versions and newer game versions. The goal of Arc is to be extremely configurable, performant, compatible, and effective. As of right now Arc is still very experimental and there are alot of things still missing.

* Extensive configuration and permissions
* Supports 1.8, 1.12 and 1.16
* LiteBans support
* Performant
* API

Arc **requires** Java 8 and ProtocolLib to function. Arc is compatible with either Spigot or PaperSpigot.



# Checks

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

*...All checks are still work in progress and subject to change...*

# Development

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

# Documentation

All the documentation is still work-in-progress and very incomplete.

Although, most things within the code are very clear and documented.

* Here are the various documentation links
  * [Commands](https://github.com/Vrekt/Arc/wiki/Commands) 
  * [API](https://github.com/Vrekt/Arc/wiki/API)
  * [Configuration](https://github.com/Vrekt/Arc/wiki/Configuration)
  * [Check Configuration](https://github.com/Vrekt/Arc/wiki/Check-configuration)
  * [Configuration Preview](https://github.com/Vrekt/Arc/wiki/Configuration-Preview)
 

# Preview
Check out the default configuration file [here](https://github.com/Vrekt/Arc/wiki/Configuration-Preview).

NOTE: Comments are not included once the configuration is written to the plugin directory.
