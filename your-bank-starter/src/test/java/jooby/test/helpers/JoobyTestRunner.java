package jooby.test.helpers;

import org.jooby.Jooby;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.*;

import java.lang.reflect.Field;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.reflect.FieldUtils.getFieldsWithAnnotation;



public class JoobyTestRunner implements TestInstancePostProcessor,
        BeforeAllCallback,
        AfterAllCallback {

    private static final String JOOBY = "JOOBY";
    private static final String JOOBY_ARGS = "JOOBY_ARGS";

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        Field[] candidates = getFieldsWithAnnotation(testInstance.getClass(), JoobyApp.class);
        requireNotEmpty(candidates);
        requireSingleField(candidates);

        Field appField = candidates[0];
        appField.setAccessible(true);

        Jooby app = getFieldValue(appField, testInstance, Jooby.class);
        requireNonNull(app);

        Store store = getStore(context);
        store.put(JOOBY, app);
        store.put(JOOBY_ARGS, appField.getAnnotation(JoobyApp.class).value());
    }

    private void requireNotEmpty(Field[] fields) {
        if (fields == null || fields.length == 0)
            throw new RuntimeException("No fields of type JoobyApp.class are found");
    }

    private void requireSingleField(Field[] fields) {
        if (fields.length > 1)
            throw new RuntimeException("Multiple fields of type JoobyApp.class are found");
    }

    private <T> T getFieldValue(Field field, Object target, Class<T> targetType) {
        try {
            return targetType.cast(field.get(target));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass()));
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        Store store = getStore(context);
        Jooby app = store.get(JOOBY, Jooby.class);
        String[] args = store.get(JOOBY_ARGS, String[].class);

        System.out.println(
                "Server will be started with the following args: " + Arrays.toString(args)
        );
        app.start(args);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        getStore(context)
                .get(JOOBY, Jooby.class)
                .stop();
    }
}
