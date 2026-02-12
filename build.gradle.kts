plugins {
    id("java")
    id("dev.architectury.loom") version("1.11-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version("2.2.20")
}


group = "org.mob.craftcards"
version = "1.21.1-1.2"

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    silentMojangMappingsLicense()
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.neoforged.net")
    maven("https://maven.parchmentmc.org")
    maven("https://cursemaven.com")

    maven("https://maven.wispforest.io/releases" )
    maven("https://maven.su5ed.dev/releases" )
    maven("https://maven.blamejared.com/" )
    maven("'https://maven.fabricmc.net" )

}

dependencies {
    minecraft("net.minecraft:minecraft:1.21.1")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.1:2024.11.17")
    })
    neoForge("net.neoforged:neoforge:21.1.182")

    compileOnly ("mezz.jei:jei-${properties["jei_mc_version"]}-common-api:${properties["jei_version"]}")
    compileOnly ("mezz.jei:jei-${properties["jei_mc_version"]}-neoforge-api:${properties["jei_version"]}")
    runtimeOnly ("mezz.jei:jei-${properties["jei_mc_version"]}-neoforge:${properties["jei_version"]}")

    implementation("curse.maven:curios-309927:6529130")


    modImplementation("com.cobblemon:neoforge:1.7.2+1.21.1")
    //Needed for cobblemon
    implementation("thedarkcolour:kotlinforforge-neoforge:5.10.0") {
        exclude("net.neoforged.fancymodloader", "loader")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(project.properties)
    }
}