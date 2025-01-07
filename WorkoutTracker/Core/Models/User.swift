// File: WorkoutTracker/Core/Models/User.swift
import Foundation
import CloudKit

struct User: Codable, Identifiable {
    let id: UUID
    var email: String
    var preferences: UserPreferences
    var deviceSyncInfo: DeviceSyncInfo
    
    // TODO: Implement user preferences structure
    struct UserPreferences: Codable {
        // Add user preference fields
    }
    
    // TODO: Implement device sync information structure
    struct DeviceSyncInfo: Codable {
        // Add device sync fields
    }
}

// File: WorkoutTracker/Core/Models/Exercise.swift
import Foundation

struct Exercise: Codable, Identifiable {
    let id: UUID
    var name: String
    var imageURL: URL?
    var isCustom: Bool
    var equipment: [Equipment]
    
    // TODO: Implement equipment enum
    enum Equipment: String, Codable {
        // Add equipment types
    }
}

// File: WorkoutTracker/Core/Models/WorkoutTemplate.swift
import Foundation

struct WorkoutTemplate: Codable, Identifiable {
    let id: UUID
    var name: String
    var exercises: [TemplateExercise]
    var defaultScheme: SetScheme
    
    // TODO: Implement template exercise structure
    struct TemplateExercise: Codable, Identifiable {
        let id: UUID
        var exercise: Exercise
        var order: Int
        var setScheme: SetScheme
    }
    
    // TODO: Implement set scheme structure
    struct SetScheme: Codable {
        // Add set scheme fields
    }
}

// File: WorkoutTracker/Core/Models/WorkoutLog.swift
import Foundation

struct WorkoutLog: Codable, Identifiable {
    let id: UUID
    let date: Date
    var templateId: UUID?
    var exercises: [LoggedExercise]
    var notes: String?
    
    // TODO: Implement logged exercise structure
    struct LoggedExercise: Codable, Identifiable {
        let id: UUID
        var exercise: Exercise
        var sets: [ExerciseSet]
    }
    
    // TODO: Implement exercise set structure
    struct ExerciseSet: Codable {
        // Add set tracking fields
    }
}
