import * as React from 'react';
import type { NavigationStackProp } from 'react-navigation-stack';
import { SafeAreaView, StyleSheet, View, Text, FlatList, ActivityIndicator } from 'react-native';
import XmBindIdSdk from 'bindid-react-native';
import style from './style';

import env from './env';

import type { XmBindIdExchangeTokenResponse } from "../../src/transmit-bind-id-api";


type AuthenticatedUserScreenProps = {
    navigation: NavigationStackProp<{}>;
    route: any;
};


interface State {
    isLoading: boolean;
    hasError: boolean;
    errorMessage: string;
    sortedPassportKeys: string[];
    passportData: { [key: string]: any };
}

interface PassportItem {
    title: string;
    value: any;
}

export class AuthenticatedUserScreen extends React.Component<AuthenticatedUserScreenProps, State> {

    constructor(props: AuthenticatedUserScreenProps) {
        super(props);
        this.state = {
            isLoading: true,
            hasError: false,
            errorMessage: "",
            sortedPassportKeys: [],
            passportData: {}
        };
    }

    componentDidMount() {
        this.parseIDToken();
    }

    render() {
        if (this.state.isLoading) {
            return this.renderLoading();
        }

        if (this.state.hasError) {
            return this.renderError();
        }

        const items: PassportItem[] = this.state.sortedPassportKeys.map((key: string) => {
            return { title: key, value: this.state.passportData[key] };
        });

        return (
            <SafeAreaView style={styles.container}>
                <FlatList
                    data={items}
                    renderItem={({ item }) => this.renderPassportRow(item)}
                    keyExtractor={({ title }) => { return title }}
                />
            </SafeAreaView>
        );
    }

    private parseIDToken = async (): Promise<void> => {

        XmBindIdSdk.exchangeToken(this.props.route.params.response)
            .then((response: XmBindIdExchangeTokenResponse) => {
                console.log(`BindID Exchange Token Completed: ${JSON.stringify(response)}`);
                this.handleExchangeResponseResponse(response);
            }).catch((error: XmBindIdError) => {
                console.log(`BindID AuthenticatExchange Token Falied: ${error.message}`);
               this.handleError(error); 
            });
    }

    private handleExchangeResponseResponse = async (response: XmBindIdExchangeTokenResponse): Promise<void> => {
        
        console.log(`idToken: ${response.idToken}`);

        if (!response.idToken) {
            console.log("Invalid response returned from authentication");
             return this.handleMessageError(`Invalid response returned from authentication: ${response}` );
         }
 
         const idToken = response.idToken;
         if (!idToken) {
             console.log("Invalid ID Token" + idToken);
             return this.handleMessageError("Invalid ID Token" + idToken);
         }

         // Once we receive the ID Token response we should verify the validity of the token
         const isValid = await XmBindIdSdk.validate(idToken, env.getHostName(env.BindIDEnvironmentMode));
         if (!isValid) {
            console.log("Invalid ID Token");
             return this.handleMessageError("Invalid ID Token");
         }
         
        //Parse the ID Token to components and present them on a Flat List
        const passportData = await XmBindIdSdk.parse(idToken);
        if (!passportData) {
            console.log("Error parsing ID Token");
            return this.handleMessageError("Error parsing ID Token");
        }

        const sortedPassportKeys = Object.keys(passportData).sort((a, b) => { return a.localeCompare(b) });

        console.log(`Passport ${JSON.stringify(sortedPassportKeys)}`);

        this.setState({
            isLoading: false,
            sortedPassportKeys,
            passportData
        });
        
    }

    // Render UI

    private renderLoading = (): React.ReactElement => {
        return (
            <View style={styles.container}>
                <ActivityIndicator size="large" />
            </View>
        );
    }

    private renderError = (): React.ReactElement => {
        return (
            <View style={styles.container}>
                <Text>{this.state.errorMessage}</Text>
            </View>
        )
    }

    private renderPassportRow = (item: PassportItem): React.ReactElement => {
        return (
            <View>
                <View style={styles.listItem}>
                    <Text style={styles.itemTitle}>{item.title.substring(4, item.title.length)}</Text>
                    <Text style={styles.itemValue}>{item.value}</Text>
                </View>
                <View style={styles.separator}></View>
            </View>
        )
    }

    // Handle Errors
    private handleError(error: XmBindIdError): void {
        this.setState({
            hasError: true,
            errorMessage: error.message,
            isLoading: false
        });
    }

    private handleMessageError(error: string): void {
        this.setState({
            hasError: true,
            errorMessage: error,
            isLoading: false
        });
    }
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: style.white,
        flex: 1
    },
    listItem: {
        flex: 1,
        padding: 12
    },
    itemTitle: {
        fontSize: 16,
        fontWeight: "600",
        color: "#000000"
    },
    itemValue: {
        fontSize: 16,
        fontWeight: "400",
        color: "#000000",
        marginTop: 2
    },
    separator: {
        backgroundColor: "#f5f5f5",
        height: 0.5,
        width: "98%",
        marginLeft: "2%"
    }
});