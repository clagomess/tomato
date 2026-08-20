# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Tomato is a 100% offline desktop REST client, built as a Java 17 Swing application (single Maven module). All user data is persisted locally as git-friendly JSON files under `~/.tomato/data` — there is no backend/server. UI is built with FlatLaf (Darcula theme), MigLayout, and RSyntaxTextArea. Main class: `io.github.clagomess.tomato.Main`.

## Commands

Use the Maven wrapper (`./mvnw`, or `mvnw.cmd` on Windows).

- Build + test: `./mvnw test package`
- Run the app: `./mvnw compile exec:java -Dexec.mainClass=io.github.clagomess.tomato.Main` (or run `Main` from the IDE)
- Run a single test class: `./mvnw test -Dtest=HttpServiceTest`
- Run a single test method: `./mvnw test -Dtest=HttpServiceTest#methodName`
- Lint (matches CI — **fails the build on any compiler warning or deprecation**): `./mvnw compile -Dmaven.test.skip -Dmaven.compiler.showDeprecation=true -Dmaven.compiler.failOnWarning=true`

CI (`.github/workflows/test.yml`) runs the lint command and `mvn test package` separately. Keep both green; do not introduce deprecation warnings.

### Dev JVM properties

- `-DTOMATO_LOG_LEVEL=DEBUG` — enable debug logging
- `-DTOMATO_AWAYS_USE_TEST_DATA=true` — force the data dir to the test fixtures (`src/test/resources/.../io/repository/home`) instead of `~/.tomato`

## Architecture

Strict layering, top to bottom: `ui` → `controller` → `io` (+ `mapper`), with `publisher` acting as a cross-cutting event bus and `dto` as the shared data model.

- **`dto.data`** — the persisted domain model (`WorkspaceDto`, `CollectionDto`, `RequestDto`, `EnvironmentDto`, `ConfigurationDto`, …). `TomatoID` is the identifier type. Entities are stored one-per-file as JSON; a workspace contains environments and a tree of collections/requests (see "Data structure" in README.md for the on-disk layout).

- **`io.repository`** — persistence layer. All repositories extend `AbstractRepository`, which resolves the data dir via `getHomeDir()`, (de)serializes with `ObjectMapperUtil`, and caches reads via `CacheManager`. In test mode it asserts every path is under `target/` to prevent tests from touching real user data. Add new persisted entities here, not by writing files directly elsewhere.

- **`io.http`** — request execution built on the JDK `HttpClient`. `HttpService` orchestrates; helpers build URL/headers/bodies (`UrlBuilder`, `HttpHeaderBuilder`, `RequestBuilder`, `MultipartFormDataBody`, `UrlEncodedFormBody`), `SSLContextBuilder`/keystore handles TLS, `HttpDebug` captures the raw exchange. Large responses are streamed to temp files (`createTempFile`, deleted on exit) to avoid OOM.

- **`io.keystore`** — environment secrets are stored in KeePass `.kdbx` files (`EnvironmentKeystore`) rather than plaintext JSON.

- **`io.converter` / `io.snippet` / `io.beautifier`** — Postman import/export + JSON-schema handling, code-snippet generation (curl, etc.), and response beautification.

- **`mapper`** — MapStruct mappers (`RequestMapper`, `CloneMapper`) plus the Postman dump/pump mappers for import/export. MapStruct and Lombok run as annotation processors (configured in `pom.xml`); regenerate by recompiling.

- **`publisher`** — an in-process observer/event bus that decouples UI components and controllers (e.g. saving a request in one tab updates the tree and other tabs). Each publisher is a **singleton** accessed via `getInstance()` (`RequestPublisher`, `WorkspacePublisher`, `EnvironmentPublisher`, `CollectionPublisher`, `SystemPublisher`, …) exposing typed events built on `base.NoKeyPublisher` (broadcast) and `base.KeyPublisher` (scoped to a key like `RequestKey`/`TabKey`). Listeners register callbacks and must remove themselves (UUID-based) on dispose; `BasePublisher.debug()` dumps live listeners. When adding a feature that several UI areas must react to, publish an event rather than calling into other controllers directly.

- **`ui`** — Swing components, organized by feature (`main`, `request`, `collection`, `environment`, `workspace`, `settings`, `menu`, `component`). `MainFrame` is the entry window; dialogs/frames extend `BaseDialog`/`BaseFrame`. Each UI feature is paired with a controller in `controller/` of the same name. UI work runs on the EDT (`SwingUtilities.invokeLater`).

## Conventions

- Nullness is annotated with **JSpecify** (`org.jspecify.annotations`), not JetBrains annotations.
- Lombok is used throughout (`@Slf4j`, `@Getter`, `@RequiredArgsConstructor`, etc.).
- Tests use JUnit 5, AssertJ, Mockito (`mockito-inline`, so static mocking is available), and WireMock for HTTP tests (mappings/fixtures under `src/test/resources`).
- When a test class has **more than one test for the same method**, group them in a `@Nested` inner class named exactly after the method under test (e.g. `@Nested class saveResponse { ... }`), and name the test methods after the scenario only (`whenTargetExists_overwriteContent`).
  Note: `-Dtest=SomeTest` does not run nested tests; use `-Dtest='SomeTest$NestedClass'` or run the full suite.
- **Every newly created file must be `git add`ed** as soon as it is created, so it shows up in the diff/review (staging only — committing still requires an explicit request).
