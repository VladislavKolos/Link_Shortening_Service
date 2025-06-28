package com.example.emobile.linkshorteningservice.service.validator.constraint.annotation;

import com.example.emobile.linkshorteningservice.service.validator.constraint.UniqueAliasValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueAliasValidator.class)
public @interface UniqueAlias {
    String message() default "Alias is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}