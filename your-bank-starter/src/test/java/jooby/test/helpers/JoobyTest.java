package jooby.test.helpers;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ExtendWith(JoobyTestRunner.class)
// allows @BeforeAll/@AfterAll methods to be non-static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("it")
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoobyTest {}
