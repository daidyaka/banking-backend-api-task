package com.trufanov.exception;

public class EntityNotFoundException extends RuntimeException {

    private static final String ENTITY_NOT_FOUND_MSG = "Entity with id %s not found";

    public EntityNotFoundException(Long id) {
        super(ENTITY_NOT_FOUND_MSG.formatted(id.toString()));
    }
}
