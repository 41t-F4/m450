import React, {useState} from 'react';
import './AddRecipe.css';
import {useNavigate} from "react-router-dom";
import RecipeForm from "../RecipeForm/RecipeForm";
import {addRecipe} from "../../apis/recipeApi";

// Aufgabe 1: Feature "neues Recipe hinzufuegen"
// Die eigentliche Formular-Logik steckt in RecipeForm (wird von Edit wiederverwendet).
function AddRecipe() {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');

    const save = (recipe) => {
        setErrorMessage('');
        addRecipe(recipe)
            .then(() => navigate("/"))
            .catch((error) => setErrorMessage("Could not save recipe: " + error.message));
    };

    return <RecipeForm title="Add Recipe" submitLabel="Submit" errorMessage={errorMessage} onSave={save}/>;
}

export default AddRecipe;
