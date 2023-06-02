# GradleJarInJar
A Gradle plugin for including jars inside jars
## Installation
The plugin can be found at the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.matyrobbrt.jarinjar) under `com.matyrobbrt.jarinjar`.  
In order to install it, add this to your buildscript:
```gradle
plugins {
    // Other plugins here
    id 'com.matyrobbrt.jarinjar' version '1.1.+'
}
```

## Usage
Example usage for Forge:
```gradle
tasks.register('createSomeJarJar', com.matyrobbrt.gradle.jarinjar.task.ForgeJarInJarTask).configure {
    archiveBaseName.set('somejarjar') // Set the jar file name to `somejarjar-$version.jar`. You can instead change the archiveClassifier if, for example, you want to have an -all classifier
    fromConfiguration(configurations.shade) // Pull dependencies from the `shade` configuration
    fromJar(tasks.otherJar) { // Include the jar created by the `otherJar` task. The artifact ID will be the jar archiveBaseName, the group will be the project group and the version the archiveVersion
        versionRange('[1,)') // and allow any version greater than 1.0.0 when deciding which version shall be picked
    }
}
```

In order to configure a dependency you can use `jij.onDependency` when declaring it:
```gradle
dependencies {
    shade('com.example:examplemod:1.0.0') {
        jij.onDependency(it) {
            versionRange = nextMajor // The same as declaring `versionRange '[1.0.0,2.0.0]
            filterContents { // You can use this to filter the contents of the jar that will be JiJ'd
                exclude 'module-info.class' // Forge will not read a mod's module-info, so we can exclude it
            }
            path = 'myexampledepdency.jar' // The name of the jar inside the META-INF/jars folder. Usually you do not need to change this as it will be computed from the dependency artifact ID, but you can change it in case of conflicts
            // You can also customize the group, artifactId and version using the properties with the same names
        }
    }
}
```

It is also possible to configure all dependencies matching a regex pattern, like so:
```gradle
jij.onConfiguration('shade') { // the name of the configuration whose dependencies you want to configure
    eachMatching('com\\..+') { // Match all artifacts whose groups start with `com.`
        // Configure the depedency. Refer to the above example on the properties available here
    }
}
```