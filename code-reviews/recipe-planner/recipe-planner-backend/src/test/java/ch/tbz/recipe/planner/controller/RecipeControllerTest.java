package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-Layer-Test (Test Level: Unit/Komponententest des Controllers).
 * {@link WebMvcTest} startet nur den MVC-Teil von Spring (Controller, JSON-Mapping, Routing),
 * kein JPA, keine h2 - darum schnell. Service und Mapper werden als Mocks ersetzt (Test Doubles),
 * so testen wir ausschliesslich das Verhalten des Controllers: Routing, Statuscodes, JSON.
 */
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService service;

    // Der Controller verlangt den Mapper im Konstruktor. WebMvcTest laedt keine @Component-Beans
    // (die MapStruct-Implementierung ist eine), darum muss er gemockt werden - sonst startet der Kontext nicht.
    @MockBean
    private RecipeEntityMapper mapper;

    // RecipePlannerApplication definiert eine @Bean CommandLineRunner init(RecipeRepository), die Testdaten seedet.
    // Die Application-Klasse wird auch im Web-Slice als Konfiguration geladen, das JPA-Repository aber nicht
    // -> Kontext-Start scheitert. Darum auch das Repository mocken (der Runner selbst laeuft im Test nicht).
    @MockBean
    private RecipeRepository repository;

    private static final UUID RECIPE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private Recipe sampleRecipe() {
        Ingredient flour = new Ingredient(UUID.randomUUID(), "Mehl", "Typ 405", Unit.GRAMM, 500);
        return new Recipe(RECIPE_ID, "Zopf", "Sonntagszopf", "https://img/zopf.jpg", List.of(flour));
    }

    @Test
    void getRecipes_returnsOkAndList() throws Exception {
        when(service.getRecipes()).thenReturn(List.of(sampleRecipe()));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Zopf"))
                .andExpect(jsonPath("$[0].ingredients[0].unit").value("GRAMM"));
    }

    @Test
    void getRecipe_byId_returnsOkAndRecipe() throws Exception {
        when(service.getRecipeById(RECIPE_ID)).thenReturn(sampleRecipe());

        mockMvc.perform(get("/api/recipes/recipe/{id}", RECIPE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RECIPE_ID.toString()))
                .andExpect(jsonPath("$.name").value("Zopf"));

        verify(service).getRecipeById(RECIPE_ID); // Pfad-Variable wurde korrekt in eine UUID geparst
    }

    @Test
    void addRecipe_postsJsonBody_returnsCreatedRecipe() throws Exception {
        Recipe input = sampleRecipe();
        input.setId(null); // Client schickt noch keine Id
        when(service.addRecipe(any(Recipe.class))).thenReturn(sampleRecipe());

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RECIPE_ID.toString()))
                .andExpect(jsonPath("$.ingredients[0].amount").value(500));
    }

    @Test
    void updateRecipe_existing_returnsOk() throws Exception {
        when(service.updateRecipe(eq(RECIPE_ID), any(Recipe.class))).thenReturn(sampleRecipe());

        mockMvc.perform(put("/api/recipes/recipe/{id}", RECIPE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zopf"));
    }

    @Test
    void updateRecipe_unknownId_returnsNotFound() throws Exception {
        // Grenzfall: Service meldet "nicht gefunden" mit null -> Controller muss 404 liefern
        when(service.updateRecipe(eq(RECIPE_ID), any(Recipe.class))).thenReturn(null);

        mockMvc.perform(put("/api/recipes/recipe/{id}", RECIPE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe())))
                .andExpect(status().isNotFound());
    }
}
