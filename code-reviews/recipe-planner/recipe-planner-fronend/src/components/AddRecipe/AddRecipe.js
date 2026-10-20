import React from 'react';
import './AddRecipe.css';
import {useNavigate} from "react-router-dom";
import RecipeForm from "../RecipeForm/RecipeForm";
import {addRecipe} from "../../apis/recipeApi";

// Aufgabe 1: Feature "neues Recipe hinzufuegen"
// Die eigentliche Formular-Logik steckt in RecipeForm (wird von Edit wiederverwendet).
function AddRecipe() {
    const navigate = useNavigate();

    const save = (recipe) => {
        addRecipe(recipe)
            .then(() => navigate("/"))
            .catch((error) => alert("Could not save recipe: " + error.message));
    };

    return <RecipeForm title="Add Recipe" submitLabel="Submit" onSave={save}/>;
}

export default AddRecipe;
