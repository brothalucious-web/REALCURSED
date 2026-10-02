# Cursed Craft

A Fabric mod for Minecraft 26.2 that makes the game wonderfully cursed.

## What works right now
- **Eat everything:** sneak + right-click the air with any non-food item. Swords give Strength (and cost health), TNT explodes, cobblestone fills you up but slows you, helmets, boots, elytra and more have their own effects. Bedrock and command blocks hurt.
- **Eat mobs:** sneak + right-click a mob with an empty hand. Each mob has its own effect, babies refuse, and bitten mobs run away.
- **Cursed death drops:** cows drop a bone, creepers give nearby players a free hug (hearts + heal).
- **Chaos block drops:** every block drops a random item (off by default, see `CursedConfig`).

## Planned (see IDEAS.md)
Cursed fishing, swapped controls, item name swaps, backwards crafting, cursed weather and dimensions, the deep-fried texture pack, sound swaps, and more.

## Building
Requires Java 25.

```
gradle build        # or ./gradlew build once you add the wrapper (run `gradle wrapper`)
gradle runClient
```

The jar lands in `build/libs/`. Pushing to GitHub runs the `build` workflow; pushing a tag like `v0.1.0` runs `release` and attaches the jar.

## Toggles
Edit `CursedConfig.java` to switch features on or off.
