# Arc
An anticheat for legacy Spigot version 1.8.8

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
- Criticals
- Reach
### Moving
- MorePackets
- NoFall
- Jesus (Water Walk/LiquidWalk)
### Network
- Swing Frequency (Server Crashing)
- Payload Frequency (Server Crashing)
### Player
- Regeneration
- FastUse (FastConsume) (FastBow)

# Future TODO
- [ ] 1.16 Support

# TODO LIST

- [x] CONVERT CODE BACK TO JAVA 8 FROM 11.
- [x] Permissions for each check to bypass
- [ ] Improve MorePackets
- [ ] Patch more crashing exploits
- [x] ~Workaround /effect for BadEffects check~
- [x] ~Fix BadEffects potion duration~
- [x] ~Better information system with violations~
- [x] Debug system
- [x] Debug/Information commands to toggle
- [x] Commands in general
- [x] Exemptions 
- [ ] More checks, obviously
- [x] Better permissions
- [x] Improve NoFall 
- [x] Lag Checking/Refactoring before continuing
- [ ] Lag Checking #2 - After 3 more checks
- [ ] Lag Checking #3 - After 6 more checks
- [ ] Timings
- [ ] Lag/TPS watch
- [ ] Inventory UI
- [ ] Player summaries
- [ ] One listener for all combat checks
- [ ] Improve/Fix reach (add velocity? ) IDK
