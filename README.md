# Dialog Pass Hider

<p align="center">
  <a href="#english">English</a>
  ·
  <a href="#русский">Русский</a>
</p>

---

<a id="english"></a>

## English

**Dialog Pass Hider** is a client-side Fabric mod that hides passwords in login and registration dialogs.

Masking is applied both to the `password` field and to the confirmation field that follows it, usually named `confirm`.

Each password field has an eye button on the right side that temporarily shows or hides the entered text.

The interface and tooltips are available in English and Russian. The language is selected automatically based on the current Minecraft language. English is used as a fallback for all other languages.

The mod changes only how the password is displayed. The original value remains unchanged and is sent to the server normally.

### Supported versions

After building the project, three files are created in `build/libs`:

- `dialog-pass-hider-2.1.0-mc1.21.6-1.21.8.jar`
- `dialog-pass-hider-2.1.0-mc1.21.9-1.21.11.jar`
- `dialog-pass-hider-2.1.0-mc26.1-26.2.jar`

Install only the JAR that matches your Minecraft version.

### Mask character configuration

When Mod Menu is installed, open the **Dialog Pass Hider** settings and select a single masking character.

The default character is `*`.

Mod Menu is an optional dependency. Password masking and the eye button work without it, but the masking character cannot be changed through the interface.

The configuration is stored in:

```text
config/dialog-pass-hider.properties
