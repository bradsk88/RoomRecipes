package ca.bradj.roomrecipes.recipes;

import com.google.common.collect.ImmutableMultiset;

import java.util.ArrayList;
import java.util.Collection;

public class NoMCRomRecipe {
    public static <S> boolean foundMatchRecipe(
            ImmutableMultiset<S> foundMS,
            ImmutableMultiset<S> recipeMS
    ) {
        if (foundMS.size() < recipeMS.size()) {
            return false;
        }
        Collection<S> found = new ArrayList<>(recipeMS);
        for (S m : foundMS) {
            found.remove(m);
        }
        return found.isEmpty();
    }
}
