package cn.zyszero.phoenix.rpc.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @Author: zyszero
 * @Date: 2024/4/2 19:15
 */
public class ScanPackageUtils {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static List<Class<?>> scanPackages(String[] packages, Predicate<Class<?>> predicate) {
        List<Class<?>> results = new ArrayList<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        for (String basePackage : packages) {
            if (StringUtils.isBlank(basePackage)) {
                continue;
            }
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage))
                    + "/" + DEFAULT_RESOURCE_PATTERN;
            System.out.println("packageSearchPath=" + packageSearchPath);
            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
//                    System.out.println(" resource: " + resource.getFilename());
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    String className = classMetadata.getClassName();
                    Class<?> clazz = Class.forName(className);
                    if (predicate.test(clazz)) {
//                        System.out.println(" ===> class: " + className);
                        results.add(clazz);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static void main(String[] args) {
        String packages = "cn.zyszero.phoenix.rpc";
        System.out.println(" 1. *********** ");
        System.out.println(" => scan all classes for packages: " + packages);
        List<Class<?>> classes = scanPackages(packages.split(","), clazz -> true);
        classes.forEach(System.out::println);

        System.out.println();
        System.out.println(" 2. *********** ");
        System.out.println(" => scan all classes with @Configuration for packages: " + packages);
        List<Class<?>> classesWithConfig = scanPackages(packages.split(","),
                clazz -> Arrays.stream(clazz.getAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().equals(Configuration.class)));
        classesWithConfig.forEach(System.out::println);
    }

}
