import * as React from 'react';
import { ActivityIndicator, StyleSheet, View, Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import XmBindIdSdk from 'bindid-react-native';
import env from './env';
import { AuthenticateScreen } from './AuthenticateScreen';
import { StepUpScreen } from './StepUpScreen';
import { AuthenticatedUserScreen } from './AuthenticatedUserScreen';

import type {  XmBindIdServerEnvironment, XmBindIdConfig } from "../../src/transmit-bind-id-api";

const Stack = createNativeStackNavigator();

interface Props { }
interface State {
  isLoading: boolean;
  isSdkError: boolean;
}

export default class App extends React.Component<Props, State> {

  constructor(props: Props) {
    super(props);
    this.state = {
      isLoading: false,
      isSdkError: false
    }
  }

  componentDidMount() {
    this.setupBindIDSDK();
  }

  render() {
    if (this.state.isLoading) {
      return this.renderLoading();
    }

    if (this.state.isSdkError) {
      return this.renderError();
    }

    return (
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen name="AuthenticateScreen" component={AuthenticateScreen} options={{ title: 'BindID Example' }} />
          <Stack.Screen name="StepUpScreen" component={StepUpScreen} options={{ title: "Step Up", headerBackTitleVisible: false }}></Stack.Screen>
          <Stack.Screen name="AuthenticatedUserScreen" component={AuthenticatedUserScreen} options={{ title: "Authenticated", headerBackTitleVisible: false }}></Stack.Screen>
        </Stack.Navigator>
      </NavigationContainer>
    );
  }

  private setupBindIDSDK = async (): Promise<void> => {
    // Initialize the BindID SDK Wrapper with environment mode (sandbox/production) and the ClientID
    const serverEnvironment: XmBindIdServerEnvironment = {
      environmentMode: env.BindIDEnvironmentMode,
      environmentUrl: ""
    };

    const config: XmBindIdConfig = {
      clientId:  env.ClientID,
      serverEnvironment: serverEnvironment
    };

    XmBindIdSdk.initialize(config)
    .then((success: boolean) => {
        console.log(`BindID initialize Completed: ${success}`);
        this.setState({ isLoading: false, isSdkError: !success });
    }).catch((error: XmBindIdError) => {
        console.log(`BindID initialized Failed: ${JSON.stringify(error)}`);
    });
  }

  // Render UI

  private renderLoading = (): React.ReactElement => {
    return (
      <View style={styles.container}>
        <ActivityIndicator size="large" />
      </View>
    )
  }

  private renderError = (): React.ReactElement => {
    return (
      <View style={styles.container}>
        <Text>Error initializing the BindID SDK</Text>
      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
