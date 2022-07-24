//
//  JWTParser.swift
//  BindID Example
//
//  Created by Transmit Security on 07/10/2021.
//

import Foundation

class JWTParser {
    
    public static let shared = JWTParser()
    private let kNoDateString = "Never"
    private let kNoStringString = "Not Set"
    
    private init() { } // Prevent creating new instances of this singleton
    
    public enum IDTokenKeys: String {
        case email = "email"
        case phoneNumber = "phone_number"
        case bindIDNetworkInfo = "bindid_network_info"
        case bindIDInfo = "bindid_info"
        case sub = "sub"
        case bindIDAlias = "bindid_alias"
        case userRegistrationTime = "user_registration_time"
        case firstLogin = "capp_first_login"
        case firstConfirmedLogin = "capp_first_confirmed_login"
        case cappLastLogin = "capp_last_login"
        case userLastSeen = "user_last_seen"
        case confirmedCappCount = "confirmed_capp_count"
        case lastLoginFromAuthenticatedDevice = "capp_last_login_from_authenticating_device"
        case bindIDAppBoundCred = "ts.bindid.app_bound_cred"
        case authenticatedDeviceLastSeen = "authenticating_device_last_seen"
        case deviceCount = "device_count"
    }
    
    public func parseIDTokenData(_ tokenData: [String: Any]) -> [String: String] {
        var passport: [String: String] = [:]
        
        let networkInfo = tokenData[IDTokenKeys.bindIDNetworkInfo.rawValue] as? [String: Any]
        let bindIDInfo = tokenData[IDTokenKeys.bindIDInfo.rawValue] as? [String: Any]

        passport["01. User ID"] = tokenData[IDTokenKeys.sub.rawValue] as? String
        passport["02. User Alias"] = tokenData[IDTokenKeys.bindIDAlias.rawValue] as? String ?? kNoStringString
        passport["03. Email"] = tokenData[IDTokenKeys.email.rawValue] as? String ?? kNoStringString
        passport["04. Phone Number"] = tokenData[IDTokenKeys.phoneNumber.rawValue] as? String ?? kNoStringString

        passport["05. User Registered On"] = networkInfo?[IDTokenKeys.userRegistrationTime.rawValue] as? String
        passport["06. User First Seen"] = formatTimestamp(bindIDInfo?[IDTokenKeys.firstLogin.rawValue] as? Double)
        passport["07. User First Confirmed"] = formatTimestamp(bindIDInfo?[IDTokenKeys.firstConfirmedLogin.rawValue] as? Double)
        passport["08. User Last Seen"] = formatTimestamp(bindIDInfo?[IDTokenKeys.cappLastLogin.rawValue] as? Double)
        passport["09. User Last Seen by Network"] = networkInfo?[IDTokenKeys.userLastSeen.rawValue] as? String
        passport["10. Total Providers that Confirmed User"] = "\(networkInfo?[IDTokenKeys.confirmedCappCount.rawValue] as? Int ?? 0)"
        passport["11. Authenticating Device Registered"] = formatTimestamp(bindIDInfo?[IDTokenKeys.lastLoginFromAuthenticatedDevice.rawValue] as? Double)
        
        let acr = tokenData["acr"] as? [String] ?? []
        passport["12. Authenticating Device Confirmed"] = acr.contains(IDTokenKeys.bindIDAppBoundCred.rawValue) ? "Yes" : "No"
        
        passport["13. Authenticating Device Last Seen"] = formatTimestamp(bindIDInfo?[IDTokenKeys.lastLoginFromAuthenticatedDevice.rawValue] as? Double)
        
        passport["14. Authenticating Device Last Seen by Network"] = networkInfo?[IDTokenKeys.authenticatedDeviceLastSeen.rawValue] as? String ?? kNoStringString
        passport["15. Total Known Devices"] = "\(networkInfo?[IDTokenKeys.deviceCount.rawValue] as? Int ?? 0)"
    
        return passport
    }
        
    private func formatTimestamp(_ timestamp: Double?) -> String {
        guard let date = timestamp?.date else { return kNoDateString }
        let dateformat = DateFormatter()
        dateformat.dateFormat = "EEEE, MMM d, yyyy"
        return dateformat.string(from: date)
    }
}
