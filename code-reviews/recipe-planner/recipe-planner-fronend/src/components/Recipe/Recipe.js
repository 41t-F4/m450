import './Recipe.css'
import {Button, Card} from "react-bootstrap";
import React from "react";
import PropTypes from "prop-types";
import {useNavigate} from "react-router-dom";

function Recipe(props) {
    const navigate = useNavigate();
    return (
        <Card style={{width: '18rem'}}>
            <Card.Img variant="top" src={props.image}/>
            <Card.Body>
                <Card.Title>{props.title}</Card.Title>
                {props.description}
                <Button variant="primary" onClick={() => navigate(`/edit/${props.id}`)}>Edit Details</Button>
            </Card.Body>
        </Card>
    )
}

// Review-Finding Aufgabe 2: ohne id navigiert der Edit-Button auf /edit/undefined.
// isRequired macht den Vertrag explizit und warnt im Dev-Modus, falls Browse die id vergisst.
Recipe.propTypes = {
    id: PropTypes.string.isRequired,
    title: PropTypes.string,
    description: PropTypes.string,
    image: PropTypes.string,
};

export default Recipe;
