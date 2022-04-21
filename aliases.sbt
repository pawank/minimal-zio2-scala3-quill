import Util._

addCommandAlias("l", "projects")
addCommandAlias("ll", "projects")
addCommandAlias("ls", "projects")
addCommandAlias("cd", "project")
//addCommandAlias("root", "cd zioplayground")
addCommandAlias("c", "compile")
addCommandAlias("ca", "Test / compile")
addCommandAlias("t", "test")
addCommandAlias("r", "run")
addCommandAlias("rs", "~reStart")
addCommandAlias("s", "~reStop")
//addCommandAlias("depcheck", "dependencyUpdates")
addCommandAlias("runrest", "runMain rapidor.api.RestService")
addCommandAlias("runapi", "runMain rapidor.server.ApplicationServer")
addCommandAlias("runcli", "runMain rapidor.Application")
addCommandAlias(
  "styleCheck",
  "scalafmtSbtCheck; scalafmtCheckAll",
)
addCommandAlias(
  "styleFix",
  "scalafmtSbt; scalafmtAll",
)
//addCommandAlias( "up2date", "reload plugins; dependencyUpdates; reload return; dependencyUpdates",)

onLoadMessage +=
  s"""|
      |╭─────────────────────────────────╮
      |│     List of defined ${styled("aliases")}     │
      |├─────────────┬───────────────────┤
      |│ ${styled("l")} | ${styled("ll")} | ${styled("ls")} │ projects          │
      |│ ${styled("cd")}          │ project           │
      |│ ${styled("root")}        │ cd root           │
      |│ ${styled("c")}           │ compile           │
      |│ ${styled("ca")}          │ compile all       │
      |│ ${styled("t")}           │ test              │
      |│ ${styled("r")}           │ run               │
      |│ ${styled("rs")}          │ ~reStart          │
      |│ ${styled("s")}           │ ~reStop           │
      |│ ${styled("runrest")}     │ RestService       │
      |│ ${styled("runapi")}      │ ApplicationServer │
      |│ ${styled("runcli")}      │ Application       │
      |│ ${styled("styleCheck")}  │ fmt check         │
      |│ ${styled("styleFix")}    │ fmt               │
      |│ ${styled("up2date")}     │ dependencyUpdates │
      |╰─────────────┴───────────────────╯""".stripMargin
