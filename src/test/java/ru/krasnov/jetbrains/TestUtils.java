package ru.krasnov.jetbrains;

import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TestUtils {

    /**
     * Method checks that all dto fields are not {@literal null}
     *
     * @param dto        - dto to check
     * @param nullFields - dto field names, that must be null
     */
    public static void checkThatAllFieldsAreFilled(Object dto, String... nullFields)
            throws IllegalAccessException {
        checkFields(dto, Arrays.asList(nullFields));
    }

    private static void checkFields(Object dto, List<String> fieldsToExclude)
            throws IllegalAccessException {
        List<Field> dtoFieldList = Arrays.asList(dto.getClass().getDeclaredFields());

        List<Field> notNullFields = dtoFieldList.stream()
                .filter(field -> !fieldsToExclude.contains(field.getName()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());
        for (Field field : notNullFields) {
            Assertions.assertNotNull(field.get(dto), String.format("Field=%s is empty in %s", field.getName(), dto));
        }
    }

}
