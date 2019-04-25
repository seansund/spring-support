# spring support

Utilities for SpringBoot applications (logging, api documentation, etc)

## Getting started

### Setup

```bash
npm install
```

### Testing

```bash
npm test
```

**OR**

```bash
./gradlew test
```

### Building

```bash
npm run build
```

**OR**

```bash
./gradlew assemble
```

### Deploying

To push the jar file to the staging repository:

```bash
npm run push
```

**OR**

```bash
./gradlew publish
```

To release the library to the public repository:

```bash
npm run push:release
```

**OR**

```bash
./gradlew closeAndReleaseRepository
```
