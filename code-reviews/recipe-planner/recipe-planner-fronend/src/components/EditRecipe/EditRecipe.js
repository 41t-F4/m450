import React, {useEffect, useState} from 'react';
import './EditRecipe.css';
import {useNavigate, useParams} from "react-router-dom";
import RecipeForm from "../RecipeForm/RecipeForm";
import {getRecipe, updateRecipe} from "../../apis/recipeApi";

// Aufgabe 2: Feature "existierendes Recipe editieren"
// Laedt das Recipe ueber die Id aus der URL, zeigt das gleiche Formular wie
// AddRecipe (vorbefuellt) und speichert per PUT auf das Backend.
function EditRecipe() {
    const {recipeId} = useParams();
    const navigate = useNavigate();
    const [recipe, setRecipe] = useState(null);

    useEffect(() => {
        getRecipe(recipeId)
            .then((response) => setRecipe(response.data))
            .catch((error) => alert("Could not load recipe: " + error.message));
    }, [recipeId]);

    const save = (updated) => {
        updateRecipe(recipeId, updated)
            .then(() => navigate("/"))
            .catch((error) => alert("Could not update recipe: " + error.message));
    };

    if (!recipe) return null;

    return <RecipeForm title="Edit Recipe" submitLabel="Save" initialRecipe={recipe} onSave={save}/>;
}

export default EditRecipe;
