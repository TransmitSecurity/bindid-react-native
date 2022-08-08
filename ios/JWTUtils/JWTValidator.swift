//
//  JWTValidator.swift
//  BindID Example
//
//  Created by Transmit Security on 12/07/2021.
//

import Foundation

class JWTValidator {
    
    public static let shared = JWTValidator()
    public typealias JWTValidatorError = String
    
    private init() { } // Prevent creating new instances of this singleton
    
    /**
     1. Parse the token to Headers, Payload and Signature
     2. Convert the headers, payload and signature to get their CFData representations
     3. Fetch the public key from the BindID jwks endpoint
     4. Parse our certificate to get its public key as a SecKey representation
     5. Verify the signature
     6. Decode JWT payload to verify the expiration date and the token issuer
     */
    
    public func validate(_ idToken: String, hostName: String, callback: @escaping (Bool, JWTValidatorError?) -> Void) {
        
        // 1. Parse the token to Headers, Payload and Signature
        let tokenParts = idToken.split(separator: ".")
        guard tokenParts.count == 3 else {
            callback(false, "Error validating JWT token components formation")
            return
        }
        
        let tokenHeadersAndPayload = "\(tokenParts[0]).\(tokenParts[1])"
        let tokenSignature = String(tokenParts[2])
        
        // 2. Convert the headers, payload and signature to get their CFData representations
        let tokenHeadersAndPayloadData = tokenHeadersAndPayload.data(using: .ascii)! as CFData
        let tokenSignatureData = Data(base64Encoded: tokenSignature.base64FromBase64Url)! as CFData
        
        // 3. Fetch the BindID Public key
        fetchBindIDPublicKey(hostName: hostName) { certificateBase64 in
            guard let certificateBase64 = certificateBase64 else {
                callback(false, "Error fetching BindID Public Key")
                return
            }

            guard let certificateData = Data(base64Encoded: certificateBase64), let certificate = SecCertificateCreateWithData(nil, certificateData as CFData) else {
                callback(false, "Error converting certificate to CFData")
                return
            }

            // 4. Parse our certificate to get its public key as a SecKey representation
            var publicKey: SecKey?
            if #available(iOS 12.0, *) {
                 publicKey = SecCertificateCopyKey(certificate)
            } else {
                // Fallback on earlier versions
                callback(false, "SecCertificateCopyKey isn't support on iOS version below 12.0")
                return
            }

            // 5. Verify the signature
            let result = SecKeyVerifySignature(
                publicKey!,
                .rsaSignatureMessagePKCS1v15SHA256,
                tokenHeadersAndPayloadData, tokenSignatureData,
                nil
            )
            
            // 6. Decode JWT payload to verify the expiration date and the token issuer
            guard let decodedPayload = try? JWTDecoder.shared.decodePayload(idToken) else {
                callback(false, "Error decoding BindID JWT payload")
                return
            }

            guard let iss = decodedPayload["iss"] as? String, // get issuer
                  let issuer = URL(string: iss),
                  let exp = decodedPayload["exp"] as? Double, // get expiration date
                  let expiration = exp.date,
                  issuer.host == hostName.deletingPrefix("https://"), // compare issuer with BindID host
                  expiration > Date() else { // confirm expiration date is later then now
                callback(false, "Error verifying token issuer and expiration date")
                return
            }
            
            callback(result, nil)
        }
    }
    
    private func fetchBindIDPublicKey(hostName: String, callback: @escaping (String?) -> Void) {
        let urlPrefix = "https://"
        let validHostName = (hostName.hasPrefix(urlPrefix)) ? hostName : "\(urlPrefix)\(hostName)"
        let publicKeyEndPoint = "\(validHostName)/jwks"
        guard let url = URL(string: publicKeyEndPoint) else { return callback(nil) }
        
        func callbackInMainThread(_ publicKey: String?) {
            DispatchQueue.main.async {
                callback(publicKey)
            }
        }
        
        // Fetch the public key from the BindID jwks endpoint
        let task = URLSession.shared.dataTask(with: url) { (data, response, error) in
            guard let data = data else { return callbackInMainThread(nil) }
            // Serialize the response and convert it to an array of key objects
            let json = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any]
            guard let keys = json?["keys"] as? [[String: Any]] else { return callbackInMainThread(nil) }
            
            // Find the key that contains the "sig" value in the "use" key. Return the publicKey in it
            if let use = keys.first(where: { ($0["use"] as? String) == "sig" }) {
                if let x5c = use["x5c"] as? [String], let publicKey = x5c.first {
                    callbackInMainThread(publicKey)
                    return
                }
            }
            
            callbackInMainThread(nil) // Public key was not found in the publicKeyEndPoint response
        }

        task.resume()
    }
}

extension String {
    var base64FromBase64Url: String {
        var base64 = self
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        
        base64 += String(repeating: "=", count: base64.count % 4)
        
        return base64
    }
    
    func deletingPrefix(_ prefix: String) -> String {
        guard self.hasPrefix(prefix) else { return self }
        return String(self.dropFirst(prefix.count))
    }
}
