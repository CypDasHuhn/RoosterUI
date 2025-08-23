plugins {
    kotlin("jvm")
}

group = "dev.cypdashuhn.rooster.localization"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    implementation("com.google.code.gson:gson:2.11.0")
    implementation("net.kyori:adventure-api:4.17.0")

    implementation(project(":RoosterCommon"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}