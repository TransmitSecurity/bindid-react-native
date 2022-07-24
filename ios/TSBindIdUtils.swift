//
//  TSBindIdUtils.swift
//  BindidReactNative
//
//  Created by Omar Ayed on 03/07/2022.
//  Copyright © 2022 Facebook. All rights reserved.
//

import UIKit


class TSBindIdUtils {

    static func jsonToObject<T: Codable>(dict: Dictionary<String, Any>, ofType: T.Type) -> T?  {
                
        NSLog("%@", "Dictionaty to parse \(dict.dictionaryAsJsonString() ?? "")")
        
        let decoder = JSONDecoder()
        if let json = dict.dictionaryAsJsonString(), let data = json.data(using: .utf8), let model = try? decoder.decode(T.self, from: data) {
            NSLog("%@", "Success to parse the Object \(model.dictionary)")
            return model
        }
        
        NSLog("%@", "Failed to parse pbject \(ofType.self)")
        return nil
    }
    
    static func toJson<T: Codable>(obj: T) -> String? {
        if let jsonData = try? JSONEncoder().encode(obj) {
            let json = String(data: jsonData, encoding: String.Encoding.utf8)
            return json
        }
        
        return nil
       
    }
    
}

extension NSDictionary {
    
    var swiftDictionary: Dictionary<String, Any> {
        var swiftDictionary = Dictionary<String, Any>()

        for key : Any in self.allKeys {
            let stringKey = key as! String
            if let keyValue = self.value(forKey: stringKey){
                swiftDictionary[stringKey] = keyValue
            }
        }

        return swiftDictionary
    }
}

extension Dictionary {
    subscript(keyPath keyPath: String) -> Any? {
        get {
            guard let keyPath = Dictionary.keyPathKeys(forKeyPath: keyPath)
                else { return nil }
            return getValue(forKeyPath: keyPath)
        }
        set {
            guard let keyPath = Dictionary.keyPathKeys(forKeyPath: keyPath),
                let newValue = newValue else { return }
            self.setValue(newValue, forKeyPath: keyPath)
        }
    }

    static private func keyPathKeys(forKeyPath: String) -> [Key]? {
        let keys = forKeyPath.components(separatedBy: ".")
            .reversed().compactMap({ $0 as? Key })
        return keys.isEmpty ? nil : keys
    }

    // recursively (attempt to) access queried subdictionaries
    // (keyPath will never be empty here; the explicit unwrapping is safe)
    private func getValue(forKeyPath keyPath: [Key]) -> Any? {
        guard let value = self[keyPath.last!] else { return nil }
        return keyPath.count == 1 ? value : (value as? [Key: Any])
                .flatMap { $0.getValue(forKeyPath: Array(keyPath.dropLast())) }
    }

    // recursively (attempt to) access the queried subdictionaries to
    // finally replace the "inner value", given that the key path is valid
    private mutating func setValue(_ value: Any, forKeyPath keyPath: [Key]) {
        guard self[keyPath.last!] != nil else { return }
        if keyPath.count == 1 {
            (value as? Value).map { self[keyPath.last!] = $0 }
        }
        else if var subDict = self[keyPath.last!] as? [Key: Value] {
            subDict.setValue(value, forKeyPath: Array(keyPath.dropLast()))
            (subDict as? Value).map { self[keyPath.last!] = $0 }
        }
    }
}


extension Encodable {
    
    var dictionary: [String: Any]? {
        guard let data = try? JSONEncoder().encode(self) else { return nil }
        return (try? JSONSerialization.jsonObject(with: data, options: .allowFragments)).flatMap { $0 as? [String: Any] }
    }
    
    var dict : [String: Any]? {
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        guard let data = try? encoder.encode(self) else { return nil }
        guard let json = try? JSONSerialization.jsonObject(with: data, options: []) as? [String:Any] else { return nil }
        return json
    }
}



extension Dictionary {
    
    func dictionaryAsJsonString() -> String? {
        if let theJSONData = try? JSONSerialization.data(withJSONObject: self, options: []),
           let theJSONText = String(data: theJSONData, encoding: .utf8) {
            print("The JSON string = \n\(theJSONText)")
            return theJSONText
        }
        
        return nil
    }
    
    func toData() -> Data? {
        return try? JSONSerialization.data(withJSONObject: self)
    }
    
}
