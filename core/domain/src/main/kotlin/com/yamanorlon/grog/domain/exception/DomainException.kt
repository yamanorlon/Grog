package com.yamanorlon.grog.domain.exception

open class DomainException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class NotFoundException(message: String) : DomainException(message)

class ValidationException(val errors: List<String>) : DomainException(
    errors.joinToString("; "),
)

class ConflictException(message: String) : DomainException(message)

class ReferenceNotFoundException(message: String) : DomainException(message)
