package net.lpcamors.optical.data;

import net.lpcamors.optical.COMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class COTags {

    public static class Blocks {

        public static final TagKey<Block> PENETRABLE = mod("beam/penetrable");
        public static final TagKey<Block> IMPENETRABLE = mod("beam/impenetrable");

        private static TagKey<Block> mod(String path){
            return BlockTags.create(new ResourceLocation(COMod.ID, path));
        }
    }

}
