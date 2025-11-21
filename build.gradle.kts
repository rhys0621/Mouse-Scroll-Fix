plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.google.protobuf") version "0.9.4"
}

val minecraftVersion = "1.21.10"
val yarnMappings = "1.21.10+build.2"
val loaderVersion = "0.17.3"
val fabricVersion = "0.135.0+1.21.10"

group = "me.rhys"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")

        content {
            includeGroup("com.github.Oryxel")
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
}

base {
    archivesName.set("ScrollFix")
}

tasks {
    processResources {
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }

        exclude("**/*.proto")
        exclude("lombok.config")
    }

    withType(JavaCompile::class.java) {
        options.release.set(21)
    }

    jar {
        from("LICENSE") {
            rename { "${it}_ScrollFix" }
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val mappingFiles = provider {
            rootProject.configurations.mappings.get().map(::zipTree)
        }

        inputs.files(mappingFiles).withPropertyName("mappingFiles")

//        from(files(mappingFiles.get())) {
//            include("mappings/mappings.tiny")
//        }
    }

    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    wrapper {
        version = "8.14.3"
    }

    named<Jar>("sourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    shadowJar {
        configurations = listOf(project.configurations.shadow.get())
        exclude("META-INF")
        archiveClassifier.set("fat")

        val mappingFiles = provider {
            rootProject.configurations.mappings.get().map(::zipTree)
        }

        inputs.files(mappingFiles).withPropertyName("mappingFiles")

        from(files(mappingFiles.get())) {
            include("mappings/mappings.tiny")
        }
    }

    build {
        dependsOn(generateProto)
    }

    remapJar {
//        dependsOn(shadowJar)
//        inputFile.set(shadowJar.get().archiveFile)

        archiveClassifier.set("")
    }

    register<Jar>("remapShadowJar") {
        dependsOn(shadowJar)
        mustRunAfter(shadowJar)

        from(zipTree(shadowJar.get().archiveFile))
        archiveClassifier.set("fat")

        // Apply Fabric remapping
        doLast {
            // This would need custom remapping logic if you want a fat jar with dependencies
        }
    }
}

sourceSets {
    main {
        java.srcDirs(
            "src/main/java",
        )
    }
}
