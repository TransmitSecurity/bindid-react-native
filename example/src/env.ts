
// Configure your BindID ClientID and Redirect URI in the BindID console at https://admin.bindid-sandbox.io/console/#/applications
import {  XmBindIdServerEnvironmentMode } from "../../src/transmit-bind-id-api";

export default {
    ClientID: "bid_demo_acme_android",
    RedirectURI: "bindid://mobile-app-example",
    BindIDEnvironmentMode: XmBindIdServerEnvironmentMode.Sandbox,

    getHostName: (envMode: XmBindIdServerEnvironmentMode): string => {
        return (envMode == XmBindIdServerEnvironmentMode.Sandbox) ? 'signin.bindid-sandbox.io' : 'signin.bindid.io';
    }
}
