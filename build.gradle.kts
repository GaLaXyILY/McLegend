import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    kotlin("jvm") version("1.9.24")
    id("io.github.goooler.shadow") version("8.1.7")
    id("xyz.jpenilla.resource-factory-bukkit-convention") version("1.1.1")
    id("io.papermc.paperweight.userdev") version("1.7.1") apply(false)
    id("xyz.jpenilla.run-paper") version("2.3.0")
}

val latest = "1.20.6"
val targetJavaVersion = 21

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    group = "kr.toxicity.mclegend"
    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
    }

    dependencies {
        compileOnly("io.lumine:Mythic-Dist:5.6.2")

        compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.0-SNAPSHOT")
        //compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")

        compileOnly("com.github.toxicity188:BetterHud:beta-23")
        compileOnly("com.github.toxicity188:InventoryFramework:1.3")
        implementation("com.github.toxicity188:DataComponentAPI:1.0.10")

        testImplementation(kotlin("test"))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }
        withType(JavaCompile::class.java) {
            options.encoding = Charsets.UTF_8.name()
        }
        withType(GroovyCompile::class.java) {
            groovyOptions.encoding = Charsets.UTF_8.name()
        }
        test {
            useJUnitPlatform()
        }
    }
    java {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    kotlin {
        jvmToolchain(targetJavaVersion)
    }
}

fun Project.dependency(depend: Any) = also {
    it.dependencies {
        compileOnly(depend)
    }
}
fun Project.paper() = dependency("io.papermc.paper:paper-api:$latest-R0.1-SNAPSHOT")

val api = project("api").paper()
val core = project("core").paper().dependency(api)
val nms = project("nms").subprojects.map {
    it.dependency(api)
}

nms.forEach {
    it.apply(plugin = "io.papermc.paperweight.userdev")
    core.dependency(it)
}

dependencies {
    implementation(api)
    implementation(core)
    nms.forEach {
        implementation(project(":nms:${it.name}", configuration = "reobf"))
    }
}

tasks {
    runServer {
        jvmArgs(
            "-Xmx10G",
            "-XX:+UseG1GC",
            "-XX:+ParallelRefProcEnabled",
            "-XX:MaxGCPauseMillis=200",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+DisableExplicitGC",
            "-XX:+AlwaysPreTouch",
            "-XX:G1NewSizePercent=30",
            "-XX:G1MaxNewSizePercent=40",
            "-XX:G1HeapRegionSize=8M",
            "-XX:G1ReservePercent=20",
            "-XX:G1HeapWastePercent=5",
            "-XX:G1MixedGCCountTarget=4",
            "-XX:InitiatingHeapOccupancyPercent=15",
            "-XX:G1MixedGCLiveThresholdPercent=90",
            "-XX:G1RSetUpdatingPauseTimePercent=5",
            "-XX:SurvivorRatio=32",
            "-XX:+PerfDisableSharedMem",
            "-XX:MaxTenuringThreshold=1",
            "-Dusing.aikars.flags=https://mcflags.emc.gs",
            "-Daikars.new.flags=true",
            "-Dfile.encoding=UTF-8",
            "-Xlog:gc*:logs/gc.log:time,uptime:filecount=5,filesize=1M",
        )
        downloadPlugins {
            url("https://github.com/IntellectualSites/FastAsyncWorldEdit/releases/download/2.10.0/FastAsyncWorldEdit-Bukkit-2.10.0.jar")

            url("https://www.mythiccraft.io/downloads/mythicmobs/free/MythicMobs-5.6.2.jar")

            url("https://github.com/toxicity188/BetterHud/releases/download/beta-23/BetterHud-beta-23.jar")
            url("https://github.com/toxicity188/InventoryFramework/releases/download/1.3/InventoryFramework-1.3.jar")
        }
        version(latest)
    }
    jar {
        finalizedBy(shadowJar)
    }
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
        nms.forEach {
            dependsOn(it.tasks.getByName("reobfJar"))
        }
        archiveClassifier = ""
        fun prefix(pattern: String) {
            relocate(pattern, "${project.group}.shaded.$pattern")
        }
        prefix("kotlin")
        prefix("kr.toxicity.libraries")
    }
}

bukkitPluginYaml {
    main = "${project.group}.McLegendImpl"
    version = project.version.toString()
    name = rootProject.name
    apiVersion = "1.20"

    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
    author = "toxicity"
    depend = listOf(
        "InventoryFramework"
    )
    description = "McLegend core plugin."
}