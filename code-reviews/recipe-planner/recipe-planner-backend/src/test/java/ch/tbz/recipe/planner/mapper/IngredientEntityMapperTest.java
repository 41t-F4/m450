package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

/**
 * Reiner Unit-Test der von MapStruct generierten Mapper-Implementierung - ohne Spring-Kontext.
 * SoftAssertions: alle Feld-Pruefungen werden gesammelt und erst bei assertAll() gemeinsam gemeldet.
 * Vorteil: Bei einem falsch gemappten Mapper sehe ich in EINEM Testlauf alle fehlerhaften Felder,
 * nicht nur das erste (harte Assertions brechen beim ersten Fehler ab).
 */
class IngredientEntityMapperTest {

    private final IngredientEntityMapper mapper = Mappers.getMapper(IngredientEntityMapper.class);

    @Test
    void entityToDomain_mapsAllFields() {
        IngredientEntity entity = new IngredientEntity(UUID.randomUUID(), "Butter", "kalt", Unit.GRAMM, 250);

        Ingredient domain = mapper.entityToDomain(entity);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(domain.getId()).isEqualTo(entity.getId());
        softly.assertThat(domain.getName()).isEqualTo("Butter");
        softly.assertThat(domain.getComment()).isEqualTo("kalt");
        softly.assertThat(domain.getUnit()).isEqualTo(Unit.GRAMM);
        softly.assertThat(domain.getAmount()).isEqualTo(250);
        softly.assertAll();
    }

    @Test
    void domainToEntity_mapsAllFields() {
        Ingredient domain = new Ingredient(UUID.randomUUID(), "Milch", null, Unit.DECILITRE, 3);

        IngredientEntity entity = mapper.domainToEntity(domain);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(entity.getId()).isEqualTo(domain.getId());
        softly.assertThat(entity.getName()).isEqualTo("Milch");
        softly.assertThat(entity.getComment()).isNull(); // Grenzfall: optionales Feld bleibt null
        softly.assertThat(entity.getUnit()).isEqualTo(Unit.DECILITRE);
        softly.assertThat(entity.getAmount()).isEqualTo(3);
        softly.assertAll();
    }

    @Test
    void listMapping_keepsOrderAndSize() {
        List<IngredientEntity> entities = List.of(
                new IngredientEntity(UUID.randomUUID(), "A", null, Unit.PIECE, 1),
                new IngredientEntity(UUID.randomUUID(), "B", null, Unit.PIECE, 2));

        List<Ingredient> domains = mapper.entitiesToDomains(entities);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(domains).hasSize(2);
        softly.assertThat(domains.get(0).getName()).isEqualTo("A");
        softly.assertThat(domains.get(1).getName()).isEqualTo("B");
        softly.assertThat(mapper.entitiesToDomains(List.of())).isEmpty(); // Grenzfall: leere Liste
        softly.assertAll();
    }
}
