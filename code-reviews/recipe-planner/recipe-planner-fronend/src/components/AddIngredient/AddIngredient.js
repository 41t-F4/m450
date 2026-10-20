import React from 'react';
import './AddIngredient.css';
import {Button, Col, Form, Row} from "react-bootstrap";

// Eine Zutaten-Zeile des Add-Recipe-Formulars.
// Review-Finding (SRP): die Zeilendarstellung bleibt eine eigene Komponente,
// AddRecipe kuemmert sich nur noch um das Formular als Ganzes.
// Die Felder werden ueber `register` von react-hook-form angebunden (useFieldArray).
const UNITS = ['PIECE', 'GRAMM', 'KILOGRAMM', 'LITRE', 'DECILITRE'];

const AddIngredient = ({index, register, errors, onRemove}) => {
    const amountError = errors?.ingredients?.[index]?.amount;

    return (
        <Row>
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
                    {/* Review-Finding: parseInt lieferte bei leerem Feld NaN. Jetzt liest
                        react-hook-form den Wert direkt als Zahl und die Validierung
                        laesst nur ganze Zahlen >= 1 durch -> kein NaN kann abgeschickt werden. */}
                    <Form.Control type="number" min="1" step="1" placeholder="Quantity"
                                  isInvalid={!!amountError}
                                  {...register(`ingredients.${index}.amount`, {
                                      valueAsNumber: true,
                                      validate: (value) => (Number.isInteger(value) && value >= 1)
                                          || "Quantity must be a whole number of at least 1",
                                  })}/>
                    <Form.Control.Feedback type="invalid">{amountError?.message}</Form.Control.Feedback>
                </Form.Group>
            </Col>
            <Col xs={1}>
                <Button onClick={onRemove} variant='outline-dark' className="mb-1">x</Button>
            </Col>
        </Row>
    );
};

export default AddIngredient;
