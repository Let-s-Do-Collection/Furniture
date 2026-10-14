[1.1.9]

**Fixed**
* Mirror: no more crash and lag with enchanted armor from other mods (for example RPG Series with Iris & Cosmetic Armor)
* Mirror: entities no longer turn invisible with shaders enabled
* Mirror: frees its memory when leaving the world or turning reflections off, and a broken reflection no longer takes down the others
* Pells: damaged Pells cant be picked up anymore, repair it first please! :)
* Compat blocks loot tables now only load when the compat mod is installed

*** 

[1.1.8]

**Fixed**
* Fish Tank: both halves now share the same fish
* Bin: full bins can be emptied with an empty hand, decoration mode now stops the bin from working instead of just suppressing particles
* Telescope: no more flickering head, hand is hidden and you cant walk around anymore while looking through it
* Mirror reflections are now off by default
* Pells: no more infinite wheat, repairing now needs a Hay Bale and broken parts drop only 6 wheat in total

**Changed**
* Updated Russian translation (thanks to Tefnya)

*** 

[1.1.7]

**Added**
* HUD info panels for Fish Tank, Wardrobe and Bin
* Notices when toggling lamps, adding fish to a Fish Tank and emptying a Bin
* Bin: iron and gold tools and armor can now be recycled into nuggets, comparators read the fill level, and full bins start to stink and attract flies. Shift right click to toggle particles & sounds in case you just want to have a full bin for purely decorative reasons
* Steam Vent now works as an extractor hood: it switches on automatically above lit stoves, campfires and furnaces and sucks up the steam
* Steam Pipe: connects in all directions for bends and junctions. Route a Steam Vent into a Chimney and watch the smoke come out on top
* Telescope: look through it with a strong zoom, check the moon phase and weather forecast in its info panel, and spot shooting stars at night
* Cash Register: right-click (or send a redstone signal) to open the drawer with a satisfying KA-CHING! Store emeralds, diamonds, gold and other valuables in its four compartments, use comparators to read the fill level, and place it next to or diagonally above a villager's workstation to shop with the villager working there (if hes around)
* Boat in a Jar: right-click to shake it!
* Mirrors are Mirroring now
* Boat in a Jar: rocks harder during thunderstorms, and a Water Bottle brews a tiny storm inside the jar
* Pouffe: bounce strength and fall damage are now configurable
* Grandfather Clock strikes twelve times at midnight and makes nearby mobs flinch
* Mirror: right-click with paper named "Mirror, Mirror" and ask who is the fairest of them all...
* Gramophone: disc 11 now feels as creepy as it sounds
* Fish Tank: cats now sit in front of it and stare at the fish
* Boat in a Jar: shake it too hard and the boat breaks. Oops!
* Compatibility layer can now be toggled per mod and per furniture type in the config
* Gramophone horn now pulses to the music, Displays rotate and bob their items, and Steam Pipes hiss and puff

**Changed**
* Trash Bag loot is now a data-driven loot table: mostly junk, sometimes small treasures, rarely the Furniture music discs, and still a chance for literally any item
* Compatibility layer items moved from their own creative tab into side tabs of the Furniture tab
* Cabinets, Dressers, Wardrobes and Shutters now use Foundation's shared implementations
* Trash Bag cooldown now uses the regular item cooldown and can be toggled in the config
* Chimneys now get sooty via random ticks and no longer need a block entity
* Pell's animation got a massive improvement. The more damage you deal, the more it falls apart. Repair it with wheat
* Cash Register got new textures, a proper moving drawer and its own KA-CHING!! sound
* Grandfather Clock got a new ticking sound, and its volume is configurable

**Fixed**
* Cabinets not closing again when the opening player disconnects or moves away
* Missing loot tables and mineable tags for Blooming Nature benches and cabinets (thanks to Awoo)
* Missing `en_us` entry for Pine Bench (thanks to Awoo again :))
* Fish Tanks dropping nothing when the second half is broken
* Grandfather Clock hour showing the wrong time because one hand was off by 6 hours
* Armor stored in Wardrobes disappearing when the top half is broken
* Wardrobes not breaking faster with an axe
* Plaid Pouffe using the Linen texture. Its very ugly now though
* Missing recipes for Larch Shutter, Palm Desk, Palm Dresser and Palm Wardrobe
* Missing loot tables for Dark Cherry and Arolla Pine furniture, Palm Bench, Palm Cabinet, Homespun Lamp and Homespun Pouffe
* Compat loot tables living in the wrong folder :D causing lots of compat furniture to drop nothing

***

[1.1.6]

**Fixed**
* Clock rendering invisible with shader packs enabled
* Grandfather Clock's inner side walls z-fighting

***

[1.1.5]

**Fixed**
* Empty CompoundTag not returned in getUpdateTag, causing sync issues (thanks to Osmond Lee)
* Meadow and Alpine Whispers curtains not showing connected textures when stacked
* Homespun curtain (Alpine Whispers) not dropping when broken
* Wardrobe items duplicating when broken as part of a Sable physics contraption
* Grandfather Clock's hour and minute hands were swapped
* Canvas paintings rendering at the wrong scale, requiring far larger walls than intended
* Brick and Stone Bricks Chimney using iron block properties (hardness, sound) instead of their own material
* Coffer placed underwater not staying waterlogged
* Fish Tank water rendering with doubled, darker translucency

**Added**
* Sable EntityType tags for sub-level retention/destruction compatibility (thanks to BeeIsYou)
* Italian translation (thanks to Serena)

***

[1.1.4]

**Fixed** 
* Removed unused recipes 
* Crash when using the turkish language pack

**Added**
* Compatibility layer for BloomingNature, Meadow, AlpineWhispers and Beachparty. When these mods are installed, you can craft furniture made from their woods.

**Changed**
* Updated ru_ru (tefnya)

***

[1.1.3]

**Added**
* KO_KR translation (thanks to cs8minty)

**Fixed**
* Gramophone repeat mode stopping after one track

***

[1.1.2]

**Fixed**
* World save hang when quitting with an active Gramophone

***

[1.1.1]

**Fixed**
* Crash when saving players with Coffer items in their inventory (missing BlockEntity id in NBT)

***

