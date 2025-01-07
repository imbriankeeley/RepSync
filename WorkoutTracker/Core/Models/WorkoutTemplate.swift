import Foundation
import CoreData

/// Represents a template for workouts that can be reused
struct WorkoutTemplate: Codable, Identifiable {
    // MARK: - Properties
    
    /// Unique identifier for the template
    let id: UUID
    
    /// Name of the template
    var name: String
    
    /// List of exercises in this template with their configurations
    var exercises: [TemplateExercise]
    
    /// Default set scheme to apply to new exercises
    var defaultScheme: SetScheme
    
    /// Last time this template was used
    var lastUsed: Date?
    
    /// Creation date of the template
    let createdAt: Date
    
    // MARK: - Nested Types
    
    /// Represents an exercise within a template with its specific configuration
    struct TemplateExercise: Codable, Identifiable {
        /// Unique identifier for the template exercise
        let id: UUID
        
        /// The exercise to perform
        var exercise: Exercise
        
        /// Order in the template
        var order: Int
        
        /// Set scheme for this specific exercise
        var setScheme: SetScheme
        
        init(
            id: UUID = UUID(),
            exercise: Exercise,
            order: Int,
            setScheme: SetScheme
        ) {
            self.id = id
            self.exercise = exercise
            self.order = order
            self.setScheme = setScheme
        }
    }
    
    /// Defines the set and repetition scheme for exercises
    struct SetScheme: Codable {
        /// Number of sets to perform
        var sets: Int
        
        /// Default number of repetitions per set
        var defaultReps: Int
        
        /// Whether to track weight for this exercise
        var trackWeight: Bool
        
        /// Target rest time between sets (in seconds)
        var restTime: TimeInterval?
        
        init(
            sets: Int = 3,
            defaultReps: Int = 10,
            trackWeight: Bool = true,
            restTime: TimeInterval? = nil
        ) {
            self.sets = sets
            self.defaultReps = defaultReps
            self.trackWeight = trackWeight
            self.restTime = restTime
        }
    }
    
    // MARK: - Initialization
    
    init(
        id: UUID = UUID(),
        name: String,
        exercises: [TemplateExercise] = [],
        defaultScheme: SetScheme = SetScheme(),
        lastUsed: Date? = nil,
        createdAt: Date = Date()
    ) {
        self.id = id
        self.name = name
        self.exercises = exercises
        self.defaultScheme = defaultScheme
        self.lastUsed = lastUsed
        self.createdAt = createdAt
    }
}

// MARK: - Validation

extension WorkoutTemplate {
    /// Validates the template properties
    func validate() throws {
        // Validate name
        guard !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            throw WorkoutTemplateError.invalidName("Template name cannot be empty")
        }
        
        // Validate exercises
        guard !exercises.isEmpty else {
            throw WorkoutTemplateError.invalidExercises("Template must contain at least one exercise")
        }
        
        // Validate exercise order
        let orderSet = Set(exercises.map { $0.order })
        guard orderSet.count == exercises.count else {
            throw WorkoutTemplateError.invalidExerciseOrder("Exercise order must be unique")
        }
        
        // Validate default scheme
        try validateSetScheme(defaultScheme)
        
        // Validate each exercise's set scheme
        try exercises.forEach { exercise in
            try validateSetScheme(exercise.setScheme)
        }
    }
    
    /// Validates a set scheme configuration
    private func validateSetScheme(_ scheme: SetScheme) throws {
        guard scheme.sets > 0 else {
            throw WorkoutTemplateError.invalidSetScheme("Number of sets must be greater than 0")
        }
        
        guard scheme.defaultReps > 0 else {
            throw WorkoutTemplateError.invalidSetScheme("Default reps must be greater than 0")
        }
        
        if let restTime = scheme.restTime {
            guard restTime >= 0 else {
                throw WorkoutTemplateError.invalidSetScheme("Rest time cannot be negative")
            }
        }
    }
}

// MARK: - Error Types

enum WorkoutTemplateError: LocalizedError {
    case invalidName(String)
    case invalidExercises(String)
    case invalidExerciseOrder(String)
    case invalidSetScheme(String)
    
    var errorDescription: String? {
        switch self {
        case .invalidName(let message): return message
        case .invalidExercises(let message): return message
        case .invalidExerciseOrder(let message): return message
        case .invalidSetScheme(let message): return message
        }
    }
}

// MARK: - Convenience Methods

extension WorkoutTemplate {
    /// Creates a new workout log from this template
    func createWorkout() -> WorkoutLog {
        let loggedExercises = exercises.map { templateExercise in
            WorkoutLog.LoggedExercise(
                id: UUID(),
                exercise: templateExercise.exercise,
                sets: (0..<templateExercise.setScheme.sets).map { setIndex in
                    WorkoutLog.ExerciseSet(
                        number: setIndex + 1,
                        weight: nil,
                        reps: templateExercise.setScheme.defaultReps,
                        completed: false
                    )
                }
            )
        }
        
        return WorkoutLog(
            id: UUID(),
            date: Date(),
            templateId: self.id,
            exercises: loggedExercises
        )
    }
    
    /// Adds an exercise to the template
    mutating func addExercise(_ exercise: Exercise) {
        let nextOrder = (exercises.map { $0.order }.max() ?? -1) + 1
        let templateExercise = TemplateExercise(
            exercise: exercise,
            order: nextOrder,
            setScheme: defaultScheme
        )
        exercises.append(templateExercise)
    }
    
    /// Removes an exercise from the template
    mutating func removeExercise(at index: Int) {
        guard exercises.indices.contains(index) else { return }
        exercises.remove(at: index)
        reorderExercises()
    }
    
    /// Reorders the exercises to ensure sequential ordering
    private mutating func reorderExercises() {
        exercises = exercises.enumerated().map { index, var exercise in
            exercise.order = index
            return exercise
        }
    }
}

#if DEBUG
// MARK: - Preview Helpers

extension WorkoutTemplate {
    static var preview: WorkoutTemplate {
        WorkoutTemplate(
            name: "Full Body Workout",
            exercises: [
                TemplateExercise(
                    exercise: Exercise(
                        name: "Barbell Squat",
                        equipment: [.barbell],
                        primaryMuscles: [.quadriceps, .glutes]
                    ),
                    order: 0,
                    setScheme: SetScheme(sets: 3, defaultReps: 8)
                ),
                TemplateExercise(
                    exercise: Exercise(
                        name: "Bench Press",
                        equipment: [.barbell, .bench],
                        primaryMuscles: [.chest]
                    ),
                    order: 1,
                    setScheme: SetScheme(sets: 3, defaultReps: 10)
                )
            ]
        )
    }
}
#endif
