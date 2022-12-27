package org.example.utils;

import org.example.annotation.Bean;
import org.example.annotation.Ignore;
import org.example.annotation.Property;
import org.example.exception.NSQAJsonException;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.example.utils.StringUtils.pascalCaseToCamelCase;
import static org.example.utils.StringUtils.presentOrNull;

/**
 * @author anhnsq@viettel.com.vn
 */
@Bean
public class NSQAJson {
  public String toJson(Object object) {
    StringBuilder stringBuilder = new StringBuilder();
    Class<?> clazz = object.getClass();

    stringBuilder
      .append("{\"")
      .append(pascalCaseToCamelCase(clazz.getSimpleName()))
      .append("\":{");

    try {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);

        if (isIgnored(field)) continue;

        Property propertyAnnotation = field.getAnnotation(Property.class);
        String defaultValue = propertyAnnotation != null ? propertyAnnotation.defaultValue() : null;
        if (!notValidString(defaultValue)) {
          field.set(object, defaultValue);
        }

        if (notValidString(defaultValue) && isRequired(propertyAnnotation) && field.get(object) == null) {
          throw new NSQAJsonException(String.format("[%s] '%s' is required, found null", clazz.getCanonicalName(), field.getName()));
        }

        Class<?> fieldType = field.getType();
        boolean isObject = !fieldType.isPrimitive() && !(fieldType == String.class);
        if (!isObject) {
          stringBuilder
            .append("\"")
            .append(Optional.ofNullable(propertyAnnotation).map(property -> presentOrNull(property.value())).orElse(field.getName()))
            .append("\":")
            .append(wrapByDoubleQuotes(fieldType))
            .append(field.get(object))
            .append(wrapByDoubleQuotes(fieldType));
        } else {
          Object objectValue = field.get(object);
          String fieldJsonKey = Optional.ofNullable(propertyAnnotation).map(property -> presentOrNull(property.value())).orElse(field.getName());
          if (objectValue == null) {
            stringBuilder.append(String.format("\"%s\": null", fieldJsonKey));
          } else {
            String subObjectJson = toJson(objectValue);
            // remove '{' and '}'
            String fieldJsonValue = subObjectJson.substring(1, subObjectJson.length() - 1);
            String[] keyValue = fieldJsonValue.split(":");
            keyValue[0] = "\"" + fieldJsonKey + "\"";

            stringBuilder.append(String.join(":", keyValue));
          }
        }

        stringBuilder.append(",");
      }
      stringBuilder.setLength(Math.max(stringBuilder.length() - 1, 0));
      stringBuilder.append("}}");
    } catch (IllegalAccessException e) {
      throw new NSQAJsonException("Illegal Access field value", e);
    }

    return stringBuilder.toString();
  }

  private boolean isIgnored(Field field) {
    return field.getAnnotation(Ignore.class) != null;
  }

  private boolean isRequired(Property propertyAnnotation) {
    return propertyAnnotation != null && propertyAnnotation.required();
  }

  private String wrapByDoubleQuotes(Class<?> fieldType) {
    return fieldType == String.class ? "\"" : "";
  }

  private boolean notValidString(String value) {
    return value == null || value.isBlank();
  }
}
