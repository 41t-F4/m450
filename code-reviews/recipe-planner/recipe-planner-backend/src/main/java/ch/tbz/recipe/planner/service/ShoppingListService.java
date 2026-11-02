package ch.tbz.recipe.planner.service;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingListService {

    private final RecipeEntityMapper mapper;

    private final RecipeRepository repository;

    public ShoppingListService(RecipeEntityMapper mapper, RecipeRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    // Zyklus 6: unbekannte Ids werden ignoriert - die Liste entsteht aus den gefundenen Rezepten
    public List<Ingredient> createShoppingListForRecipeIds(List<UUID> recipeIds) {
        List<Recipe> recipes = recipeIds.stream()
                .map(repository::findById)
                .flatMap(Optional::stream)
                .map(mapper::entityToDomain)
                .toList();
        return createShoppingList(recipes);
    }

    public List<Ingredient> createShoppingList(List<Recipe> recipes) {
        Map<String, Ingredient> byNameAndUnit = new LinkedHashMap<>();
        for (Recipe recipe : recipes) {
            if (recipe.getIngredients() == null) {
                continue; // Zyklus 5: Rezept ohne Zutaten (Mapper liefert null statt leerer Liste)
            }
            for (Ingredient ingredient : recipe.getIngredients()) {
                byNameAndUnit.merge(keyOf(ingredient), toShoppingListLine(ingredient), ShoppingListService::sumAmounts);
            }
        }
        return new ArrayList<>(byNameAndUnit.values());
    }

    // Zyklus 3/4: gleicher Name + gleiche Einheit = eine Zeile; andere Einheit bleibt getrennt (keine Umrechnung)
    private static String keyOf(Ingredient ingredient) {
        return ingredient.getName() + "|" + ingredient.getUnit();
    }

    // Einkaufslisten-Zeile ist eine Kopie: id/comment des Rezept-Ingredients haben hier keine Bedeutung
    private static Ingredient toShoppingListLine(Ingredient ingredient) {
        return new Ingredient(null, ingredient.getName(), null, ingredient.getUnit(), ingredient.getAmount());
    }

    private static Ingredient sumAmounts(Ingredient existing, Ingredient added) {
        existing.setAmount(existing.getAmount() + added.getAmount());
        return existing;
    }
}
