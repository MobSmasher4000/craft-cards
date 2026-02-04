plugins {
    id("java")
    id("dev.architectury.loom") version("1.11-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version("2.2.0")
}

group = "org.mob.craftcards"
version = "1.10"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven ( url = "https://cursemaven.com")
    maven ( "https://maven.wispforest.io/releases" )
    maven ( "https://maven.su5ed.dev/releases" )
    maven ( "https://maven.fabricmc.net" )
    maven ( url = "https://maven.blamejared.com/" )
}

dependencies {
    minecraft("net.minecraft:minecraft:1.21.1")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:0.17.3")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.116.6+1.21.1")

    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.6+kotlin.2.2.20")
    modImplementation("com.cobblemon:fabric:1.7.1+1.21.1")
    modImplementation("curse.maven:jei-238222:7181663")
    modImplementation("io.wispforest:accessories-fabric:${properties["accessories_version"]}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(project.properties)
    }
}