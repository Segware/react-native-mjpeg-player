
# react-native-mjpeg-player

## Getting started

`$ npm install react-native-mjpeg-player --save`

### Mostly automatic installation

`$ react-native link react-native-mjpeg-player`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-mjpeg-player` and add `RNMjpegPlayer.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMjpegPlayer.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.mathias.RNMjpegPlayerPackage;` to the imports at the top of the file
  - Add `new RNMjpegPlayerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-mjpeg-player'
  	project(':react-native-mjpeg-player').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-mjpeg-player/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-mjpeg-player')
  	```


## Usage
```javascript
import RNMjpegPlayer from 'react-native-mjpeg-player';

// TODO: What to do with the module?
RNMjpegPlayer;
```
  