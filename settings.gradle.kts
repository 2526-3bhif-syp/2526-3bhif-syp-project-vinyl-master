rootProject.name = "vinylmaster"

include("backend", "frontend")

project(":backend").projectDir = file("src/backend")
project(":frontend").projectDir = file("src/frontend")
