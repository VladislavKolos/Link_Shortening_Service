package com.example.emobile.linkshorteningservice.service.validator.constraint;

import com.example.emobile.linkshorteningservice.repository.LinkRepository;
import com.example.emobile.linkshorteningservice.service.validator.constraint.annotation.UniqueAlias;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniqueAliasValidator implements ConstraintValidator<UniqueAlias, String> {
    private final LinkRepository linkRepository;

    @Override
    public boolean isValid(String alias, ConstraintValidatorContext constraintValidatorContext) {
        return !linkRepository.existsByAlias(alias);
    }
}