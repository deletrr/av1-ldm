pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()

        // Node.js binaries (kotlinNodeJsSetup)
        exclusiveContent {
            forRepository {
                ivy {
                    name = "Node Distributions"
                    url = uri("https://nodejs.org/dist")
                    patternLayout { artifact("v[revision]/[artifact]-v[revision]-[classifier].[ext]") }
                    metadataSources { artifact() }
                    content { includeModule("org.nodejs", "node") }
                }
            }
            filter { includeModule("org.nodejs", "node") }
        }

        // Yarn binaries (kotlinYarnSetup)
        exclusiveContent {
            forRepository {
                ivy {
                    name = "Yarn Distributions"
                    url = uri("https://github.com/yarnpkg/yarn/releases/download")
                    patternLayout { artifact("v[revision]/[artifact]-v[revision].[ext]") }
                    metadataSources { artifact() }
                    content { includeModule("com.yarnpkg", "yarn") }
                }
            }
            filter { includeModule("com.yarnpkg", "yarn") }
        }

        // Binaryen (Kotlin/Wasm optimization)
        exclusiveContent {
            forRepository {
                ivy {
                    name = "Binaryen Distributions"
                    url = uri("https://github.com/nicolo-ribaudo/binaryen-builds/releases/download")
                    patternLayout { artifact("[revision]/[artifact]-[revision]-[classifier].[ext]") }
                    metadataSources { artifact() }
                    content { includeModule("aspect-build.aspect-cli", "binaryen") }
                }
            }
            filter { includeModule("aspect-build.aspect-cli", "binaryen") }
        }
    }
}

rootProject.name = "LDM-AV1-KMP"
include(":shared")
include(":androidApp")
