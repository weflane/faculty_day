plugins {
    id("buildlogic.kotlin-library-conventions")
}
dependencies {
    testImplementation(kotlin("test"))
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    ignoreFailures = true
}