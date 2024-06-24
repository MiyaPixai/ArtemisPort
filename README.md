Unofficial Artemis Ports
========
[![GitHub](https://img.shields.io/github/downloads/MiyaPixai/ArtemisPort/total?logo=github)](https://github.com/MiyaPixai/ArtemisPort/releases)
[![License](https://img.shields.io/badge/license-LGPL%203.0-green.svg)](https://github.com/Wynntils/Artemis/blob/main/LICENSE)

This is an unofficial port of [Artemis](https://github.com/Wynntils/Artemis) to other minecraft versions not currently supported by the mod.

Downloading a release
========
You can download the latest builds from the [releases page](https://github.com/MiyaPixai/ArtemisPort/releases).

Issues
========
Please fill issues you encounter with ported versions [here](https://github.com/MiyaPixai/ArtemisPort/issues) and NOT to the Artemis github.
While I try to test releases as much as I can, I cannot test everything and issues not present in Artemis may slip in ported versions.

If you'd like to request for a specific version not supported by either Artemis or Wynntils, feel free to file an issue here.

Workspace Setup
========

### Initial Setup
To set up the workspace, just import the project as a gradle project into your IDE. You might have to run `./gradlew --refresh-dependencies` if your IDE does not automatically do it.

### Building
To build the mod just run `./gradlew buildDependents` and the artifacts should be generated in `fabric/build/libs` and `forge/build/libs`. There are a lot of jars there, use the jar which has the respective loader at the end (eg. `wynntils-VERSION-fabric.jar`).

### Code Formatting
The code format is checked by Spotless using the Palantir engine. When opening a PR, Spotless checks will be run automatically by GitHub Actions. This bot runs the `spotlessApply` which fixes all formatting errors that it can find. If such errors are found, the bot will then push a commit to your branch with these fixes.

However, it can be nice to fix the formatting locally before pushing. There are several ways to do this.

* The most basic is to run the same gradle target as the bot, `spotlessApply`. This can be done from the command line `./gradlew spotlessApply` or as a gradle target from your IDE.

* If you are using IntelliJ IDEA, you can install the [Spotless Gradle plugin](https://plugins.jetbrains.com/plugin/18321-spotless-gradle) to get an easy and quick way of running the Spotless target for the current editor.

* To make sure your commits always pass the Spotless check, you can install a git pre-commit hook. The hook is optional to use. If you want to use it, you must tell git to pick up hooks from the directory containing the hook. To do this, run this from your repo root: `git config core.hooksPath utils/git-hooks`.

* Finally, you can also use the Palantir formatting directly. This will skip the additional non-Palantir rules we have in Spotless, but these are few and unlikely to affect most cases. For IntelliJ, there is the [Palantir plugin](https://plugins.jetbrains.com/plugin/13180-palantir-java-format) that is supposed to do that, but at the moment it unfortunately seems to be broken.

### Hot-swapping
Using the Hotswap Agent is recommended if you want to do live code editing. Please note that at the moment, only version [Hotswap Agent 1.4.2](https://github.com/HotswapProjects/HotswapAgent/releases/tag/1.4.2-SNAPSHOT) works. See [Hotswap Agent installation instructions](http://hotswapagent.org/mydoc_quickstart-jdk17.html),
but bear in mind that the instructions are incorrect (!). Don't "unpack" `hotswap-agent.jar`, instead
rename the downloaded jar file to `hotswap-agent.jar`. Finally, add `wynntils.hotswap=true` in your personal `gradle.properties` file.
By default, this is `C:\Users\<your username>\.gradle\gradle.properties` on Windows, or `~/.gradle/gradle.properties` on Linux/MacOS.
If the file does not exist, you can create it yourself.
Don't forget to set the correct Java installation for your run configurations, and make sure to use [these](https://i.imgur.com/4VMFCM0.png) settings in IntelliJ IDEA.
After this, start the game using your run configurations, you should see some logs about HotswapAgent. If you don't see any, you did something wrong. To hotswap, hit the build button (build, not rerun, restart, etc.).

### Run Configurations and Authenticating
Architectury Loom currently only supports VSCode and IntelliJ IDEA. Eclipse is not supported by upstream at the moment. After running Initial Setup, run configurations should appear automatically (note that you might have to restart your IDE after Initial Setup).

The project has [DevAuth](https://github.com/DJtheRedstoner/DevAuth) set up by default. When you run the development run configurations, you will get a link to log in with your Microsoft account. After first login, you will be able to run the game like you would in a production environment. You can use an alt configuration by specifying `-Ddevauth.account=alt` in your JVM options, or by temporarily changing `.devauth/config.toml`.

### Vineflower decompiler
The project has [Vineflower for Loom](https://github.com/Juuxel/loom-vineflower) set-up automatically. This is done so to highly increase the quality of decompiled sources. To use it, run `./gradlew genSourcesWithVineflower`. After it finished, the decompiled Minecraft source will be in `minecraft-project-@common-merged-named-sources.jar` You have to attach these sources in Intellij IDEA for Vineflower to take effect.

License
========

Artemis and therefor, ArtemisPort is licensed under the license [GNU Lesser General Public License v3.0](https://github.com/MiyaPixai/ArtemisPort/blob/main/LICENSE)

Unless specified otherwise, All the assets **are over Wynntils domain © Wynntils**.
