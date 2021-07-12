package com.netcetera.girders.cache;

import org.junit.jupiter.api.Test;
import org.springframework.cache.interceptor.SimpleKey;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Class method parameter key generator testCacheSubject.
 */
@SuppressWarnings("rawtypes")
class ClassMethodParameterKeyGeneratorTest {

  private final ClassMethodParameterKeyGenerator keyGenerator = new ClassMethodParameterKeyGenerator();

  private final TestCacheSubject testCacheSubject = new TestCacheSubject();

  /**
   * Should test key generator for methods with zero parameters.
   */
  @Test
  void shouldTestKeyGeneratorForMethodsWithNoParameters() throws NoSuchMethodException {
    // given
    Method method = TestCacheSubject.class.getDeclaredMethod("methodZero");

    // when
    Object key = keyGenerator.generate(testCacheSubject, method);

    // then
    String firstKey = (String) ((List) key).get(0);
    String secondKey = (String) ((List) key).get(1);
    SimpleKey thirdKey = (SimpleKey) ((List) key).get(2);
    assertThat(firstKey, containsString(TestCacheSubject.class.getName()));
    assertThat(secondKey, is("methodZero"));
    assertThat(thirdKey, is(SimpleKey.EMPTY));
  }

  /**
   * Should test key generator for methods with one parameter.
   */
  @Test
  void shouldTestKeyGeneratorForMethodsWithOneParameter() throws NoSuchMethodException {
    // given
    Method method = TestCacheSubject.class.getDeclaredMethod("methodOne", String.class);

    // when
    Object key = keyGenerator.generate(testCacheSubject, method, "123");

    // then
    String firstKey = (String) ((List) key).get(0);
    String secondKey = (String) ((List) key).get(1);
    String thirdKey = (String) ((List) key).get(2);
    assertThat(firstKey, containsString(TestCacheSubject.class.getName()));
    assertThat(secondKey, is("methodOne"));
    assertThat(thirdKey, is("123"));
  }

  /**
   * Should test key generator for methods with multiple parameters.
   */
  @Test
  void shouldTestKeyGeneratorForMethodsWithMultipleParameters() throws NoSuchMethodException {
    // given
    Method method = TestCacheSubject.class.getDeclaredMethod("methodTwo", String.class, String.class);

    // when
    Object key = keyGenerator.generate(testCacheSubject, method, "string 1", "string 2");

    // then
    String firstKey = (String) ((List) key).get(0);
    String secondKey = (String) ((List) key).get(1);
    SimpleKey thirdKey = (SimpleKey) ((List) key).get(2);
    assertThat(firstKey, containsString(TestCacheSubject.class.getName()));
    assertThat(secondKey, equalTo("methodTwo"));
    assertThat(thirdKey, is(new SimpleKey("string 1", "string 2")));
  }

  @SuppressWarnings({"UnusedReturnValue", "MethodMayBeStatic"})
  private static class TestCacheSubject {

    private String methodZero() {
      return "foo";
    }

    private String methodOne(String parameter) {
      return parameter;
    }

    private String methodTwo(String parameter1, String parameter2) {
      return parameter1 + parameter2;
    }
  }
}
