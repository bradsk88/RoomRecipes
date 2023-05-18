package ca.bradj.roomrecipes.recipes;

import ca.bradj.roomrecipes.RoomRecipes;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.primitives.Ints;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomRecipe implements Recipe<Container>, Comparable<RoomRecipe> {
    private final int recipeStrength;

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String toString() {
        return "RoomRecipe{" +
                "id=" + id +
                ", recipeItems=" + recipeItems +
                '}';
    }

    private final ResourceLocation id;
    private final NonNullList<Ingredient> recipeItems;

    public RoomRecipe(
            ResourceLocation id,
            NonNullList<Ingredient> recipeItems,
            int recipeStrength
    ) {
        this.id = id;
        this.recipeItems = recipeItems;
        this.recipeStrength = recipeStrength;
    }

    @Override
    public boolean matches(
            Container inv,
            Level p_77569_2_
    ) {
        List<Ingredient> found = new ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack item = inv.getItem(i);
            Ingredient foundIng = null;
            for (Ingredient ing : recipeItems) {
                if (ing.test(item)) {
                    foundIng = ing;
                    break;
                }
            }
            if (foundIng != null) {
                found.add(foundIng);
            }
        }
        ImmutableMultiset<JsonElement> foundMS = ImmutableMultiset.copyOf(found.stream()
                .map(Ingredient::toJson)
                .collect(Collectors.toList()));
        ImmutableMultiset<JsonElement> recipeMS = ImmutableMultiset.copyOf(recipeItems.stream()
                .map(Ingredient::toJson)
                .collect(Collectors.toList()));
        return foundMS.size() >= recipeMS.size() && foundMS.containsAll(recipeMS);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack assemble(Container p_77572_1_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(
            int p_43999_,
            int p_44000_
    ) {
        return true;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipesInit.ROOM_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipesInit.ROOM;
    }

    @Override
    public int compareTo(@NotNull RoomRecipe roomRecipe) {
        int compare = Ints.compare(getIngredients().size(), roomRecipe.getIngredients().size());
        if (compare == 0) {
            return Ints.compare(this.recipeStrength, roomRecipe.recipeStrength);
        }
        return compare;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RoomRecipe> {

        @Override
        public RoomRecipe fromJson(
                ResourceLocation recipeLoc,
                JsonObject recipeJson,
                ICondition.IContext context
        ) {
            return this.fromJson(recipeLoc, recipeJson);
        }

        @Override
        public @NotNull RoomRecipe fromJson(
                @NotNull ResourceLocation recipeId,
                @NotNull JsonObject json
        ) {
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            int strength = 1;
            if (json.has("recipe_strength")) {
                strength = json.get("recipe_strength").getAsInt();
            }

            return new RoomRecipe(recipeId, inputs, strength);
        }

        @Nullable
        @Override
        public RoomRecipe fromNetwork(
                @NotNull ResourceLocation recipeId,
                FriendlyByteBuf buffer
        ) {
            int rSize = buffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(rSize, Ingredient.EMPTY);
            for (int i = 0; i < rSize; i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }
            int recipeStrength = buffer.readInt();
            return new RoomRecipe(recipeId, inputs, recipeStrength);
        }

        @Override
        public void toNetwork(
                FriendlyByteBuf buffer,
                RoomRecipe recipe
        ) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buffer);
            }
            buffer.writeInt(recipe.recipeStrength);
        }
    }

    public static class Type implements RecipeType<RoomRecipe> {

        public static final Type INSTANCE = new Type();
        public static final ResourceLocation ID = new ResourceLocation(RoomRecipes.MODID, "room");

    }

}
