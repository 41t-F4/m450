import React, {useEffect, useState} from 'react';
import './Planer.css';
import {Alert, Button, Container, Form, Table} from "react-bootstrap";
import {createShoppingList, getRecipes} from "../../apis/recipeApi";

// TDD-Feature Einkaufsliste (tdd/projekt-tdd-einkaufsliste.md):
// Rezepte auswaehlen -> POST /api/shopping-list -> zusammengefasste Zutaten anzeigen.
// Die Logik (Summieren, Einheiten getrennt) liegt im Backend und ist dort per TDD getestet.
function Planer() {
    const [recipes, setRecipes] = useState([]);
    const [selectedIds, setSelectedIds] = useState([]);
    const [shoppingList, setShoppingList] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        getRecipes()
            .then((response) => setRecipes(response.data))
            .catch((error) => setErrorMessage("Could not load recipes: " + error.message));
    }, []);

    const toggle = (id) => {
        setSelectedIds((ids) => ids.includes(id) ? ids.filter((i) => i !== id) : [...ids, id]);
    };

    const create = () => {
        setErrorMessage('');
        createShoppingList(selectedIds)
            .then((response) => setShoppingList(response.data))
            .catch((error) => setErrorMessage("Could not create shopping list: " + error.message));
    };

    return (
        <Container className="Planer">
            <h2>Planer</h2>
            {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
            <Form>
                {recipes.map((recipe) => (
                    <Form.Check key={recipe.id} id={`plan-${recipe.id}`} type="checkbox" label={recipe.name}
                                checked={selectedIds.includes(recipe.id)} onChange={() => toggle(recipe.id)}/>
                ))}
            </Form>
            <Button className="mt-3" disabled={selectedIds.length === 0} onClick={create}>
                Einkaufsliste erstellen
            </Button>

            {shoppingList && (
                <Table striped bordered variant="dark" className="mt-4">
                    <thead>
                    <tr><th>Zutat</th><th>Menge</th><th>Einheit</th></tr>
                    </thead>
                    <tbody>
                    {shoppingList.map((line) => (
                        <tr key={line.name + "|" + line.unit}>
                            <td>{line.name}</td><td>{line.amount}</td><td>{line.unit}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            )}
        </Container>
    );
}

export default Planer;
