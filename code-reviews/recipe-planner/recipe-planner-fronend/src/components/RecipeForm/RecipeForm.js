import React from 'react';
import './RecipeForm.css';
import {useForm, useFieldArray} from "react-hook-form";
import {Form, Button, Col, Row, Alert} from 'react-bootstrap';
import AddIngredient from "../AddIngredient/AddIngredient";

// Gemeinsames Formular fuer "Add Recipe" (Aufgabe 1) und "Edit Recipe" (Aufgabe 2).
// Review-Erkenntnis: statt das Formular fuer Edit zu kopieren, wird es als
// wiederverwendbare Komponente extrahiert (DRY).
// Die einzelne Zutaten-Zeile ist in AddIngredient ausgelagert (Review-Finding Aufgabe 1: SRP).
const EMPTY_RECIPE = {name: '', description: '', imageUrl: '', ingredients: []};

const EMPTY_INGREDIENT = {name: '', comment: '', unit: 'PIECE', amount: 1};

const RecipeForm = ({title, submitLabel, initialRecipe, errorMessage, onSave}) => {

    const {
        register,
        control,
        handleSubmit,
        formState: {errors},
    } = useForm({values: initialRecipe ?? EMPTY_RECIPE});

    const {fields, append, remove} = useFieldArray({control, name: "ingredients"});

    // amount kommt dank valueAsNumber (siehe AddIngredient) bereits als Zahl an
    return (
        <div className="bg">
            <Form className="m-3" onSubmit={handleSubmit(onSave)}>
                <h1 className="h3 bg-dark text-bg-primary mt-2">{title}</h1>
                {/* Review-Finding Aufgabe 2: Fehler im UI statt als blockierendes alert() */}
                {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
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
                    <AddIngredient key={field.id} index={index} register={register}
                                   errors={errors} onRemove={() => remove(index)}/>
                ))}
                <Row>
                    <Col>
                        <Button variant='warning' className="mt-1 mb-3"
                                onClick={() => append(EMPTY_INGREDIENT)}>
                            Add Ingredient
                        </Button>
                    </Col>
                </Row>
                <Button variant="primary" type="submit" className="mb-5">{submitLabel}</Button>
            </Form>
        </div>
    );
};

export default RecipeForm;
