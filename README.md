# GGWeb: GGWave-via-WebView Android Demo

This project wraps the [GGWave](https://github.com/ggerganov/ggwave) JavaScript implementation and supporting
HTML in a dynamically-created `WebView`, to power an API to allow developers to send data over sound from an
Android application.

The `ggweb/` module contains the HTML and JavaScript as assets, and has a single `GGWeb` class. That class has:

- `send()` to send data over sound
- `startRecording()` to enable receipt of data over sound
- `stopRecording()` to disable receipt of data over sound

The `demo/` module has a basic activity for demonstrating the use of `GGWeb`.

**THIS DEMO IS NOT PRODUCTION-GRADE SOFTWARE AND SERVES ONLY AS A PROOF OF CONCEPT. ANYONE USING THIS DEMO CODE
IN PRODUCTION SHOULD RECONSIDER.**

This project, and GGWave, are licensed under the MIT License.

