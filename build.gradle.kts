plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.13.6"
}

repositories {
    cloche {
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenForge()
        mavenFabric()
        mavenParchment()
        librariesMinecraft()
        main()
    }
    mavenCentral()
    maven("https://api.modrinth.com/maven")
}

group = "dev.worldgen.abridged"
version = "2.0.0"

cloche {
    mappings {
        official()
    }

    metadata {
        modId = "abridged"
        name = "Abridged"
        description = "Bridges big and small across rivers."
        license = "MIT"
        icon = "pack.png"

        author("Apollo")
    }

    common {
        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.3")
            compileOnly("maven.modrinth:lithostitched:1.4.11-forge-1.20")
        }
    }
    val shared120 = common("shared:1.20") {}
    val shared121 = common("shared:1.21") {}
    val shared1219 = common("shared:1.21.9") {}

    fabric("fabric:1.20.1") {
        dependsOn(shared120)

        loaderVersion = "0.16.13"
        minecraftVersion = "1.20.1"
        mixins.from(file("src/common/main/abridged.mixins.json"))

        dependencies {
            fabricApi("0.92.6")
            modRuntimeOnly("maven.modrinth:lithostitched:1.4.11-fabric-1.20")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.10")
                }
            }

            entrypoint("main") {
                value = "dev.worldgen.abridged.fabric.AbridgedFabric"
            }
        }

    }

    fabric("fabric:1.21.1") {
        dependsOn(shared121)

        loaderVersion = "0.16.13"
        minecraftVersion = "1.21.1"
        mixins.from(file("src/common/main/abridged.mixins.json"))

        dependencies {
            fabricApi("0.116.1")
            modRuntimeOnly("maven.modrinth:lithostitched:1.4.11-fabric-1.21")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.10")
                }
            }

            entrypoint("main") {
                value = "dev.worldgen.abridged.fabric.AbridgedFabric"
            }
        }
    }

    fabric("fabric:1.21.9") {
        dependsOn(shared1219)

        loaderVersion = "0.17.2"
        minecraftVersion = "1.21.9"
        mixins.from(file("src/common/main/abridged.mixins.json"))

        dependencies {
            fabricApi("0.134.0")
            modRuntimeOnly("maven.modrinth:lithostitched:1.5.0-fabric-1.21.9")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.10")
                }
            }

            entrypoint("main") {
                value = "dev.worldgen.abridged.fabric.AbridgedFabric"
            }
        }
    }

    forge("forge:1.20.1") {
        dependsOn(shared120)

        loaderVersion = "47.4.0"
        minecraftVersion = "1.20.1"

        dependencies {
            modRuntimeOnly("maven.modrinth:lithostitched:1.4.11-forge-1.20")
        }

        runs {
            client()
            server()
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.10")
                }
            }
        }
    }

    neoforge("neoforge:1.21.1") {
        dependsOn(shared121)

        loaderVersion = "21.1.206"
        minecraftVersion = "1.21.1"

        dependencies {
            modRuntimeOnly("maven.modrinth:lithostitched:1.4.11-neoforge-1.21")
        }

        runs {
            client()
            server()
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.10")
                }
            }
        }
    }

    neoforge("neoforge:1.21.9") {
        dependsOn(shared1219)

        loaderVersion = "21.9.11-beta"
        minecraftVersion = "1.21.9"

        dependencies {
            modRuntimeOnly("maven.modrinth:lithostitched:1.5.0-neoforge-1.21.9")
        }

        runs {
            client()
            server()
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.10")
                }
            }
        }
    }
}