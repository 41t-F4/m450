package ch.tbz.recipe.planner.service;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;

/**
 * TDD-Feature "Einkaufsliste" (Projekt-Auftrag, siehe tdd/projekt-tdd-einkaufsliste.md).
 * Reiner Unit-Test ohne Spring: die Logik braucht weder DB noch Web-Layer -> schnelle Feedbackschleife.
 * Jeder Test = ein Zyklus Red -> Green -> Refactor, eine Regel pro Test.
 * Ab Zyklus 6 kommt das Repository dazu - als Mockito-Mock (Test Double, Block 05), der Mapper bleibt echt.
 */
@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock
    private RecipeRepository repository;

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);

    private ShoppingListService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new ShoppingListService(mapper, repository);
    }

    private static Recipe recipeWith(Ingredient... ingredients) {
        return new Recipe(UUID.randomUUID(), "Rezept", null, null, List.of(ingredients));
    }

    private static Ingredient ingredient(String name, Unit unit, int amount) {
        return new Ingredient(UUID.randomUUID(), name, "Kommentar", unit, amount);
    }

    // Zyklus 1
    @Test
    void createShoppingList_emptyInput_returnsEmptyList() {
        List<Ingredient> shoppingList = service.createShoppingList(List.of());

        assertThat(shoppingList).isEmpty();
    }

    // Zyklus 2
    @Test
    void createShoppingList_oneRecipeOneIngredient_isPassedThrough() {
        Recipe recipe = recipeWith(ingredient("Tomate", Unit.PIECE, 5));

        List<Ingredient> shoppingList = service.createShoppingList(List.of(recipe));

        assertThat(shoppingList)
                .extracting(Ingredient::getName, Ingredient::getUnit, Ingredient::getAmount)
                .containsExactly(tuple("Tomate", Unit.PIECE, 5));
    }

    // Zyklus 3
    @Test
    void createShoppingList_sameIngredientInTwoRecipes_amountsAreSummed() {
        Recipe pasta = recipeWith(ingredient("Tomate", Unit.PIECE, 5));
        Recipe salat = recipeWith(ingredient("Tomate", Unit.PIECE, 3));

        List<Ingredient> shoppingList = service.createShoppingList(List.of(pasta, salat));

        assertThat(shoppingList)
                .extracting(Ingredient::getName, Ingredient::getUnit, Ingredient::getAmount)
                .containsExactly(tuple("Tomate", Unit.PIECE, 8));
    }

    // Zyklus 4
    @Test
    void createShoppingList_sameNameDifferentUnit_staysSeparate() {
        Recipe brot = recipeWith(ingredient("Mehl", Unit.GRAMM, 500));
        Recipe pizza = recipeWith(ingredient("Mehl", Unit.KILOGRAMM, 1));

        List<Ingredient> shoppingList = service.createShoppingList(List.of(brot, pizza));

        assertThat(shoppingList)
                .extracting(Ingredient::getName, Ingredient::getUnit, Ingredient::getAmount)
                .containsExactly(tuple("Mehl", Unit.GRAMM, 500), tuple("Mehl", Unit.KILOGRAMM, 1));
    }

    // Zyklus 5 (Randfall: Mapper laesst ingredients == null, siehe RecipeEntityMapperTest)
    @Test
    void createShoppingList_recipeWithoutIngredients_isIgnored() {
        Recipe tee = new Recipe(UUID.randomUUID(), "Tee", null, null, null);
        Recipe salat = recipeWith(ingredient("Gurke", Unit.PIECE, 1));

        List<Ingredient> shoppingList = service.createShoppingList(List.of(tee, salat));

        assertThat(shoppingList)
                .extracting(Ingredient::getName)
                .containsExactly("Gurke");
    }

    // Zyklus 6: Rezepte per Id laden; unbekannte Id wird ignoriert (Entscheid: Einkaufsliste aus dem, was es gibt)
    @Test
    void createShoppingListForRecipeIds_loadsKnownRecipes_ignoresUnknownId() {
        UUID knownId = UUID.randomUUID();
        UUID unknownId = UUID.randomUUID();
        RecipeEntity entity = mapper.domainToEntity(recipeWith(ingredient("Zwiebel", Unit.PIECE, 2)));
        when(repository.findById(knownId)).thenReturn(Optional.of(entity));
        when(repository.findById(unknownId)).thenReturn(Optional.empty());

        List<Ingredient> shoppingList = service.createShoppingListForRecipeIds(List.of(knownId, unknownId));

        assertThat(shoppingList)
                .extracting(Ingredient::getName, Ingredient::getAmount)
                .containsExactly(tuple("Zwiebel", 2));
    }
}
