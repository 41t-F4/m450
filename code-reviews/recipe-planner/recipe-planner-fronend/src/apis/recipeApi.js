import axios from "axios";

// Zentrale API-Zugriffe: baseURL nur an einer Stelle (Review-Finding aus Aufgabe 1:
// die URL war vorher in Browse.js hartcodiert und waere in jeder Komponente dupliziert worden)
const baseURL = "http://localhost:8080/api/recipes";

export const getRecipes = () => axios.get(baseURL);

export const getRecipe = (recipeId) => axios.get(`${baseURL}/recipe/${recipeId}`);

export const addRecipe = (recipe) => axios.post(baseURL, recipe);

export const updateRecipe = (recipeId, recipe) => axios.put(`${baseURL}/recipe/${recipeId}`, recipe);
