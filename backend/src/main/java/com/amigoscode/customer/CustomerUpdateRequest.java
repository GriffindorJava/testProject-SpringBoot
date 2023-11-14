package com.amigoscode.customer;

public record CustomerUpdateRequest(
        Integer age,
        String name,
        String email
) {
}
