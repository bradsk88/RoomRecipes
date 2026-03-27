# RoomRecipes

A core mod for Minecraft that adds the ability to detect "Rooms" of different
types based on their contents

## What is a room?
A room has the following qualities.
- It has walls. These can be any solid block.
- The walls are at two blocks tall.
   - You can build taller rooms, but anything above the second layer will not be
     checked as part of the recipe. (This will likely improve in the future)
- It has space inside. Air or any non-solid block.
- It has a door

## Recipes

Recipes can be added to `src/main/resources/data/<your-mod>/recipes` using a
custom recipe format that closely resembles vanilla Minecraft recipes.

### Recipe Example - Bedroom
This "bedroom" example checks for 1 torch and 2 bed blocks in an enclosed room.
(A Minecraft "bed" block is actually two blocks)

`bedroom.json`
```
{
  "type": "roomrecipes:room",
  "recipe_strength": 1,
  "ingredients": [
    {
      "item": "minecraft:torch"
    },
    {
      "tag": "minecraft:beds"
    },
    {
      "tag": "minecraft:beds"
    }
  ]
}
```

### Recipe Rules

Let's say you have two recipes. One that looks for a chest, and one that looks
for a chest **and** a furnace. These recipes "overlap". So, what kind of room
will be detected if the room contains a chest and a furnace?

The answer is the chest/furnace room. Here are some general rules:
1. recipes with more required items will be detected over recipes with fewer
   items. Assuming both recipes are "matches" for the current room.
2. `item` are worth more than `tag`s. So, for two same-sized recipes, the one
   with more `item`s will be detected.
3. There is an optional `recipe_strength` parameter that can be used to break
   a tie between two recipes. This should be used with caution because it will
   likely cause poor compatibility between different mods that use RoomRecipes.

## Publishing

### Modrinth

Versions are published to Modrinth using `./gradlew publishModrinth`.

**Setup:** Copy `.env.example` to `.env` and fill in your credentials:

```
MODRINTH_TOKEN=mrp_your_token_here
MODRINTH_PROJECT_ID=mvnAaWca
# CHANGELOG=Optional release notes
```

Get a token from [modrinth.com → Settings → Personal Access Tokens](https://modrinth.com/settings/pats)
with the **"Create versions"** scope. The `.env` file is gitignored.

The version number and type (`alpha`/`beta`/`release`) are derived automatically
from the branch name. The published jar is re-obfuscated via `reobfJar`.

### Maven Local

To publish a deobfuscated jar to your local Maven repository (for use as a
dev dependency by other mods):

```
./gradlew publishToMavenLocal
```
