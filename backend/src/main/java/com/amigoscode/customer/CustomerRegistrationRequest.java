package com.amigoscode.customer;

public record CustomerRegistrationRequest(
        Integer age,
        String name,
        String email
) {
}
