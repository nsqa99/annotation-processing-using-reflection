package org.example.core;

import org.example.annotation.Bean;
import org.example.annotation.Inject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anhnsq@viettel.com.vn
 */
public class BeanContainer {

  public Map<Class<?>, Object> processBeans(Class<?> source) {
    Objects.requireNonNull(source);
    Set<Class<?>> projectClasses = findAllClassesUsingClassLoader(source.getPackageName());
    Map<Class<?>, Object> mapBeanToInstances = initializeBeans(projectClasses);
    injectBeans(projectClasses, mapBeanToInstances);

    return mapBeanToInstances;
  }
  public Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
    Set<String> packageFiles = findAllClassesInsidePackage(packageName);
    return packageFiles.stream()
      .map(this::getClass)
      .collect(Collectors.toSet());
  }
  public Map<Class<?>, Object> initializeBeans(Set<Class<?>> classes) {
    Map<Class<?>, Object> mapClassInstances = new HashMap<>();
    classes.stream().filter(this::isBean).forEach(clazz -> {
      try {
        Constructor<?> constructor = clazz.getConstructor();
        mapClassInstances.put(clazz, constructor.newInstance());
      } catch (NoSuchMethodException e) {
        System.out.println("Beans need empty constructors " + clazz.getName());
        e.printStackTrace();
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
        System.out.println("Error when invoke constructor of class " + clazz.getName());
        e.printStackTrace();
      }
    });

    return mapClassInstances;
  }
  public void injectBeans(Set<Class<?>> classes, Map<Class<?>, Object> initializedBeans) {
    classes.forEach(clazz -> {
      Field[] classFields = clazz.getDeclaredFields();
      for (Field field : classFields) {
        field.setAccessible(true);

        boolean needInject = Objects.nonNull(field.getAnnotation(Inject.class));
        if (needInject) {
          Class<?> fieldType = field.getType();
          if (!initializedBeans.containsKey(fieldType)) {
            System.out.println("Injected value must be declared as Bean");
          } else {
            try {
              field.set(initializedBeans.get(clazz), initializedBeans.get(fieldType));
            } catch (IllegalAccessException e) {
              System.out.println("Illegal access exception with class " + clazz.getName());
              e.printStackTrace();
            }
          }
        }
      }
    });
  }
  private boolean isBean(Class<?> clazz) {
    return Objects.nonNull(clazz.getAnnotation(Bean.class));
  }
  private Set<String> findAllClassesInsidePackage(String packageName) {
    InputStream stream = ClassLoader.getSystemClassLoader()
      .getResourceAsStream(packageName.replaceAll("[.]", "/"));
    if (stream == null) {
      return Collections.emptySet();
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    Map<Boolean, List<String>> folderFilesSplitMap = reader.lines().collect(Collectors.partitioningBy(line -> line.endsWith(".class")));

    Set<String> files = folderFilesSplitMap.get(true).stream().map(fileName -> packageName + "." + fileName.substring(0, fileName.lastIndexOf(".")))
        .collect(Collectors.toSet());
    Set<String> folders = new HashSet<>(folderFilesSplitMap.get(false));
    Set<String> subFolderClasses = new HashSet<>();
    for (String folder : folders) {
      String subPackage = packageName + "." + folder;

      subFolderClasses.addAll(findAllClassesInsidePackage(subPackage));
    }

    files.addAll(subFolderClasses);

    return files;
  }

  private Class<?> getClass(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      System.out.println("Class not found: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
}
