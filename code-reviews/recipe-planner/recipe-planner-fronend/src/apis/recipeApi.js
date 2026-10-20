import axios from "axios";

// Zentrale API-Zugriffe: baseURL nur an einer Stelle.
// Review-Finding Aufgabe 1: die URL war in Browse.js hartcodiert und waere mit
// dem Add-Feature ein zweites Mal dupliziert worden (DRY).
const baseURL = "http://localhost:8080/api/recipes";

export const getRecipes = () => axios.get(baseURL);

export const addRecipe = (recipe) => axios.post(baseURL, recipe);
