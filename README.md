# react-native-mjpeg-player

* Support only for android

## Getting started

`$ npm install react-native-mjpeg-player --save`

### Mostly automatic installation

`$ react-native link react-native-mjpeg-player`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`

- Add `import com.segware.RNMjpegPlayerPackage;` to the imports at the top of the file
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
import React from 'react';
import MjpegPlayer from 'react-native-mjpeg-player';

class MjpegPlayerComponent extends React.Component {
  render() {
    const { url, width, height } = this.props;

    return (
      <MjpegPlayer
        ref={ref => {
          this.mjpegPlayer = ref;
        }}
        style={{ width: `100%`, height: `100%` }}
        settings={{
          url,
          width,
          height
        }}
      />
    );
  }
}

export default MjpegPlayerComponent;
```

## Props

| Prop			| Description   |   Type  | Default|
| ------------- | ------------- |-------- |--------|
| **url** | mjpeg url stream  |  string | **required**
| **width**  | frame width  | number | **required**
| **height** | frame height | number | **required**
