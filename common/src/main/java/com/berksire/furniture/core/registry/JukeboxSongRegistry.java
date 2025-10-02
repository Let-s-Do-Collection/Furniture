package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public class JukeboxSongRegistry {

    public static final ResourceKey<JukeboxSong> CPHS_PRIDE = create("cphs_pride");
    public static final ResourceKey<JukeboxSong> LETSDO_THEME = create("letsdo_theme");

    private static ResourceKey<JukeboxSong> create(String string) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Furniture.identifier(string));
    }

}
