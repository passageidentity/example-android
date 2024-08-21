<img src="https://storage.googleapis.com/passage-docs/passage-logo-gradient.svg" alt="Passage logo" style="width:250px;"/>

# Example Android App with Passage Auth
### 🔑 The easiest way to get passkeys up and running on Android

[Passage](https://passage.id) and the [Passage Android SDK](https://github.com/passageidentity/passage-android) were built to make passkey authentication as fast, simple, and secure as possible. This example Android application is a great place to start. Before using Passage in your own Android app, you can use this example app to:

* Login/Register with Passkeys: Experience secure and user-friendly passkey authentication.

* Login/Register with One-Time Passcode (OTP): Implement OTP-based authentication for enhanced security.

* Login/Register with Magic Link: Provide users with a seamless login experience using magic links.

* Login/Register with Hosted Login: Utilize hosted login to handle authentication externally.

<img width="700" height="600" alt="image" src="https://github.com/user-attachments/assets/674fd6e6-0330-42e2-b467-eada7709f509">


<br>

## Requirements

- Android Studio Electric Eel (or newer)
- Android device with Android 13+ installed and Google account setup
- A Passage account and app (you can register for a free account [here](https://passage.id))
- Completed registration of your Android app with Passage (view instructions [here](https://docs.passage.id/mobile/android/add-passage))
  - NOTE: When setting up your `assetlinks.json` file, you'll need to add `id.passage.example_android` to your target list OR change the package name of this example app to match your Android app's package name.

<br>

## Configuration

### ✏️ Modify `strings.xml` file

In the [strings.xml file](https://github.com/passageidentity/example-android/blob/main/app/src/main/res/values/strings.xml) replace`YOUR_AUTH_ORIGIN` with your app’s Passage auth origin, respectively. Learn more about Passage app ids and auth origins [here](https://docs.passage.id/getting-started/creating-a-new-app).
<img width="927" alt="image" src="https://github.com/user-attachments/assets/a4ce766a-12f0-468d-a071-2e19ac3edc25">



### 🚀 Run the app!

If all of the configuration was setup correctly, you should be able to run this application in the emulator or on a real device through Android Studio!
