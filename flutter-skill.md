# authorize_net_plugin — flutter-skill Integration Test Flows
_Run these flows manually using the flutter-skill MCP tools after connecting to the example app._

---

## Setup

### Device
| Field | Value |
|-------|-------|
| Simulator | iPhone 17 Pro |
| Device ID | `ED53CA75-876C-42E7-A223-38111BAB976D` |
| Project | `example/` |

### Test Credentials (Authorize.net Sandbox)
| Field | Value |
|-------|-------|
| API Login ID | `7594xDmRzll` |
| Client Key | `34Fg4ta24e5Y6VQ8guqgUKguPLxW7EwqWWd2wSzCjwDUTN65w9SZ2Qk3p95X93cs` |
| Test Card (AmEx) | `370000000000002` |
| CVV | `900` |
| Environment | `test` (sandbox) |

### 1. Launch and connect the app

```bash
# Kill any existing flutter run first
pkill -f "flutter run" 2>/dev/null

# Launch from the example directory
cd example
flutter run -d ED53CA75-876C-42E7-A223-38111BAB976D \
  --vm-service-port=50011 --no-dds \
  > /tmp/flutter_authnet.log 2>&1 &

# Wait for VM Service URI
until grep -q "Dart VM Service" /tmp/flutter_authnet.log; do sleep 2; done
grep "http://127" /tmp/flutter_authnet.log | tail -1
# → copy TOKEN from: http://127.0.0.1:50011/<TOKEN>/

# Connect flutter-skill
mcp__flutter-skill__connect_app(uri: "ws://127.0.0.1:50011/<TOKEN>/ws")
```

> **Prerequisite**: `FlutterSkillBinding.ensureInitialized()` is called in `lib/main.dart` before `runApp()` (already configured).

---

## FLOW-01 · App Launch → Plugin Initialises and Calls Authorize.net

Tests that the plugin loads, makes the API call on startup, and renders a result (success or API error) rather than crashing.

```
screenshot()
# Should show "Plugin example app" title
# Body shows "Running on: <token>" (success) or "Running on: <error message>" (API error)
# Must NOT show "Running on: Unknown" (that means initState never completed)
# Must NOT crash or show a Flutter error screen
```

**Pass criteria:** App launches, shows the result of the Authorize.net API call in the body text. Any response from the API (including error messages) confirms the plugin is wired up and communicating correctly.

---

## FLOW-02 · Successful Token Response

Tests the happy path — plugin returns an `opaqueData` token from Authorize.net sandbox.

> Requires valid, non-expired sandbox API credentials and a future expiry date on the test card.

```
# Verify body text contains a token (starts with "Running on:")
# A successful token response looks like a long alphanumeric string
# e.g. "Running on: 9498938494930394829..." (the opaque data value)

screenshot()
# Body must NOT contain "Failed", "error", or "Unknown"
```

**Pass criteria:** Body displays the opaque data token returned by Authorize.net sandbox.

---

## FLOW-03 · API Error Handling — Expired Card

Tests that the plugin surfaces API errors cleanly without crashing.

Update `example/lib/main.dart` to use a past expiry year:
```dart
expirationYear: '2020',  // expired
```

```
# Hot restart the app
hot_restart()
screenshot()
# Body shows "Running on: Expiration date must be in the future."
# App does NOT crash — error is caught and displayed
```

**Pass criteria:** Expired card causes the API to return an error message, which the plugin surfaces via `PlatformException`. The example app catches it and displays the message string.

---

## FLOW-04 · Invalid Credentials Error Handling

Tests that bad API credentials return an error rather than a crash.

Update `example/lib/main.dart` to use a fake login ID:
```dart
apiLoginId: 'INVALID_ID',
```

```
hot_restart()
screenshot()
# Body shows an Authorize.net authentication error message
# App does NOT show a Flutter red error screen
```

**Pass criteria:** Invalid credentials produce a readable error string in the body. No uncaught exceptions.

---

## Known Issues Fixed

| Fix | Detail |
|-----|--------|
| `PluginRegistry.Registrar` import removed | v1 Flutter embedding import caused `cannot find symbol` on Flutter 3.x. Plugin already used v2 API (`FlutterPlugin` + `ActivityAware`) — unused import was the only issue. |
| Test card expiry updated to `2028` | Hardcoded `2025` year was in the past, causing every test run to hit the "Expiration date must be in the future" error. |
