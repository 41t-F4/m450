package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

class RecipeEntityMapperTest {

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);

    @Test
    void entityToDomain_mapsAllFieldsIncludingNestedIngredients() {
        IngredientEntity egg = new IngredientEntity(UUID.randomUUID(), "Ei", null, Unit.PIECE, 2);
        RecipeEntity entity = new RecipeEntity(UUID.randomUUID(), "Omelette", "schnell", "https://img/o.jpg", List.of(egg));

        Recipe domain = mapper.entityToDomain(entity);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(domain.getId()).isEqualTo(entity.getId());
        softly.assertThat(domain.getName()).isEqualTo("Omelette");
        softly.assertThat(domain.getDescription()).isEqualTo("schnell");
        softly.assertThat(domain.getImageUrl()).isEqualTo("https://img/o.jpg");
        softly.assertThat(domain.getIngredients()).hasSize(1);
        // verschachteltes Mapping: MapStruct generiert den Ingredient-Mapper intern mit
        softly.assertThat(domain.getIngredients().get(0).getName()).isEqualTo("Ei");
        softly.assertThat(domain.getIngredients().get(0).getUnit()).isEqualTo(Unit.PIECE);
        softly.assertAll();
    }

    @Test
    void domainToEntity_mapsAllFields_andNullIngredientsStayNull() {
        Recipe domain = new Recipe(UUID.randomUUID(), "Tee", null, null, null);

        RecipeEntity entity = mapper.domainToEntity(domain);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(entity.getId()).isEqualTo(domain.getId());
        softly.assertThat(entity.getName()).isEqualTo("Tee");
        softly.assertThat(entity.getDescription()).isNull();
        softly.assertThat(entity.getImageUrl()).isNull();
        softly.assertThat(entity.getIngredients()).isNull(); // Grenzfall: keine Zutaten -> kein leeres List-Objekt
        softly.assertAll();
    }

    @Test
    void roundTrip_domainToEntityToDomain_isLossless() {
        Ingredient salt = new Ingredient(UUID.randomUUID(), "Salz", "Prise", Unit.GRAMM, 1);
        Recipe original = new Recipe(UUID.randomUUID(), "Suppe", "warm", "https://img/s.jpg", List.of(salt));

        Recipe back = mapper.entityToDomain(mapper.domainToEntity(original));

        // Lombok @Data liefert equals() ueber alle Felder -> ein Vergleich prueft das ganze Objekt
        org.assertj.core.api.Assertions.assertThat(back).isEqualTo(original);
    }
}
