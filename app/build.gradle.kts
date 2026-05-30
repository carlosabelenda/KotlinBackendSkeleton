plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
    kotlin("plugin.serialization") version "2.2.20"
}
kotlin {
    jvmToolchain(21)
}
dependencies {
    // Security constraints for transitive dependencies
    constraints {
        implementation("io.netty:netty-codec-http:4.2.9.Final") // CVE-2025-67735
        testImplementation("commons-codec:commons-codec:1.18.0") // Info disclosure fix
    }

    // Ktor BOM (CRITICAL)
    implementation(platform("io.ktor:ktor-bom:3.3.3"))

    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    implementation("ch.qos.logback:logback-classic:1.5.25")

    // Ktor (versiones gestionadas por BOM)
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-status-pages")

    // OpenAPI / Swagger UI
    implementation("io.github.smiley4:ktor-openapi:5.4.0")
    implementation("io.github.smiley4:ktor-swagger-ui:5.4.0")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("com.h2database:h2:2.4.240")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Testing (JUnit 4)
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.3")

}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass.set("com.decksolutions.kotlinbackendskeleton.ApplicationKt")
}
