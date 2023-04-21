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

Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "un-renamed" MCP source code (aka
SRG Names) - this means that you will not be able to read them directly against
normal code.

Setup Process:
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `gradlew genEclipseRuns` (`./gradlew genEclipseRuns` if you are on Mac/Linux)
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `gradlew genIntellijRuns` (`./gradlew genIntellijRuns` if you are on Mac/Linux)
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
=============================
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license, if you do not agree with it you can change your mapping names to other crowdsourced names in your 
build.gradle. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md

Additional Resources: 
=========================
Community Documentation: http://mcforge.readthedocs.io/en/latest/gettingstarted/  
LexManos' Install Video: https://www.youtube.com/watch?v=8VEdtQLuLO0  
Forge Forum: https://forums.minecraftforge.net/  
Forge Discord: https://discord.gg/UvedJ9m  