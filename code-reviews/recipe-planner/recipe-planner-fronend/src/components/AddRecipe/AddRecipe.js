import React from 'react';
import './AddRecipe.css';
import {useForm, useFieldArray} from "react-hook-form";
import {Form, Button, Col, Row} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";
import {addRecipe} from "../../apis/recipeApi";

// Aufgabe 1: Feature "neues Recipe hinzufuegen".
// Variante A (uebernommen): react-hook-form mit useFieldArray fuer die Zutaten-Zeilen.
// Die Feldnamen entsprechen 1:1 dem Backend-Domain-Objekt (name, comment, unit, amount).
const UNITS = ['PIECE', 'GRAMM', 'KILOGRAMM', 'LITRE', 'DECILITRE'];

const EMPTY_INGREDIENT = {name: '', comment: '', unit: 'PIECE', amount: 1};

function AddRecipe() {
    const navigate = useNavigate();

    const {
        register,
        control,
        handleSubmit,
        formState: {errors},
    } = useForm({
        defaultValues: {name: '', description: '', imageUrl: '', ingredients: []},
    });

    const {fields, append, remove} = useFieldArray({control, name: "ingredients"});

    const submit = (formRecipe) => {
        // amount kommt aus dem Input als String -> ins Backend-Format (int) wandeln
        const recipe = {
            ...formRecipe,
            ingredients: formRecipe.ingredients.map(ingredient => ({
                ...ingredient,
                amount: parseInt(ingredient.amount, 10),
            })),
        };
        addRecipe(recipe)
            .then(() => navigate("/"))
            .catch((error) => alert("Could not save recipe: " + error.message));
    };

    return (
        <div className="bg">
            <Form className="m-3" onSubmit={handleSubmit(submit)}>
                <h1 className="h3 bg-dark text-bg-primary mt-2">Add Recipe</h1>
                <Form.Group className="mb-1" controlId="recipeName">
                    <Form.Label>Recipe Name:</Form.Label>
                    <Form.Control placeholder="Name" isInvalid={!!errors.name}
                                  {...register("name", {required: true})}/>
                    <Form.Control.Feedback type="invalid">Name is required</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className="mb-1" controlId="recipeDescription">
                    <Form.Label>Description:</Form.Label>
                    <Form.Control placeholder="Description" {...register("description")}/>
                </Form.Group>
                <Form.Group className="mb-1 mb-5" controlId="recipeImageUrl">
                    <Form.Label>Image URL:</Form.Label>
                    <Form.Control placeholder="URL" {...register("imageUrl")}/>
                </Form.Group>
                <Row>
                    <Col>Ingredient</Col>
                    <Col>Unit</Col>
                    <Col>Quantity</Col>
                    <Col xs={1}></Col>
                </Row>
                <hr/>
                {fields.map((field, index) => (
                    <Row key={field.id}>
                        <Col>
                            <Form.Group className="mb-1">
                                <Form.Control placeholder="Name"
                                              {...register(`ingredients.${index}.name`, {required: true})}/>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="mb-1">
                                <Form.Select {...register(`ingredients.${index}.unit`)}>
                                    {UNITS.map(unit => <option key={unit}>{unit}</option>)}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="mb-1">
                                <Form.Control type="number" min="1" placeholder="Quantity"
                                              {...register(`ingredients.${index}.amount`, {required: true})}/>
                            </Form.Group>
                        </Col>
                        <Col xs={1}>
                            <Button onClick={() => remove(index)} variant='outline-dark'
                                    className="mb-1">x</Button>
                        </Col>
                    </Row>
                ))}
                <Row>
                    <Col>
                        <Button variant='warning' className="mt-1 mb-3"
                                onClick={() => append(EMPTY_INGREDIENT)}>
                            Add Ingredient
                        </Button>
                    </Col>
                </Row>
                <Button variant="primary" type="submit" className="mb-5">Submit</Button>
            </Form>
        </div>
    );
}

export default AddRecipe;
