package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import ch.tbz.recipe.planner.service.ShoppingListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD Zyklus 7: Web-Slice fuer die Einkaufsliste (gleicher Aufbau wie RecipeControllerTest).
 * Der Service ist gemockt - hier geht es nur um Routing, JSON rein (Id-Liste) und JSON raus (Zeilen).
 */
@WebMvcTest(ShoppingListController.class)
class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingListService service;

    // wie in RecipeControllerTest: die Application-Klasse zieht Mapper + Repository in den Web-Slice
    @MockBean
    private RecipeEntityMapper mapper;

    @MockBean
    private RecipeRepository repository;

    @Test
    void createShoppingList_postsRecipeIds_returnsOkAndLines() throws Exception {
        UUID pastaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID salatId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(service.createShoppingListForRecipeIds(List.of(pastaId, salatId)))
                .thenReturn(List.of(new Ingredient(null, "Tomate", null, Unit.PIECE, 8)));

        mockMvc.perform(post("/api/shopping-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"" + pastaId + "\", \"" + salatId + "\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Tomate"))
                .andExpect(jsonPath("$[0].unit").value("PIECE"))
                .andExpect(jsonPath("$[0].amount").value(8));
    }
}
