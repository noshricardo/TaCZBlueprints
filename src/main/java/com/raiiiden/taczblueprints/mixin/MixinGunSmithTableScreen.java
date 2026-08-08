package com.raiiiden.taczblueprints.mixin;

import com.raiiiden.taczblueprints.TaCZBlueprints;
import com.raiiiden.taczblueprints.attachment.IGunUnlocks;
import com.raiiiden.taczblueprints.attachment.ModAttachmentTypes;
import com.raiiiden.taczblueprints.config.BlueprintConfig;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IGun;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Mixin(value = GunSmithTableScreen.class, remap = false)
public class MixinGunSmithTableScreen {

    @Inject(method = "classifyRecipes", at = @At("RETURN"))
    private void onClassifyRecipes(CallbackInfo ci) {
        GunSmithTableScreen screen = (GunSmithTableScreen) (Object) this;
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        try {
            Field selectedRecipeListField = GunSmithTableScreen.class.getDeclaredField("selectedRecipeList");
            Field selectedRecipeField = GunSmithTableScreen.class.getDeclaredField("selectedRecipe");
            selectedRecipeListField.setAccessible(true);
            selectedRecipeField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<ResourceLocation> recipes = (List<ResourceLocation>) selectedRecipeListField.get(screen);
            if (recipes == null) return;

            RecipeManager recipeManager = mc.level.getRecipeManager();
            IGunUnlocks unlocks = mc.player.getData(ModAttachmentTypes.UNLOCKS);


                Set<String> unlockedGuns = unlocks.getUnlockedGuns();
                List<String> enabledGuns = BlueprintConfig.SERVER.getEnabledGuns();
                List<String> enabledAttachments = BlueprintConfig.SERVER.getEnabledAttachments();
                List<String> enabledAmmo = BlueprintConfig.SERVER.getEnabledAmmo();

                boolean enableGunTable = BlueprintConfig.SERVER.enableGunTable.get();
                boolean enableAttachmentTable = BlueprintConfig.SERVER.enableAttachmentTable.get();
                boolean enableAmmoTable = BlueprintConfig.SERVER.enableAmmoTable.get();

                Iterator<ResourceLocation> it = recipes.iterator();
                int removedCount = 0;

                while (it.hasNext()) {
                    ResourceLocation recipeId = it.next();
                    var recipeOpt = recipeManager.byKey(recipeId);
                    if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof GunSmithTableRecipe gunRecipe)) {
                        it.remove();
                        removedCount++;
                        continue;
                    }

                    ItemStack out = gunRecipe.getOutput();
                    Item item = out.getItem();
                    ResourceLocation baseItemId = BuiltInRegistries.ITEM.getKey(item);
                    String baseItem = baseItemId == null ? "null" : baseItemId.toString();

                    CustomData customData = out.get(DataComponents.CUSTOM_DATA);

                    CompoundTag tag = customData != null ? customData.copyTag() : null;
                    String nbtGunId = (tag != null && tag.contains("GunId")) ? tag.getString("GunId") : null;
                    nbtGunId = (tag != null && tag.contains("AttachmentId")) ? tag.getString("AttachmentId") : nbtGunId;
                    nbtGunId = (tag != null && tag.contains("AmmoId")) ? tag.getString("AmmoId") : nbtGunId;

                    // Normalize gun ID
                    String normalizedGunId = null;
                    if (nbtGunId != null && !nbtGunId.isEmpty()) {
                        ResourceLocation rl =ResourceLocation.parse(nbtGunId);
                        String path = rl.getPath();
                        if (!path.startsWith("gun/") && tag.contains("GunId")) path = "gun/" + path;
                        if (!path.startsWith("attachment/") && tag.contains("AttachmentId")) path = "attachment/" + path;
                        if (!path.startsWith("ammo/") && tag.contains("AmmoId")) path = "ammo/" + path;
                        normalizedGunId =ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), path).toString();
                    } else if (!"null".equals(baseItem)) {
                        ResourceLocation rl =ResourceLocation.parse(baseItem);
                        String path = rl.getPath();
                        if (!path.startsWith("gun/") && tag.contains("GunId")) path = "gun/" + path;
                        if (!path.startsWith("attachment/") && tag.contains("AttachmentId")) path = "attachment/" + path;
                        if (!path.startsWith("ammo/") && tag.contains("AmmoId")) path = "ammo/" + path;
                        normalizedGunId =ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), path).toString();
                    }

                    boolean remove = false;

                    // 🔹 Decide removal based on config
                    if (item instanceof IGun && !enableGunTable) {
                        remove = true;
                        if (normalizedGunId != null &&
                                (enabledGuns.contains(normalizedGunId) || unlockedGuns.contains(normalizedGunId))) {
                            remove = false; // Keep unlocked guns
                        }
                    } else if (item instanceof IAttachment && !enableAttachmentTable) {
                        remove = true;
                        if (!enabledAttachments.isEmpty() && enabledAttachments.contains(baseItem) || unlockedGuns.contains(normalizedGunId)) remove = false;
                    } else if (item instanceof IAmmo && !enableAmmoTable) {
                        remove = true;
                        if (!enabledAmmo.isEmpty() && enabledAmmo.contains(baseItem) || unlockedGuns.contains(normalizedGunId)) remove = false;
                    }

                    if (remove) {
                        it.remove();
                        removedCount++;
                    }
                }

                // TaCZBlueprints.LOGGER.debug("[Blueprint] Filtered out {} recipes, {} remaining", removedCount, recipes.size());

                // Re-select recipe
                try {
                    Object currentSelectedRecipe = selectedRecipeField.get(screen);
                    if (currentSelectedRecipe instanceof RecipeHolder<?> currentRecipe) {
                        if (recipes.contains(currentRecipe.id())) return;
                    }

                    if (recipes.isEmpty()) {
                        selectedRecipeField.set(screen, null);
                    } else {
                        ResourceLocation firstRecipeId = recipes.get(0);
                        Method getSelectedRecipeMethod = GunSmithTableScreen.class.getDeclaredMethod("getSelectedRecipe", ResourceLocation.class);
                        getSelectedRecipeMethod.setAccessible(true);
                        Object selectedRecipe = getSelectedRecipeMethod.invoke(screen, firstRecipeId);
                        selectedRecipeField.set(screen, selectedRecipe);
                    }
                } catch (ReflectiveOperationException e) {
                    TaCZBlueprints.LOGGER.error("[Blueprint] Error updating selected recipe", e);
                }


        } catch (Exception e) {
            TaCZBlueprints.LOGGER.error("[Blueprint] classifyRecipes mixin failed", e);
        }
    }
}
