# YTTV-Patcher

YTTV-Patcher is an open-source modular APK patching framework specialized for YouTube for Android TV. It provides a clean, extensible architecture for applying patches to Android APKs without copying existing proprietary solutions.

## Architecture

The project follows a clean modular architecture:

- **patcher-api**: Core interfaces and abstractions (Patch, PatchContext, PatchResult, etc.)
- **patcher-core**: Implementation of the patching pipeline (APK loading, analysis, building, signing)
- **patcher-cli**: Command-line interface for running patches
- **patches/test**: Patch implementations including the TestPatch and placeholder patches
- **common**: Shared utilities (Version parsing, ApkInfo)

## Project Structure

```
YTTV-Patcher/
├── patcher-api/          # Public API interfaces
├── patcher-core/         # Core patching engine
├── patcher-cli/          # CLI application
├── patches/test/         # Patch implementations
├── common/               # Shared utilities
├── .github/workflows/    # CI/CD configuration
└── gradle/               # Gradle wrapper
```

## Building

Requirements:
- JDK 17
- Linux/macOS/Windows

```bash
./gradlew build
```

## Running the CLI

### Show APK info
```bash
./gradlew :patcher-cli:run --args="info input.apk"
```

### List available patches
```bash
./gradlew :patcher-cli:run --args="list-patches"
```

### Patch an APK
```bash
./gradlew :patcher-cli:run --args="patch input.apk -o patched.apk"
```

### Enable specific patches
```bash
./gradlew :patcher-cli:run --args="patch input.apk --enable TestPatch --enable DpadFixPatch -o patched.apk"
```

## How Patches Work

Patches implement the `Patch` interface and declare:
- **id**: Unique identifier
- **supportedPackages**: Which apps the patch targets
- **supportedVersions**: Version range compatibility
- **dependencies**: Other patches that must run first
- **options**: Configurable typed options

The `PatchEngine` validates compatibility, resolves dependencies, and executes patches in order.

## Creating a New Patch

1. Create a new module or add to `patches/test`
2. Implement the `Patch` interface
3. Register it in `patcher-cli/src/main/kotlin/com/yttv/patcher/cli/Main.kt`

Example:
```kotlin
class MyPatch : Patch {
    override val id = "MyPatch"
    override val name = "My Patch"
    override val description = "Does something cool"
    override val supportedPackages = listOf("com.google.android.youtube.tv")
    override val supportedVersions = Version.parse("1.0.0")..Version.parse("99.0.0")
    override val dependencies = emptyList<String>()
    override val options = emptyList<PatchOption<*>>()

    override fun execute(context: PatchContext): PatchResult {
        // Your patching logic here
        return PatchResult.Success()
    }
}
```

## GitHub Actions

The repository includes a GitHub Actions workflow that:
- Runs on push, pull requests, and manual dispatch
- Uses Ubuntu with JDK 17
- Executes `./gradlew test` and `./gradlew build`
- Uploads build artifacts on success
- Uploads test reports on failure

## Supported Versions

Currently, the framework is designed for `com.google.android.youtube.tv`. Version compatibility is determined per-patch via the `supportedVersions` range.

## Current Limitations

- The manifest parser is simplified; production use should integrate a proper AXML parser
- APK signing uses a basic marker approach; production should use `apksigner` or proper keystore integration
- DEX bytecode manipulation is not yet implemented
- Resource decoding/encoding is not yet implemented

## Licensing

This project is open source. All code is original and does not copy from proprietary solutions. Third-party dependencies:

- Kotlin Standard Library (Apache 2.0)
- JUnit 5 (EPL 2.0)
- Clikt (Apache 2.0)
- Android Tools (Apache 2.0)

## Important Notes

- This framework does not implement DRM bypass
- This framework does not implement account-security bypass
- This framework does not implement anything intended to defeat platform security mechanisms
