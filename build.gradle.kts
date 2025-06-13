plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.33.0")
    testImplementation("org.testng:testng:7.7.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.1.0")
}

tasks.test {
    useTestNG()
}