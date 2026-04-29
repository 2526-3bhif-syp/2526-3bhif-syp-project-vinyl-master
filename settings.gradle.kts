rootProject.name = "vinylmaster"

include("model", "view", "controller")

project(":model").projectDir = file("src/model")
project(":view").projectDir = file("src/view")
project(":controller").projectDir = file("src/controller")
