# Arc
An anticheat built for PaperSpigot.

# Supported Versions
- 1.8.8 *
- 1.15.2
- 1.16.4

*1.8.8 is the main supported version of Arc. All checks will be developed FIRST for 1.8.8

# Whats Required
- ProtocolLib
- Java 8

# Current Features
- Customizable checks and check parameters.
- Permissions for each type of check and category.
- Configurable punishment
- Configurable messages
- Basic API
- More to come...

# Documentation
You can find general configuration info [here](https://github.com/Vrekt/Arc/wiki/Configuration)

You can find check configuration info [here](https://github.com/Vrekt/Arc/wiki/Check-configuration)

You can find API documentation [here](https://github.com/Vrekt/Arc/wiki/API)

# Current Checks
### Combat
- KillAura
  - Direction
  - TODO.
- Criticals
- Reach
### Moving
- MorePackets
- NoFall
- Jesus
### Network
- Swing Frequency (Server Crashing)
- Payload Frequency (Server Crashing)
### Player
- Regeneration
- FastUse
  - Fast Consume
  - Fast Bow

# Focus
- [ ] Sub-type configurations
- [ ] Patch more crashing exploits
- [ ] Lag Checking #3 - After 6 more checks
- [ ] Timings
- [ ] Lag/TPS watch
- [ ] Inventory UI
- [ ] Player summaries

# Finished
- [x] CONVERT CODE BACK TO JAVA 8 FROM 11.
- [x] Permissions for each check to bypass
- [x] ~Improve MorePackets~
- [x] ~Workaround /effect for BadEffects check~
- [x] ~Fix BadEffects potion duration~
- [x] ~Better information system with violations~
- [x] Debug system
- [x] Debug/Information commands to toggle
- [x] Commands in general
- [x] Exemptions 
- [x] More checks, obviously
- [x] Better permissions
- [x] Improve NoFall 
- [x] Lag Checking/Refactoring before continuing
- [x] Lag Checking #2 - After 3 more checks
- [x] One listener for all combat checks
- [x] Improve/Fix reach (add velocity? ) IDK
