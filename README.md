# Kanji Dojo

<img src="preview_assets//inkscape_icon.svg" height=120 align="right">

Learn & practice writing Japanese characters

### Features
- Study **kana** (both Hiragana and Katakana) and kanji
- Practice by following JLPT levels or school grades
- Create your own list to study, <b>more than 6000 characters</b> in total are available
- Works <b>offline</b>

There is separate repository for dictionaries parser and data here: [Kanji-Dojo-Data](https://github.com/syt0r/Kanji-Dojo-Data)

<details>
<summary><h3 style="display: inline">Screenshots</h3></summary>

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/7.png" height="500"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/8.png" height="500"/>

</details>

### Downloads

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/ua.syt0r.kanji.fdroid/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=ua.syt0r.kanji)

Or get the latest APK from the [Releases Section](https://github.com/syt0r/Kanji-Dojo/releases/latest).

### Version comparison

| Feature   | Fdroid | Google Play                  | Desktop |
|-----------|--------|------------------------------|---------|
| Analytics | -      | Firebase, enabled by default | -       |
| Other     | -      | Play services in app review  | -       |

### Contributing
- Pull-Requests are welcome!
- Before making PR create and discuss the issue 
- Use proper code formatting

### Project details

Used libraries: `Compose Multiplatform`, `Kotlin Coroutines`, `Koin`, `SqlDelight` 

Most parts of the app are implemented in the `core` multiplatform module. The `app` module only contains some specific parts for the Fdroid and Google Play versions (flavors)

Under the main package path there are 2 different packages: 
* `core` - contains app's general logic for features like repositories, databases, app state managers and handlers
* `presentation` - contains UI, navigation logic, screen specific logic and use cases

Typically a feature under `core` package is placed in a separate package and consist of an interface and implementation. 

The `presentation` package contains common UI and screen infrastructure. A single screen contains:
* `<ScreenName>Contract` - an interface with screen's view model and screen state data
* `<ScreenName>ViewModel` - handles screen's logic
* `<ScreenName><Action>UseCase` - components that handle some specific action, invoked by view model
* `<ScreenName>Screen` composable that handles navigation and communication between UI and view model
* `<ScreenName>UI` - composable function that represents UI, it depends only on data so it can be covered by previews
* `<ScreenName>Module` - a koin module that defines how to provide all screen specific components. 
</br>**Note:** View models should be additionally registered in `androidViewModelModule` since on android they're additionally wrapped by a platform specific component
* (Optional) `Content` interface for screens that can be replaced in flavors, like `SettingsScreenContract.Content`

There is a single Koin's `module` for all features under `core` package, but there are also separate modules for a platform (`platformComponentsModule`) and flavor (`flavorModule`) specific features. All screens have their own koin modules

### Credits

* **KanjiVG**</br>
  Provides writing strokes, radicals information </br>
  License: Creative Commons Attribution-Share Alike 3.0</br>
  Link: https://kanjivg.tagaini.net/
* **Kanji Dic**</br>
  Provides characters info, such as meanings, readings and classifications </br>
  License: Creative Commons Attribution-Share Alike 3.0</br>
  Link: http://www.edrdg.org/wiki/index.php/KANJIDIC_Project
* **Tanos by Jonathan Waller**</br>
  Provides JLPT classification for kanji </br>
  License: Creative Commons BY</br>
  Link: http://www.tanos.co.uk/jlpt/
* **JMDict**</br>
  Japanese-Multilingual dictionary, provides expressions </br>
  License: Creative Commons Attribution-Share Alike 4.0</br>
  Link: https://www.edrdg.org/jmdict/j_jmdict.html
* **JmdictFurigana**</br>
  Open-source furigana resource to complement the EDICT/Jmdict and ENAMDICT/Jmnedict dictionary files </br>
  License: Creative Commons Attribution-Share Alike 4.0</br>
  Link: https://github.com/Doublevil/JmdictFurigana
* **Frequency list by Leeds university**</br>
  Words ranking by frequency of usage in internet </br>
  License: Creative Commons BY</br>
  Link: http://corpus.leeds.ac.uk/list.html

### License

> (c) 2022-2023 Yaroslav Shuliak
> 
> This is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
> 
> This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
> 
> You should have received a copy of the GNU General Public License along with this app. If not, see https://www.gnu.org/licenses/.
