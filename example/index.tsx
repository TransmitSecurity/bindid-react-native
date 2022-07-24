import { Platform, AppRegistry } from 'react-native';
import App from './src/App';
import { app as appName } from './app.json';

if( Platform.OS === 'ios') {
    AppRegistry.registerComponent(appName.ios.name, () => App);
} else {
    AppRegistry.registerComponent(appName.android.name, () => App); 
}
