<img src="https://storage.googleapis.com/passage-docs/passage-logo-gradient.svg" alt="Passage logo" style="width:250px;"/>

# Example Android App with Passage Auth
### 🔑 The easiest way to get passkeys up and running on Android

[Passage](https://passage.id) and the [Passage Android SDK](https://github.com/passageidentity/passage-android) were built to make passkey authentication as fast, simple, and secure as possible. This example Android application is a great place to start. Before using Passage in your own Android app, you can use this example app to:
* Plug in your own Passage app credentials to see passkeys in action
* Learn basic implementation of the Passage Android SDK

A successful registration flow will look like this:

<img width="1069" alt="Screenshot 2023-05-15 at 5 42 31 PM" src="https://github.com/passageidentity/example-android/assets/16176400/0b45d333-edc3-4871-b9fa-71dce3bd48be">

<br>

## Requirements

- Android Studio Electric Eel (or newer)
- Android device with Android 13+ installed and Google account setup
- A Passage account and app (you can register for a free account [here](https://passage.id))
- Completed registration of your Android app with Passage (view instructions [here](https://github.com/passageidentity/passage-android))
  - NOTE: When setting up your `assetlinks.json` file, you'll need to add `id.passage.example_android` to your target list OR change the package name of this example app to match your Android app's package name.

<br>

## Configuration

### ✏️ Modify `strings.xml` file

In the [strings.xml file](https://github.com/passageidentity/example-android/blob/main/app/src/main/res/values/strings.xml) replace `YOUR_APP_ID` and `YOUR_AUTH_ORIGIN` with your app’s Passage app id and auth origin, respectively. Learn more about Passage app ids and auth origins [here](https://docs.passage.id/mobile/android/add-passage).
<img width="1011" alt="Screenshot 2023-05-15 at 5 54 58 PM" src="https://github.com/passageidentity/example-android/assets/16176400/35220be6-cc05-4bbf-8c2e-3c9e0e781a65">


### 🚀 Run the app!

If all of the configuration was setup correctly, you should be able to run this application in the emulator or on a real device through Android Studio!
