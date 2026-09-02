# Task Flow — provaandroidM

[Português](README.md) | **English**

An Android application for organizing tasks by **category, priority, and due date**, with local persistence and a progress dashboard. Built with **Kotlin**, it uses XML interfaces, Fragments, View Binding, and an MVVM structure with a ViewModel, repositories, and Room.

The dashboard displays the name **Task Flow**; the installed app is labeled **Prova**. Its interface is in Portuguese; this English README translates the documentation.

> **Current status:** the features below were identified in the source code. This review's build attempt stopped because the environment had no configured Android SDK; on-device behavior was not validated. The “Repete diariamente” option only labels daily tasks; it does not implement automatic recurrence.

## Features

| Area | Implemented features |
| --- | --- |
| **Categories** | Create, list, edit, and delete categories with a name and color. Choose from a palette of 20 colors. |
| **Tasks** | Create, edit, and delete tasks with a title, description, category, priority, optional due date, and daily-task flag. |
| **Priorities** | `Baixa` (low), `Média` (medium), and `Alta` (high); the new-task form selects medium priority by default. |
| **Completion** | Long-press a task to mark it as `Concluída` (completed), either in the task list or on the dashboard. |
| **Due-date grouping** | Lists are divided into overdue, today, next 7 days, next 8–30 days, no deadline/future, and completed tasks. |
| **Dashboard** | Overall completion percentage, progress bars per group, and pending-task groups that can be expanded or collapsed. |
| **Visual identification** | Cards show the category color, status, priority, and due date. On Android 9 and higher, the adapter also configures shadows using the category color. |
| **Persistence** | Room database `taskflow_database`, schema version `3`, keeps categories and tasks between app launches. |

The bottom menu provides access to **Dashboard**, **Tarefas** (tasks), and **Categorias** (categories). The application starts on the dashboard, without sample categories or tasks.

## Usage

After configuring and building the project:

1. Open **Categorias**, tap **+**, enter a name, and choose a color.
2. Open **Tarefas** and tap **+**. At least one category must already exist.
3. Enter a title, select a category and priority, and optionally add a description and a deadline using the date picker.
4. Check **Repete diariamente** (repeats daily) to identify the task as daily. This option does not create new occurrences or reopen completed tasks.
5. Save. Tap a task in **Tarefas** to edit it; the edit dialog also includes **Excluir** (delete).
6. Long-press a task to complete it. The current interface has no reopen action.
7. Open **Dashboard** and tap group headings to expand or collapse their pending tasks.
8. Tap a category to edit its name or color.

**Category deletion:** long-pressing a category deletes it immediately, without confirmation. The database relationship uses `ON DELETE CASCADE`, so **all tasks linked to that category are also deleted**.

## Organization and metrics

### Task list

Grouping uses the current date as its reference:

| Displayed group | Applied rule |
| --- | --- |
| `Atrasadas` (overdue) | Incomplete tasks with a due date before today. |
| `Hoje` (today) | Incomplete tasks due today. |
| `Esta Semana` (this week) | Incomplete tasks due in the next 1–7 days. |
| `Este Mês` (this month) | Incomplete tasks due in the next 8–30 days. |
| `Sem Prazo / Futuro` (no deadline / future) | Incomplete tasks without a deadline or due more than 30 days ahead; date-parsing errors can also place tasks in this group. |
| `Concluídas` (completed) | All tasks with status `Concluída`, regardless of their due date. |

“This Week” and “This Month” represent rolling day ranges, rather than the calendar week and month.

### Dashboard

Each task is assigned to **one group only**, using this precedence: **overdue → daily → high priority → today → next day → next 2–7 days**. An overdue daily task, for example, appears under “Overdue.” Tasks that do not meet these criteria remain available in **Tarefas**.

The dashboard list displays only pending tasks within those groups. Progress bars also count completed tasks classified in each group.

The main indicator calculates:

```text
percentage = completed tasks / total tasks × 100
```

The result is displayed without decimal places; with no tasks, it is `0%`. Despite the label **Progresso de Hoje** (today's progress), the current calculation includes **all stored tasks**, without filtering for today's date.

## Technologies and configuration

| Item | Configuration found |
| --- | --- |
| Language / Kotlin plugin | Kotlin `1.9.22` |
| UI | XML, Fragments, RecyclerView, and View Binding |
| UI components | Material Components `1.13.0`, AppCompat `1.7.1`, and ConstraintLayout `2.2.1` |
| State and architecture | MVVM, AndroidViewModel, LiveData, and repositories |
| Asynchronous operations | Coroutines with `viewModelScope` and `Dispatchers.IO` |
| Persistence | Room `2.6.1` |
| Code processing | KSP `1.9.22-1.0.17` |
| Lifecycle | `2.7.0` |
| Navigation Component | `2.7.7` |
| Android Gradle Plugin | `8.5.0` |
| Gradle Wrapper | `9.3.1` |
| Requested daemon JVM | Java `21`, vendor `JETBRAINS` |
| Java/Kotlin code target | Java/JVM `17` |
| Compile / target SDK | API `34` / API `34` |
| Minimum Android version | Android 7.0 — API `24` |
| Application ID / version | `com.example.prova` / `1.0` — version code `1` |

Versions are defined in `gradle/libs.versions.toml`, the `build.gradle.kts` files, and the Gradle configuration files. This project's interface uses Android Views; its implementation does not use Jetpack Compose.

## Building and running

### Verification result and compatibility

An attempt to run `:app:assembleDebug` with the original files reached dependency resolution for the Java compilation task and stopped with **`SDK location not found`**. No APK was generated, and tests were not run. This result identifies a limitation of the review environment; it does not establish a compilation defect in the application code.

The repository combines **Gradle 9.3.1, AGP 8.5.0, and Kotlin 1.9.22**. This combination falls outside the documented fully supported range for Kotlin 1.9.22 and needs validation in a configured Android environment.

For reference, [AGP 8.5 documents Gradle 8.7, JDK 17, and SDK Build-Tools 34.0.0](https://developer.android.com/build/releases/agp-8-5-0-release-notes#compatibility), while the [Kotlin compatibility matrix](https://kotlinlang.org/docs/gradle-configure-project.html) lists supported ranges for each plugin version. If adjustments are needed, consider Gradle, AGP, Kotlin, and KSP together; changing Java alone does not validate compatibility between them.

### Required environment

- Git and an Android Studio version compatible with the selected AGP version.
- Android SDK Platform **34**, Platform-Tools, and Build-Tools **34.0.0** for the currently declared Android configuration.
- A JVM matching the chosen configuration: the current `gradle/gradle-daemon-jvm.properties` requests **JetBrains JDK 21**, while the code targets Java/JVM 17 bytecode.
- An emulator or device running Android **7.0/API 24** or higher.
- Internet access to download tools and dependencies during initial setup.

The app does not require a login, server, API key, or location permissions. The manifest declares no additional permissions; records are stored in the local database.

### Android Studio

1. Clone the repository:

   ```bash
   git clone https://github.com/Rodrigo-Artur/provaandroidM.git
   cd provaandroidM
   ```

2. Open the folder containing `settings.gradle.kts` in Android Studio.
3. Install the SDK and corresponding tools through **SDK Manager**, and configure the Gradle JVM.
4. Synchronize Gradle. If version-related errors occur, review the toolchain using the compatibility section above.
5. Start an emulator or connect a device with USB debugging enabled.
6. Select the `app` run configuration and click **Run**.

### Terminal

Run the following commands from the repository root, with the JVM configured and the SDK path set through `ANDROID_HOME` or `sdk.dir` in `local.properties`. Successful build completion still needs validation.

**Windows — PowerShell:**

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

**Linux/macOS:**

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

`installDebug` requires a connected device or emulator. After a successful build, the APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Code organization

```text
provaandroidM/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/prova/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── dao/          # Room queries and operations
│       │   │   │   ├── db/           # AppDatabase
│       │   │   │   ├── entity/       # Categoria and Tarefa
│       │   │   │   └── repository/   # Repositories for both models
│       │   │   └── ui/
│       │   │       ├── adapter/      # Cards and list headings
│       │   │       ├── view/         # Dashboard, tasks, and categories
│       │   │       └── viewmodel/    # Shared TarefaViewModel
│       │   └── res/
│       │       ├── layout/           # XML interfaces
│       │       ├── navigation/       # Navigation graph
│       │       ├── menu/             # Bottom menu
│       │       └── values/           # Colors, strings, and themes
│       ├── test/
│       └── androidTest/
├── gradle/
│   ├── libs.versions.toml
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

`MainActivity` connects the bottom menu to `NavHostFragment`. The three Fragments share `TarefaViewModel` through `activityViewModels()`, observe `LiveData`, and send changes to the repositories. Repositories access the Room DAOs; writes are initiated by the ViewModel on `Dispatchers.IO`.

`Categoria` stores `id`, `nome`, and `colorHex`. `Tarefa` stores `id`, `titulo`, `descricao`, `categoriaID`, `prioridade`, `status`, `limitDate`, `isDaily`, and `createdAt`. The category is referenced through a foreign key, with an index on `categoriaID` and cascading deletion.

## Known limitations

- **Incomplete recurrence:** `isDaily` affects labeling and grouping; there is no scheduling, new-occurrence creation, daily reset, or reminder system.
- **Completion cannot be reversed:** the interface marks tasks as completed but does not allow returning them to pending status.
- **Deletion without confirmation:** a long press removes a category together with its tasks. Deleting a task through the form also has no second confirmation.
- **Destructive migrations:** `fallbackToDestructiveMigration()` allows the database to be recreated, losing data when the schema version changes without an available migration.
- **Dates stored as text:** `limitDate` uses `dd/MM/yyyy` or `Sem prazo`. The query sorts this text, so the order within groups is not necessarily chronological across months and years.
- **Inconsistent DAO status:** `getTarefasConcluidas()` searches for `Concluida`, without an accent, while the interface stores `Concluída`. Current screens use `allTarefas` and classify records themselves; the dedicated query does not correctly represent those completions.
- **Misleading metric label:** “Progresso de Hoje” displays the overall completion percentage, rather than today's productivity.
- **Basic validation:** task titles and category names are only checked for nonempty values; whitespace-only entries and duplicates are not specifically handled.
- **Local scope:** the interface has no task-list search, cross-device synchronization, authentication, or data export.

## Tests

The repository includes two sample tests: `ExampleUnitTest`, which checks an addition, and `ExampleInstrumentedTest`, which checks the application ID. They do not cover categories, completion, cascading deletion, date grouping, or migrations.

With the Android environment configured:

**Windows — PowerShell:**

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
```

**Linux/macOS:**

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

The instrumented test requires a connected device or emulator. These commands are execution instructions, not a claim that tests passed.

## Suggested development priorities

The following proposals have not been implemented:

1. Validate the build with a configured SDK and align tool versions to establish a base with documented compatibility.
2. Protect category deletion and replace destructive migrations with migrations that preserve data.
3. Standardize status and date representations, correct the daily metric, and define recurrence and reopening behavior.
4. Add tests for dashboard rules, persistence, completion, and cascading deletion.

## Repository and license

Source code is available at [Rodrigo-Artur/provaandroidM](https://github.com/Rodrigo-Artur/provaandroidM). The reviewed repository does not contain a project-level `LICENSE` file.

Documentation based on source-code review of commit [`207f914`](https://github.com/Rodrigo-Artur/provaandroidM/tree/207f9144a58e99cedf122115d0012ba43da7e7af).
