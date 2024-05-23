fun RepositoryHandler.projectRepository() {
    maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public") }
    mavenCentral()
    gradlePluginPortal()
}

subprojects {
    repositories {
        projectRepository()
    }
}

repositories {
    projectRepository()
}

