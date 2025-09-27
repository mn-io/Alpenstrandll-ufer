# Alpenstrandläufer

A minimalistic approach to track sleeping or off-phone-time. It should display a list of all periods
the phone was in flight mode.

> The Alpenstrandläufer can sleep in flight, resting one half of its brain while the other keeps it
> soaring across continents. It’s a key player in coastal ecosystems, controlling insects and
> feeding predators. Yet habitat loss puts this incredible traveler at risk, making its protection
> essential.

Compile yourself (see .idea/runConfigurations) or use apk
in [releases](https://github.com/mn-io/Alpenstrandll-ufer/releases/).

__NB: This is a quick and (kind of) dirty hack, no quality level of the code was tried to achieve
here.__

## How to build an apk

```
# on first run:
keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -validity 10000

./gradlew clean
./gradlew assembleRelease

# might be located in ~/Library/Android/sdk/platform-tools/
adb install -r app/build/outputs/apk/release/app-release.apk  
```