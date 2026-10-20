import React, {useEffect, useState} from 'react';
import './EditRecipe.css';
import {useNavigate, useParams} from "react-router-dom";
import {Alert} from "react-bootstrap";
import RecipeForm from "../RecipeForm/RecipeForm";
import {getRecipe, updateRecipe} from "../../apis/recipeApi";

// Aufgabe 2: Feature "existierendes Recipe editieren"
// Laedt das Recipe ueber die Id aus der URL, zeigt das gleiche Formular wie
// AddRecipe (vorbefuellt) und speichert per PUT auf das Backend.
function EditRecipe() {
    const {recipeId} = useParams();
    const navigate = useNavigate();
    const [recipe, setRecipe] = useState(null);
    // Review-Finding: Fehler als State im UI anzeigen statt mit alert() den Flow zu unterbrechen
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        getRecipe(recipeId)
            .then((response) => setRecipe(response.data))
            .catch((error) => setErrorMessage("Could not load recipe: " + error.message));
    }, [recipeId]);

    const save = (updated) => {
        setErrorMessage('');
        updateRecipe(recipeId, updated)
            .then(() => navigate("/"))
            .catch((error) => setErrorMessage("Could not update recipe: " + error.message));
    };

    if (!recipe) {
        return errorMessage ? <Alert variant="danger" className="m-3">{errorMessage}</Alert> : null;
    }

    return <RecipeForm title="Edit Recipe" submitLabel="Save" initialRecipe={recipe}
                       errorMessage={errorMessage} onSave={save}/>;
}

export default EditRecipe;
