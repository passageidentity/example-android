<img src="https://storage.googleapis.com/passage-docs/passage-logo-gradient.svg" alt="Passage logo" style="width:250px;"/>

# Example Android App

This example Android app demonstrates the basic integration of the [Passage SDK](https://github.com/passageidentity/passage-android) in an Android Kotlin application. Before implementing the Passage Android SDK in your own app, it may be most helpful to get this example app up and running with your own Passage credentials.

## Requirements

- Android Studio Electric Eel (or newer)
- Android device with Android 13+ installed and Google account setup
- A Passage account and app (you can register for a free account [here](https://passage.id))
- Completed registration of your Android app with Passage (view instructions [here](https://github.com/passageidentity/passage-android))

## Configuration

### Modify `strings.xml` file

Replace `YOUR_APP_ID` and `YOUR_AUTH_ORIGIN` with your app’s Passage app id and auth origin, respectively.
<img width="1011" alt="Screenshot 2023-05-15 at 5 54 58 PM" src="https://github.com/passageidentity/example-android/assets/16176400/35220be6-cc05-4bbf-8c2e-3c9e0e781a65">


### Run the app! 🚀

If all of the configuration was setup correctly, you should be able to run this application in the emulator or on a real device through Android Studio!

NOTE: As of this writing, the Android emulator does not support passkeys, so when run in the emulator this example will fallback to one time passcodes or magic links instead.

A successful registration flow should look like this:
<img width="1069" alt="Screenshot 2023-05-15 at 5 42 31 PM" src="https://github.com/passageidentity/example-android/assets/16176400/0b45d333-edc3-4871-b9fa-71dce3bd48be">


