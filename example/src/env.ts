
// Configure your BindID ClientID and Redirect URI in the BindID console at https://admin.bindid-sandbox.io/console/#/applications
import {  XmBindIdServerEnvironmentMode } from "../../src/transmit-bind-id-api";

export default {
    ClientID: "bid_demo_acme_android",
    RedirectURI: "bindid://mobile-app-example",
    BindIDEnvironmentMode: XmBindIdServerEnvironmentMode.Sandbox,

    getHostName: (envMode: XmBindIdServerEnvironmentMode): string => {
        switch(envMode) { 
            case XmBindIdServerEnvironmentMode.Sandbox: { 
               return  'signin.bindid-sandbox.io';
            } 
            case XmBindIdServerEnvironmentMode.Production: { 
                return 'signin.bindid.io'; 
            } 
            default: { 
               return 'signin.bindid-sandbox.io';
            } 
         } 
    }
}
