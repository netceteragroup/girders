package com.netcetera.girders.logging;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Fixes the Spring loggers. See
 * {@link SpringLoggerFix#redefineSuperclassLogger(Object, Logger, Class)} for details.
 */
public final class SpringLoggerFix {

  private SpringLoggerFix() {
    // Utility
  }

  /**
   * Why this ugly hack? Well, many Spring super classes like GenericFilterBean initialize a logger
   * using
   * <pre>
   * protected final Log logger = LogFactory.getLog(this.getClass())
   * </pre>
   * <p>
   * Since the {@code logger} instance is protected it's inherited by all subclasses and since it is
   * created with {@code this.getClass()} we no longer have the ability to set the log level
   * differently for this implementation and the Spring super classes (they have the same scope, the
   * application package scope).
   * <p>
   * Furthermore, if you declare a "local" logger in your class like so
   * <pre>
   * private final Logger logger = LoggerFactory.getLogger(A.class);
   * </pre>
   * <p>
   * you get a compiler warning because your {@code logger} field is hiding a field in the Spring
   * super class with the same name.
   * </p>
   *
   * @param ourSubclass              Girders subclass instance
   * @param ourSubclassesLocalLogger local logger instance used in the Girders subclass
   * @param springSuperclass         Spring superclass
   */
  public static void redefineSuperclassLogger(Object ourSubclass, Logger ourSubclassesLocalLogger,
      Class<?> springSuperclass) {
    try {
      Field loggerField = ReflectionUtils.findField(springSuperclass, "logger");
      if (loggerField != null) {
        loggerField.setAccessible(true);
        loggerField.set(ourSubclass, LogFactory.getLog(springSuperclass));
      }
    } catch (IllegalAccessException e) {
      ourSubclassesLocalLogger.info("Couldn't reset logger field.", e);
    } catch (SecurityException e) {
      ourSubclassesLocalLogger.info("Couldn't set logger field accessible.", e);
    }
  }

}
