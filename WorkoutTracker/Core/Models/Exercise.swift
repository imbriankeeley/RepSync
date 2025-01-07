import Foundation

/// Represents a single exercise that can be performed during a workout
struct Exercise: Codable, Identifiable, Hashable {
    // MARK: - Properties
    
    /// Unique identifier for the exercise
    let id: UUID
    
    /// Name of the exercise
    var name: String
    
    /// Optional URL for the exercise demonstration image
    var imageURL: URL?
    
    /// Whether this is a custom user-created exercise
    var isCustom: Bool
    
    /// List of required equipment for this exercise
    var equipment: Set<Equipment>
    
    /// Optional description of the exercise
    var exerciseDescription: String?
    
    /// Primary muscle groups targeted by this exercise
    var primaryMuscles: Set<MuscleGroup>
    
    /// Secondary muscle groups engaged during this exercise
    var secondaryMuscles: Set<MuscleGroup>
    
    // MARK: - Initialization
    
    init(
        id: UUID = UUID(),
        name: String,
        imageURL: URL? = nil,
        isCustom: Bool = false,
        equipment: Set<Equipment> = [],
        exerciseDescription: String? = nil,
        primaryMuscles: Set<MuscleGroup> = [],
        secondaryMuscles: Set<MuscleGroup> = []
    ) {
        self.id = id
        self.name = name
        self.imageURL = imageURL
        self.isCustom = isCustom
        self.equipment = equipment
        self.exerciseDescription = exerciseDescription
        self.primaryMuscles = primaryMuscles
        self.secondaryMuscles = secondaryMuscles
    }
}

// MARK: - Equipment Types

/// Equipment required for exercises
enum Equipment: String, Codable, CaseIterable {
    case barbell
    case dumbbell
    case kettlebell
    case resistanceBand
    case cable
    case machine
    case smithMachine
    case bodyweight
    case pullupBar
    case bench
    case foam
    case other
    
    var displayName: String {
        switch self {
        case .barbell: return "Barbell"
        case .dumbbell: return "Dumbbell"
        case .kettlebell: return "Kettlebell"
        case .resistanceBand: return "Resistance Band"
        case .cable: return "Cable Machine"
        case .machine: return "Machine"
        case .smithMachine: return "Smith Machine"
        case .bodyweight: return "Bodyweight"
        case .pullupBar: return "Pull-up Bar"
        case .bench: return "Bench"
        case .foam: return "Foam Roller"
        case .other: return "Other"
        }
    }
}

// MARK: - Muscle Groups

/// Major muscle groups that can be targeted by exercises
enum MuscleGroup: String, Codable, CaseIterable {
    case chest
    case back
    case shoulders
    case biceps
    case triceps
    case forearms
    case quadriceps
    case hamstrings
    case calves
    case glutes
    case abdominals
    case obliques
    case traps
    case lowerBack
    
    var displayName: String {
        switch self {
        case .chest: return "Chest"
        case .back: return "Back"
        case .shoulders: return "Shoulders"
        case .biceps: return "Biceps"
        case .triceps: return "Triceps"
        case .forearms: return "Forearms"
        case .quadriceps: return "Quadriceps"
        case .hamstrings: return "Hamstrings"
        case .calves: return "Calves"
        case .glutes: return "Glutes"
        case .abdominals: return "Abdominals"
        case .obliques: return "Obliques"
        case .traps: return "Trapezius"
        case .lowerBack: return "Lower Back"
        }
    }
}

// MARK: - Exercise Constants

enum ExerciseConstants {
    static let maxNameLength = 50
    static let maxDescriptionLength = 500
    
    static let defaultExercises: [Exercise] = [
        Exercise(
            name: "Barbell Bench Press",
            equipment: [.barbell, .bench],
            exerciseDescription: "Classic compound movement for chest development",
            primaryMuscles: [.chest],
            secondaryMuscles: [.shoulders, .triceps]
        ),
        Exercise(
            name: "Pull-up",
            equipment: [.pullupBar, .bodyweight],
            exerciseDescription: "Fundamental back exercise",
            primaryMuscles: [.back],
            secondaryMuscles: [.biceps, .forearms]
        ),
        // Add more default exercises as needed
    ]
}

// MARK: - Validation

extension Exercise {
    /// Validates the exercise properties
    func validate() throws {
        // Validate name
        guard !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw ExerciseError.invalidName("Exercise name cannot be empty")
        }
        
        guard name.count <= ExerciseConstants.maxNameLength else {
            throw ExerciseError.invalidName("Exercise name cannot exceed \(ExerciseConstants.maxNameLength) characters")
        }
        
        // Validate description length if present
        if let description = exerciseDescription {
            guard description.count <= ExerciseConstants.maxDescriptionLength else {
                throw ExerciseError.invalidDescription("Exercise description cannot exceed \(ExerciseConstants.maxDescriptionLength) characters")
            }
        }
        
        // Validate muscle groups
        guard !primaryMuscles.isEmpty else {
            throw ExerciseError.invalidMuscleGroups("At least one primary muscle group must be specified")
        }
        
        // Ensure no overlap between primary and secondary muscles
        guard primaryMuscles.intersection(secondaryMuscles).isEmpty else {
            throw ExerciseError.invalidMuscleGroups("Primary and secondary muscle groups cannot overlap")
        }
    }
}

// MARK: - Error Types

enum ExerciseError: LocalizedError {
    case invalidName(String)
    case invalidDescription(String)
    case invalidMuscleGroups(String)
    case invalidEquipment(String)
    
    var errorDescription: String? {
        switch self {
        case .invalidName(let message): return message
        case .invalidDescription(let message): return message
        case .invalidMuscleGroups(let message): return message
        case .invalidEquipment(let message): return message
        }
    }
}

// MARK: - Equatable & Hashable

extension Exercise {
    static func == (lhs: Exercise, rhs: Exercise) -> Bool {
        lhs.id == rhs.id
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}

#if DEBUG
// MARK: - Preview Helpers

extension Exercise {
    static var preview: Exercise {
        Exercise(
            name: "Barbell Squat",
            equipment: [.barbell],
            exerciseDescription: "Compound lower body movement",
            primaryMuscles: [.quadriceps, .glutes],
            secondaryMuscles: [.hamstrings, .calves, .lowerBack]
        )
    }
    
    static var previewExercises: [Exercise] = [
        preview,
        Exercise(
            name: "Deadlift",
            equipment: [.barbell],
            exerciseDescription: "Full body compound movement",
            primaryMuscles: [.back, .glutes],
            secondaryMuscles: [.hamstrings, .traps, .forearms]
        ),
        Exercise(
            name: "Push-up",
            equipment: [.bodyweight],
            exerciseDescription: "Basic upper body exercise",
            primaryMuscles: [.chest],
            secondaryMuscles: [.shoulders, .triceps]
        )
    ]
}
