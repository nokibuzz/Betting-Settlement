package com.sportygroup.domain.exception;

public class ResourceNotFoundException extends BettingException {
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s not found with ID: %s", resource, id));
    }}
