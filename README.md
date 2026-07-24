# Dialog Pass Hider

## English

**Dialog Pass Hider** is a client-side Fabric mod that hides passwords in login and registration dialogs.

Masking is applied both to the `password` field and to the confirmation field that follows it (`confirm`). Each password field has an eye button on the right that temporarily reveals or hides the entered text.

The interface and tooltips are available in English and Russian. The language is selected automatically based on the current Minecraft language. English is used as the fallback for all other languages.

The mod only changes how the password is displayed. The original value remains unchanged and is sent to the server normally.

### Supported versions

After building, three files are generated in `build/libs`:

- `dialog-pass-hider-2.1.0-mc1.21.6-1.21.8.jar`
- `dialog-pass-hider-2.1.0-mc1.21.9-1.21.11.jar`
- `dialog-pass-hider-2.1.0-mc26.1-26.2.jar`

Install only the JAR that matches your Minecraft version.

### Mask character configuration

When Mod Menu is installed, open the **Dialog Pass Hider** settings and select a single masking character. The default character is `*`.

Mod Menu is an optional dependency. Password masking and the eye button work without it, but the masking character cannot be changed through the interface.

The configuration is stored in:

```text
config/dialog-pass-hider.properties
```

### Building

JDK 25 or newer is required:

```shell
./gradlew clean build
```

<br>

---

<br>

# Dialog Pass Hider

## Русский

**Dialog Pass Hider** — клиентский Fabric-мод, который скрывает пароли в диалогах входа и регистрации.

Маскирование применяется и к полю `password`, и к следующему за ним полю подтверждения (`confirm`). Справа от каждого парольного поля находится кнопка-глаз, которая временно показывает или снова скрывает введённый текст.

Интерфейс и подсказки переведены на русский и английский. Язык выбирается автоматически вместе с языком Minecraft. Для остальных языков используется английский вариант.

Мод меняет только отображение пароля. Исходное значение остаётся в поле без изменений и отправляется серверу в оригинальном виде.

### Поддерживаемые версии

После сборки в `build/libs` появляются три файла:

- `dialog-pass-hider-2.1.0-mc1.21.6-1.21.8.jar`
- `dialog-pass-hider-2.1.0-mc1.21.9-1.21.11.jar`
- `dialog-pass-hider-2.1.0-mc26.1-26.2.jar`

Нужно установить только один JAR, соответствующий версии Minecraft.

### Настройка символа маскирования

Если установлен Mod Menu, откройте настройки **Dialog Pass Hider** и задайте один символ маскирования. По умолчанию используется `*`.

Mod Menu является необязательной зависимостью. Без него скрытие пароля и кнопка-глаз работают, но изменить символ через интерфейс нельзя.

Настройка хранится в:

```text
config/dialog-pass-hider.properties
```

### Сборка

Требуется JDK 25 или новее:

```shell
./gradlew clean build
```
