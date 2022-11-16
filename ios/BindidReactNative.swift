import Foundation
import XmBindIdSDK

@objc(BindidReactNative)
class BindidReactNative: NSObject {
    
    // MARK:- Public Module API
    
    @objc(initialize:withResolver:withRejecter:)
    func initialize(config: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        var dictSwift = config.swiftDictionary
        
        if let index = dictSwift[keyPath: "serverEnvironment.environmentMode"] as? Int {
            dictSwift[keyPath: "serverEnvironment.environmentMode"] =  XmBindIdServerEnvironmentMode.allCases[index].rawValue
            if let environmentUrl = dictSwift[keyPath: "serverEnvironment.environmentUrl"] as? String, environmentUrl == "" {
                dictSwift[keyPath: "serverEnvironment.environmentUrl"] =  XmBindIdServerEnvironmentMode.allCases[index].rawValue
            }
        }
        
        if let index = dictSwift[keyPath: "serverEnvironment.tokenExchangePlatformMode"] as? Int {
            dictSwift[keyPath: "serverEnvironment.tokenExchangePlatformMode"] = XmBindIdTokenExchangePlatformMode.allCases[index].rawValue
        }

        if let index = dictSwift[keyPath: "apiCompat"] as? Int {
            dictSwift[keyPath: "apiCompat"] = XmBindIdApiCompatibilityLevel.allCases[index].rawValue
        }
      
        DispatchQueue.main.async {
            if let data = TSBindIdUtils.jsonToObject(dict: dictSwift, ofType: XmBindIdConfig.self) {
                XmBindIdSdk.shared.initialize(config: data) { (_, error) in
                    if let err = error {
                        NSLog("%@", "Failed to initialize SDK: \(err.code!)")
                        reject(err.code.rawValue, err.message, nil)
                    } else {
                        NSLog("%@", "BindID SDK initialized successfully")
                        resolve(true)
                    }
                }
            } else {
                reject("Falied to parse data \(dictSwift.dictionaryAsJsonString() ?? "NULL")", "Falied to parse the configuration data", nil)
            }
        }
        
    }
    
    @objc(authenticate:withResolver:withRejecter:)
    func authenticate(bindIdRequestRequest: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        
        let dictSwift = authenticateDictionary(dict: bindIdRequestRequest)
        
        DispatchQueue.main.async {
            
            if let data = TSBindIdUtils.jsonToObject(dict: dictSwift, ofType: XmBindIdAuthenticationRequest.self) {
                
                XmBindIdSdk.shared.authenticate(bindIdRequestParams: data) { (response, error) in
                    if let err = error {
                        reject(err.code.rawValue, err.message, nil)
                    } else if let response = response {
                        resolve(response.dictionary)
                    }
                }
            } else {
                reject("Falied to parse data \(dictSwift.dictionaryAsJsonString() ?? "NULL")", "Falied to parse the configuration data", nil)
            }
            
            
        }
    }
    
    @objc(signTransaction:withResolver:withRejecter:)
    func signTransaction(bindIdTransactionRequest: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        let dictSwift = authenticateDictionary(dict: bindIdTransactionRequest)
        
        DispatchQueue.main.async {
            
            if let data = TSBindIdUtils.jsonToObject(dict: dictSwift, ofType: XmBindIdTransactionSigningRequest.self) {
                
                if let transactionSigningDataDict = dictSwift["transactionSigningData"] as? [String: Any] {
                    
                    if let displayDataDict = transactionSigningDataDict["displayData"] as? [String: Any] {
                        
                        var payee =  ""
                        var paymentMethod = ""
                        var paymentAmount = ""
                        
                        if let value = displayDataDict["payee"] as? String {
                            payee = value
                        }
                        if let value = displayDataDict["paymentMethod"] as? String {
                            paymentMethod = value
                        }
                        if let value = displayDataDict["paymentAmount"] as? String {
                            paymentAmount = value
                        }
                        
                        data.transactionSigningData = XmBindIdTransactionSigningData(displayData: XmBindIdTransactionSigningDisplayData(payee: payee, paymentAmount: paymentAmount, paymentMethod: paymentMethod))
                    }
                }
                
                XmBindIdSdk.shared.signTransaction(bindIdTransactionRequest: data) { (response, error) in
                    if let err = error {
                        reject(err.code.rawValue, err.message, nil)
                    } else if let response = response {
                        resolve(response.dictionary)
                    }
                }
            } else {
                reject("Falied to parse data \(dictSwift.dictionaryAsJsonString() ?? "NULL")", "Falied to parse the configuration data", nil)
            }
        }
    }
    
    
    @objc(authenticateBoundUser:withResolver:withRejecter:)
    func authenticateBoundUser(bindIdRequestRequest: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        
        
        let dictSwift = authenticateDictionary(dict: bindIdRequestRequest)
        
        DispatchQueue.main.async {
            
            if let data = TSBindIdUtils.jsonToObject(dict: dictSwift, ofType: XmBindIdBoundUserAuthenticationRequest.self) {
                
                XmBindIdSdk.shared.authenticateBoundUser(bindIdRequestParams: data) { (response, error) in
                    if let err = error {
                        reject(err.code.rawValue, err.message, nil)
                    } else if let response = response {
                        resolve(response.dictionary)
                    }
                }
            } else {
                reject("Falied to parse data \(dictSwift.dictionaryAsJsonString() ?? "NULL")", "Falied to parse the configuration data", nil)
            }
        }
    }
    
    @objc(exchangeToken:withResolver:withRejecter:)
    func exchangeToken(exchangeRequest: NSDictionary, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        /**
         Exchange the authentication response for the ID and access token using a PKCE token exchange
         */
        
        let dictSwift = exchangeRequest.swiftDictionary
        
        DispatchQueue.main.async {
            
            if let data = TSBindIdUtils.jsonToObject(dict: dictSwift, ofType: XmBindIdExchangeTokenRequest.self) {
                
                XmBindIdSdk.shared.exchangeToken(exchangeRequest: data) { (response, error) in
                    if let err = error {
                        reject(err.code.rawValue, err.message, nil)
                    } else if let tokenResponse = response {
                        resolve(tokenResponse.dictionary)
                    }
                    
                }
            } else {
                reject("Falied to parse data \(dictSwift.dictionaryAsJsonString() ?? "NULL")", "Falied to parse the configuration data", nil)
            }
        }
    }
    
    
    
    @objc(validate:withHostName:withResolver:withRejecter:)
    func validate(idToken: String, hostName: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        JWTValidator.shared.validate(idToken, hostName: hostName) { isValid, error in
            guard error == nil else {
                return reject("Error validating JWT token", error!, nil)
            }
            resolve(isValid)
        }
    }
    
    @objc(parse:withResolver:withRejecter:)
    func parse(idToken: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let decoded = try? JWTDecoder.shared.decodePayload(idToken) else {
            return reject("Error decoding JWT token", "Error decoding JWT token", nil)
        }
                
        resolve(decoded)
    }
    

    private func authenticateDictionary(dict: NSDictionary) -> Dictionary<String, Any> {
        
        var dictSwift = dict.swiftDictionary
        if let index = dictSwift[keyPath: "loginHint.type"] as? Int {
            dictSwift[keyPath: "loginHint.type"] =  XmBindIdLoginHintType.allCases[index].rawValue
        }
        if let index = dictSwift[keyPath: "boundTo.type"] as? Int {
            dictSwift[keyPath: "boundTo.type"] = XmBindIdBoundToType.allCases[index].rawValue
        }
        
        if let indices = dictSwift[keyPath: "scope"] as? [Int] {
            dictSwift[keyPath: "scope"] = indices.compactMap({
                XmBindIdScopeType.allCases[$0].rawValue
            })
        }
        
        if let indices = dictSwift[keyPath: "verifications"] as? [Int] {
            dictSwift[keyPath: "verifications"] = indices.compactMap({
                XmRequiredVerifications.allCases[$0].rawValue
            })
        }
        
        return dictSwift
    }
}
