<img align="right" src="test.png">

# Arc
Arc is an anticheat built to support older PvP versions and newer game versions.

* Supports 1.8.8, 1.12.2 and 1.16.5
* Customizable check configurations
* Extensive configuration
* Extensive permissions
* Performant
* API
##

<img align="right" src="violationdebug.png">

* Customizable violation messages
* Customizable violation parameters
* Customizable violation actions
* Customizable notify levels for each check
* Hovering debug system
* Violation data kept on logout
* Notified on player kicks/bans
* Ability to toggle on/off


##

<img align="right" src="https://i.imgur.com/P4otxe9.png">

* WIP Inventory management system
* Toggle violations
* Reload configuration
* View timings

... and more!

# Installing
* Arc *requires* Java 8.
* Arc *requires* ProtocolLib
* Compatible with Spigot and PaperSpigot!

# Checks

* `Combat` checks
  * KillAura
    * Direction
    * Attack Speed
    * WIP.
  * Criticals
  * Reach
  * NoSwing
* `Moving` checks
  * Flight
    * Contains multiple related checks but is still WIP.
    * Ascending too high (Spider)
    * Ascending too fast (Spider)
    * HighJump
    * Clipping through blocks
    * FastLadder
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
  * Still WIP

*...All checks are still work in progress and subject to change*

# Development

View the [trello board](https://trello.com/b/Ytgv320C/arc) to see in-progress items and TODO.

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
 

# Preview

### Configuration
<details>
  <summary>
    Full configuration
  </summary>
```
   # The global kick message. Players will see this message when kicked.
# Valid parameters:
# - %check%
global-kick-message: "&cYou have been kicked for %check%"

# The global kick delay in seconds.
global-kick-delay: 0

# The message to send to players with the permission `arc.violations`
# Valid parameters:
# - %prefix% - the prefix
# - %player% - The player name
# - %check% - The check name
# - %time% - The time banned for
global-violations-kick-message: "%prefix% &9%player%&f will be kicked for &c%check%&f in &c%time%&f seconds."

# The global ban message. Players will see this message when banned.
# Valid parameters
# - %check%
global-ban-message: "&cYou have been banned for %check%"

# The global ban delay in seconds.
global-ban-delay: 0

# The global ban type.
# Valid parameters:
# - NAME
# - IP
global-ban-type: NAME

# The global ban length type.
# Valid parameters:
# - DAYS - Days
# - YEARS - Years
# - PERM - Permanent
global-ban-length-type: DAYS

# How long a player should be banned for.
global-ban-length: 30

# If the ban should be broadcasted.
global-broadcast-ban: false

# The message to broadcast if `global-broadcast-ban` is `true`
# Valid parameters:
# - %prefix% - The prefix
# - %player% - The player name
# - %check% - The check name
# - %time% - The time banned for
# - %type% - Days, Years, Permanent
# Example: Skiddin(%player%) was banned for Speed(%check%) for 30(%time%) days(%type%)
global-broadcast-ban-message: "%prefix% &c%player% was banned for %check% for %time% %type%"

# The message to send to players with the permission `arc.violations`
# Valid parameters:
# - %prefix% - the prefix
# - %player% - The player name
# - %check% - The check name
# - %time% - The time banned for
global-violations-ban-message: "%prefix% &9%player%&f will be banned for &c%check%&f in &c%time%&f seconds."

# If check timings should be enabled.
enable-check-timings: true

# If the TPS helper should be enabled.
enabled-tps-helper: true

# If the TPS drops below this amount TPS helper will engage.
tps-helper-limit: 17

# The message to display when a violation occurs
# Valid parameters:
# - %prefix% - the prefix
# - %player% - The player name
# - %check% - The check name
# - %level% - the violation level
violation-notify-message: "%prefix% &9%player%&f has violated check &c%check%&8(&c%level%&8)&7"

# The message to display when the sender has no permission for the /arc command.
arc-command-no-permission-message: "Unknown command. Type /help for help."

# The prefix to show in chat.
arc-prefix: "&8[&cArc&8]"

# The amount of time after a player leaves their violation data will be removed, in minutes.
violation-data-timeout: 30

# If the event API should be enabled.
enable-event-api: true

# If debug messages are enabled:
debug-messages: false

# If AdvancedBan plugin should be used for banning/kicking.
use-advanced-ban: false

# If LiteBans plugin should be used for banning/kicking.
use-lite-bans: false

# The lite bans command to execute.
# For example: /ban Player -s 7d cheating
# %player% - the player
# Any liteban flag you may want, for example -s
# %length% - the length of the ban
# %reason% - the reason
lite-bans-command: "%player% -p %length% %reason%"
Criticals:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  minimum-distance-allowed: 0.099
  max-no-movement-allowed: 3
  max-similar-movement-allowed: 3
  min-similar-movement-difference: 0.05
MorePackets:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: true
  ban-level: 20
  kick: false
  kick-level: 0
  max-flying-packets-per-second: 25
  max-position-packets-per-second: 25
  max-look-packets-per-second: 25
  kick-if-threshold-reached: true
  packet-kick-threshold: 50
NoFall:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  expected-fall-distance-tolerance: 1.0
  invalid-ground-moves-allowed: 50
  distance-fallen-threshold: 2.5
PayloadFrequency:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: true
  ban-level: 5
  kick: true
  kick-level: 2
  max-packet-size-books: 4096
  max-packet-size-others: 32767
  check-interval-milliseconds: 1000
  max-packets-per-interval: 1
  max-packet-size-kick: true
  max-packets-per-interval-kick: true
  channels:
  - MC|BSign
  - MC|BEdit
SwingFrequency:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: true
  ban-level: 10
  kick: true
  kick-level: 5
  max-packets-per-second: 50
  kick-if-threshold-reached: false
  packet-kick-threshold: 100
Regeneration:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  version-1-8-8:
    regeneration-time-minimum: 3400
  version-1-12:
    regeneration-time-minimum: 450
  version-1-16:
    regeneration-time-minimum: 450
FastUse:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  fastbow:
    use-delta-min: 100
    delta-shot-min: 200
  fastconsume:
    consume-time-ms: 1400
Jesus:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  time-in-liquid-required: 3
  max-setback-distance: 2
  time-ascending-required: 1
  time-in-liquid-required-distance-checking: 3
  max-no-distance-change-allowed: 3
  ascending-min-distance-required: 0.12
  ascending-min-difference-distance: 0.05
AttackReach:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  max-survival-distance: 4.0
  max-creative-distance: 6.5
  ignore-vertical-axis: true
  subtract-eye-height: true
  default-eye-height: 1.75
  subtract-player-velocity: true
  subtract-entity-velocity: true
NoSwing:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  version-1-8-8:
    swing-time: 100
  version-1-12:
    swing-time: 1000
  version-1-16:
    swing-time: 1000
KillAura:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  direction:
    max-yaw-difference: 75
    max-pitch-difference: 75
  attackspeed:
    max-attacks-per-second: 20
    min-attack-delta: 35
Flight:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  max-jump-distance: 0.42
  max-climbing-speed-up: 0.12
  max-climbing-speed-down: 0.151
  climbing-cooldown: 5
  max-ascend-time: 7
  jump-boost-ascend-amplifier: 3
  ground-distance-threshold: 1.25
  ground-distance-horizontal-cap: 0.5
  slime-block-distance-fallen-threshold: 0
  vertical-clip-vertical-minimum: 0.99
  safe-location-update-distance-threshold: 1.99
Speed:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 1
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  base-move-speed-sprinting: 0.2873
  base-move-speed-walking: 0.2166
  base-move-speed-sneaking: 0.0666
  cancel-large-movements: true
  large-movements: 3
  teleport-cooldown-ms: 500
  ice-slipperiness: 0.98
  sneak-time-delay-ice: 20
  sneak-time-delay: 15
BlockBreakReach:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  survival-distance: 5.2
  creative-distance: 6.0
BlockPlaceReach:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  survival-distance: 5.2
  creative-distance: 6.0
BlockInteractReach:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  survival-distance: 5.2
  creative-distance: 6.0
BlockBreakNoSwing:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  version-1-8-8:
    swing-time: 100
  version-1-12:
    swing-time: 1000
  version-1-16:
    swing-time: 1000
BlockPlaceNoSwing:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  version-1-8-8:
    swing-time: 100
  version-1-12:
    swing-time: 1000
  version-1-16:
    swing-time: 1000
BlockInteractNoSwing:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: false
  ban-level: 0
  kick: false
  kick-level: 0
  version-1-8-8:
    swing-time: 100
  version-1-12:
    swing-time: 1000
  version-1-16:
    swing-time: 1000
Nuker:
  enabled: true
  cancel: true
  cancel-level: 0
  notify: true
  notify-every: 4
  ban: true
  ban-level: 1000
  kick: true
  kick-level: 500
  packet-check: true
  creative-only: true
  max-breaks-per-second: 15
  min-delta-between-breaks: 50
  min-delta-between-breaks-threshold: 5
  check-packets: true
  max-break-packets-per-second: 50
  kick-if-threshold-reached: false
  packet-kick-threshold: 100
```
</details>
