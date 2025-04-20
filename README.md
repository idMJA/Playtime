# Playtime Plugin

A powerful and flexible Minecraft plugin for tracking player playtime across multiple server versions.

## Features

- 🕒 Accurate playtime tracking using Minecraft's built-in statistics
- 📊 Top players leaderboard
- 🎨 Fully customizable messages and colors
- 🔌 PlaceholderAPI support
- 🌐 Multi-version support (1.8.8 - 1.21.4)
- ⚡ Optimized performance with minimal resource usage

## Requirements

- Java requirements per Minecraft version (see [Compatibility Table](#compatibility-table))
- Paper/Spigot server
- Optional: PlaceholderAPI for placeholders support

## Installation

1. Download the latest release from the [releases page](https://github.com/idMJA/Playtime/releases)
2. Place the jar file in your server's `plugins` folder
3. Restart your server
4. Edit the configuration in `plugins/Playtime/config.yml` if desired

## Commands

- `/playtime` - View your playtime
- `/playtime [player]` - View another player's playtime
- `/playtime top [amount]` - View top players
- `/playtimereload` - Reload the plugin configuration

## Permissions

- `playtime.use` - Allow using the playtime command (default: true)
- `playtime.others` - Allow viewing other players' playtime (default: op)
- `playtime.top` - Allow viewing the top playtime list (default: true)
- `playtime.reload` - Allow reloading the plugin configuration (default: op)

## Configuration

```yaml
# Whether to show playtime in days, hours, minutes format
show-detailed-time: true

# Message formats
messages:
  playtime-self: "&7Your PlayTime:\n&a%time%"
  playtime-other: "&7%player%'s PlayTime:\n&a%time%"
  joins: "&7%joins%x Joined"
  # ... more messages

# Color settings
colors:
  primary: "&a"
  secondary: "&7"
  accent: "&e"
  error: "&c"
  success: "&a"
```

## PlaceholderAPI Placeholders

- `%playtime_time%` - Player's formatted playtime
- `%playtime_position%` - Player's position in the leaderboard
- `%playtime_joins%` - Number of times player has joined

## Building from Source

### Compatibility Table

| Minecraft Version | Required Java | Recommended Java | Paper Build |
|------------------|---------------|------------------|-------------|
| 1.21.4 | Java 17+ | Java 21 | 150 |
| 1.21.3 | Java 17+ | Java 21 | 82 |
| 1.21.1 | Java 17+ | Java 21 | 132 |
| 1.21 | Java 17+ | Java 21 | 130 |
| 1.20.6 | Java 17+ | Java 21 | 151 |
| 1.20.5 | Java 17+ | Java 21 | 22 |
| 1.20.4 | Java 17+ | Java 21 | 499 |
| 1.20.2 | Java 17+ | Java 21 | 318 |
| 1.20.1 | Java 17+ | Java 21 | 196 |
| 1.20 | Java 17+ | Java 21 | 17 |
| 1.19.4 | Java 17+ | Java 21 | 550 |
| 1.19.3 | Java 17+ | Java 21 | 448 |
| 1.19.2 | Java 17+ | Java 21 | 307 |
| 1.19.1 | Java 17+ | Java 21 | 111 |
| 1.19 | Java 17+ | Java 21 | 81 |
| 1.18.2 | Java 17+ | Java 17 | 388 |
| 1.18.1 | Java 17+ | Java 17 | 216 |
| 1.18 | Java 17+ | Java 17 | 66 |
| 1.17.1 | Java 16+ | Java 16 | 411 |
| 1.17 | Java 16+ | Java 16 | 79 |
| 1.16.5 | Java 11+ | Java 11 | 794 |
| 1.16.4 | Java 11+ | Java 11 | 416 |
| 1.16.3 | Java 11+ | Java 11 | 253 |
| 1.16.2 | Java 11+ | Java 11 | 189 |
| 1.16.1 | Java 11+ | Java 11 | 138 |
| 1.15.2 | Java 8+ | Java 8 | 393 |
| 1.15.1 | Java 8+ | Java 8 | 62 |
| 1.15 | Java 8+ | Java 8 | 21 |
| 1.14.4 | Java 8+ | Java 8 | 245 |
| 1.14.3 | Java 8+ | Java 8 | 134 |
| 1.14.2 | Java 8+ | Java 8 | 107 |
| 1.14.1 | Java 8+ | Java 8 | 50 |
| 1.14 | Java 8+ | Java 8 | 17 |
| 1.13.2 | Java 8+ | Java 8 | 657 |
| 1.13.1 | Java 8+ | Java 8 | 386 |
| 1.13 | Java 8+ | Java 8 | 173 |

### Prerequisites

1. Install JDK:
   - For latest versions (1.21.x - 1.19.x): Install [Java 21](https://adoptium.net/)
   - For specific versions: Install Java version according to the table above

2. Install Git:
   - Windows: Download and install from [git-scm.com](https://git-scm.com/)
   - Linux: `sudo apt install git` (Ubuntu/Debian) or `sudo dnf install git` (Fedora)
   - macOS: `brew install git` (using Homebrew)

### Building Steps

1. Clone the repository:

```bash
git clone https://github.com/idMJA/Playtime.git
cd Playtime
```

2. Build for latest version (1.21.4):

```bash
# Windows
.\gradlew.bat clean build

# Linux/macOS
./gradlew clean build
```

3. Build for specific version:

```bash
# Windows
.\gradlew.bat clean build -PmcVersion=1.16.5

# Linux/macOS
./gradlew clean build -PmcVersion=1.16.5
```

4. Find the built plugin:
   - The compiled jar will be in `build/libs/Playtime-1.0-SNAPSHOT.jar`
   - Copy this file to your server's `plugins` folder

### Development Server

Run test server for any version:

```bash
# Latest version (1.21.4)
.\gradlew.bat runServer

# Specific version (example: 1.16.5)
.\gradlew.bat runServer1_16_5
```

First run will:

1. Download appropriate Paper version
2. Create server directory in `run/`
3. Generate eula.txt (you need to accept it)
4. Install the plugin automatically

## Building for Production

#### Prerequisites

1. Install JDK 21 (recommended for latest versions)
2. Install Git
3. Install [Gradle](https://gradle.org/install/) (optional, but recommended for production builds)

#### Production Build Steps

1. Clone the repository:

```bash
git clone https://github.com/idMJA/Playtime.git
cd Playtime
```

2. Clean and build with production settings:

```bash
# Windows
.\gradlew.bat clean build --no-daemon --parallel --build-cache

# Linux/macOS
./gradlew clean build --no-daemon --parallel --build-cache
```

3. Find the production build:
   - Location: `build/libs/Playtime-1.0-SNAPSHOT.jar`
   - Size: Should be around 100-200KB
   - SHA-256 checksum will be generated in `build/libs/Playtime-1.0-SNAPSHOT.jar.sha256`

#### Production Build Options

1. Build for specific Minecraft version:

```bash
# Example for 1.16.5
./gradlew clean build -PmcVersion=1.16.5 --no-daemon --parallel --build-cache
```

2. Build with custom version:

```bash
# Example: Set version to 1.0.0
./gradlew clean build -Pversion=1.0.0 --no-daemon --parallel --build-cache
```

3. Build with debug information removed:

```bash
./gradlew clean build -PstripDebug=true --no-daemon --parallel --build-cache
```

#### Production Build Best Practices

1. Always use `--no-daemon` flag to ensure clean builds
2. Use `--parallel` for faster builds on multi-core systems
3. Enable `--build-cache` for faster subsequent builds
4. Verify the build:
   - Check file size (should be reasonable)
   - Verify SHA-256 checksum
   - Test on a clean server instance

#### Building for Multiple Versions

1. Create a build script (build.sh or build.bat):

```bash
#!/bin/bash
versions=("1.16.5" "1.17.1" "1.18.2" "1.19.4" "1.20.4" "1.21.4")

for version in "${versions[@]}"
do
    echo "Building for Minecraft $version..."
    ./gradlew clean build -PmcVersion=$version --no-daemon --parallel --build-cache
    mv build/libs/Playtime-1.0-SNAPSHOT.jar build/libs/Playtime-$version.jar
done
```

2. Run the script:

```bash
# Make executable (Linux/macOS)
chmod +x build.sh
./build.sh
```

#### Verifying Production Builds

1. Check build output:

```bash
# Windows
certutil -hashfile build/libs/Playtime-1.0-SNAPSHOT.jar SHA256

# Linux/macOS
shasum -a 256 build/libs/Playtime-1.0-SNAPSHOT.jar
```

2. Test the build:
   - Copy to a clean test server
   - Verify plugin loads without errors
   - Check all features work as expected
   - Monitor server performance

#### Troubleshooting Production Builds

1. If build fails:
   - Check Java version compatibility
   - Verify Gradle version
   - Clean Gradle cache: `./gradlew cleanBuildCache`

2. If plugin doesn't load:
   - Check server logs
   - Verify dependencies
   - Test with different Java versions

3. If performance issues:
   - Check for memory leaks
   - Monitor CPU usage
   - Verify thread safety

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have suggestions:

1. Check the [Issues](https://github.com/idMJA/Playtime/issues) page
2. Create a new issue if your problem isn't already listed
3. Join our Discord server for support (<https://dc.gg/tx>)
