plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.17.0"
    id("org.jetbrains.intellij.platform.grammarkit") version "2.17.0"
}

group = "fr.hardel.yu.idea"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.1.3")
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "fr.hardel.yu.idea"
        name = "Yu (.mcui)"
        description = "Support d'édition du langage .mcui du moteur Yu : coloration, complétion et navigation croisée Java vers .mcui."
        vendor {
            name = "Hardel"
        }
        ideaVersion {
            sinceBuild = "261"
            untilBuild = "261.*"
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/sources/grammarkit/lexer")
            srcDir("build/generated/sources/grammarkit/parser")
        }
    }
}

tasks {
    generateLexer {
        sourceFile.set(file("src/main/grammar/Mcui.flex"))
        targetRootOutputDir.set(file("build/generated/sources/grammarkit/lexer"))
        packageName.set("fr.hardel.yu.idea.lang.lexer")
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/grammar/Mcui.bnf"))
        targetRootOutputDir.set(file("build/generated/sources/grammarkit/parser"))
        purgeOldFiles.set(true)
    }

    compileJava {
        dependsOn(generateLexer, generateParser)
    }

    withType<JavaCompile>().configureEach {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}
