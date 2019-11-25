package com.tterrag.registrate.providers;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@FunctionalInterface
@SuppressWarnings("deprecation")
public interface ProviderType<T extends RegistrateProvider> {

    // CLIENT DATA
    public static final ProviderType<RegistrateItemModelProvider> ITEM_MODEL = register("item_model", (p, e) -> new RegistrateItemModelProvider(p, e.getGenerator(), e.getExistingFileHelper()));
    public static final ProviderType<RegistrateBlockstateProvider> BLOCKSTATE = register("blockstate", (p, e) -> new RegistrateBlockstateProvider(p, e.getGenerator(), e.getExistingFileHelper()));
    public static final ProviderType<RegistrateLangProvider> LANG = register("lang", (p, e) -> new RegistrateLangProvider(p, e.getGenerator()));

    // SERVER DATA
    public static final ProviderType<RegistrateRecipeProvider> RECIPE = register("recipe", (p, e) -> new RegistrateRecipeProvider(p, e.getGenerator()));
    public static final ProviderType<RegistrateLootTableProvider> LOOT = register("loot", (p, e) -> new RegistrateLootTableProvider(p, e.getGenerator()));
    public static final ProviderType<RegistrateTagsProvider<Block>> BLOCK_TAGS = register("tags/block", type -> (p, e) -> new RegistrateTagsProvider<Block>(p, type, "blocks", BlockTags::setCollection, e.getGenerator(), Registry.BLOCK));
    public static final ProviderType<RegistrateTagsProvider<Item>> ITEM_TAGS = register("tags/item", type -> (p, e) -> new RegistrateTagsProvider<Item>(p, type, "items", ItemTags::setCollection, e.getGenerator(), Registry.ITEM));
    public static final ProviderType<RegistrateTagsProvider<Fluid>> FLUID_TAGS = register("tags/fluid", type -> (p, e) -> new RegistrateTagsProvider<Fluid>(p, type, "fluids", FluidTags::setCollection, e.getGenerator(), Registry.FLUID));
    public static final ProviderType<RegistrateTagsProvider<EntityType<?>>> ENTITY_TAGS = register("tags/entity", type -> (p, e) -> new RegistrateTagsProvider<EntityType<?>>(p, type, "entity_types", EntityTypeTags::setCollection, e.getGenerator(), Registry.ENTITY_TYPE));

    T create(Registrate parent, GatherDataEvent event);
    
    static <T extends RegistrateProvider> ProviderType<T> register(String name, Function<ProviderType<T>, BiFunction<Registrate, GatherDataEvent, T>> type) {
        ProviderType<T> ret = new ProviderType<T>() {
            
            @Override
            public T create(Registrate parent, GatherDataEvent event) {
                return type.apply(this).apply(parent, event);
            }
        };
        return register(name, ret);
    }

    static <T extends RegistrateProvider> ProviderType<T> register(String name, ProviderType<T> type) {
        RegistrateDataProvider.TYPES.put(name, type);
        return type;
    }
}
