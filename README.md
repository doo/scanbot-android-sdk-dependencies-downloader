## Scanbot Android SDK dependencies artifacts downloader

Tool for downloading Scanbot Android SDK artifacts for offline usage.

### Usage

There are two tasks available from this project:
 - `fetchDeps` - for downloading the `*.aar` dependencies artifacts;
 - `printDeps` - for printing a full list of SDK's dependencies. The latter might be needed to resolve dependencies within you own project.

Running tasks can be done using your IDE's interface (e.g. for IntelliJ IDEA you need to double press Shift, enter `Execute Gradle Task` then enter `gradle <TASK_NAME>` and press Enter).

Alternatively, you can run tasks using command line from this project's root folder, which we will focus on in this readme.

Params for running tasks can be specified after `-P`:

`<gradle_task> -Pparam_name=value`

Value can be omitted (used like a flag):

`<gradle_task> -Pparam_name`

### Downloading artifacts

To download Scanbot SDK dependencies you need to execute `fethDeps` task:

```bash
cd ~/my_projects_folder/scanbot-android-sdk-dependencies-downloader
./gradlew fetchDeps
```

It offers the following params:
 - `version`: version of Scanbot SDK to download. Currently, defaults to 1.86.0
 - flag `full`: defines whether to download all dependencies, including transitive, or only ScanbotSDK artifacts. Omitted by default.

For example, to download all artifacts for version 1.86.0 the command would look like:

`./gradlew fetchDeps -Pversion=1.86.0 -Pfull`

### Printing dependencies

Additional information might help you resolve dependencies conflicts. To obtain it you can use `printDeps` task:

`./gradlew printDeps`

It offers the following params:
- `version`: version of Scanbot SDK to download. Currently, defaults to 1.86.0
- flag `full`: defines whether to also show Scanbot SDK dependencies, or only transitive ones. Omitted by default.

For example, to print all dependencies for version 1.86.0 the command would look like:

`./gradlew printDeps -Pversion=1.86.0 -Pfull`

### License

TODO
