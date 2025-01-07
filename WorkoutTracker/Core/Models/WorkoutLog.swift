import Foundation

/// Represents a completed or in-progress workout session
struct WorkoutLog: Codable, Identifiable {
    // MARK: - Properties
    
    /// Unique identifier for the workout
    let id: UUID
    
    /// Date and time when the workout was performed
    let date: Date
    
    /// Reference to the template used (if workout was created from template)
    var templateId: UUID?
    
    /// List of exercises performed during the workout
    var exercises: [LoggedExercise]
    
    /// Optional notes about the workout
    var notes: String?
    
    /// Duration of the workout (if completed)
    var duration: TimeInterval?
    
    /// Workout completion status
    var status: WorkoutStatus = .inProgress
    
    // MARK: - Nested Types
    
    /// Represents a single exercise performed during the workout
    struct LoggedExercise: Codable, Identifiable {
        /// Unique identifier for the logged exercise
        let id: UUID
        
        /// Reference to the exercise performed
        var exercise: Exercise
        
        /// Sets performed for this exercise
        var sets: [ExerciseSet]
    }
    
    /// Represents a single set of an exercise
    struct ExerciseSet: Codable {
        /// Set number within the exercise
        var number: Int
        
        /// Weight used (in kg)
        var weight: Double?
        
        /// Number of repetitions performed
        var reps: Int
        
        /// Whether the set was completed
        var completed: Bool
        
        /// Optional notes for the set
        var notes: String?
        
        /// Rest time after this set (in seconds)
        var restTime: TimeInterval?
    }
    
    /// Represents the status of a workout
    enum WorkoutStatus: String, Codable {
        case inProgress
        case completed
        case cancelled
    }
    
    // MARK: - Initialization
    
    init(
        id: UUID = UUID(),
        date: Date = Date(),
        templateId: UUID? = nil,
        exercises: [LoggedExercise] = [],
        notes: String? = nil,
        duration: TimeInterval? = nil,
        status: WorkoutStatus = .inProgress
    ) {
        self.id = id
        self.date = date
        self.templateId = templateId
        self.exercises = exercises
        self.notes = notes
        self.duration = duration
        self.status = status
    }
}

// MARK: - Validation

extension WorkoutLog {
    /// Validates the workout log properties
    func validate() throws {
        // Validate exercises
        guard !exercises.isEmpty else {
            throw WorkoutLogError.invalidExercises("Workout must contain at least one exercise")
        }
        
        // Validate sets
        for exercise in exercises {
            guard !exercise.sets.isEmpty else {
                throw WorkoutLogError.invalidSets("Exercise must contain at least one set")
            }
            
            // Validate set numbers are sequential
            let setNumbers = exercise.sets.map { $0.number }
            guard setNumbers == Array(1...setNumbers.count) else {
                throw WorkoutLogError.invalidSets("Set numbers must be sequential")
            }
            
            // Validate weights are non-negative
            if let invalidWeight = exercise.sets.first(where: { $0.weight ?? 0 < 0 }) {
                throw WorkoutLogError.invalidWeight("Weight cannot be negative: \(invalidWeight.weight ?? 0)")
            }
            
            // Validate reps are positive
            if let invalidReps = exercise.sets.first(where: { $0.reps <= 0 }) {
                throw WorkoutLogError.invalidReps("Reps must be greater than 0: \(invalidReps.reps)")
            }
        }
        
        // Validate duration is non-negative if present
        if let duration = duration, duration < 0 {
            throw WorkoutLogError.invalidDuration("Duration cannot be negative")
        }
    }
}

// MARK: - Error Types

enum WorkoutLogError: LocalizedError {
    case invalidExercises(String)
    case invalidSets(String)
    case invalidWeight(String)
    case invalidReps(String)
    case invalidDuration(String)
    
    var errorDescription: String? {
        switch self {
        case .invalidExercises(let message): return message
        case .invalidSets(let message): return message
        case .invalidWeight(let message): return message
        case .invalidReps(let message): return message
        case .invalidDuration(let message): return message
        }
    }
}

// MARK: - Convenience Methods

extension WorkoutLog {
    /// Calculates the total volume (weight × reps) for the entire workout
    var totalVolume: Double {
        exercises.reduce(0) { workoutTotal, exercise in
            workoutTotal + exercise.sets.reduce(0) { setTotal, set in
                setTotal + (set.weight ?? 0) * Double(set.reps)
            }
        }
    }
    
    /// Returns all completed sets across all exercises
    var completedSets: Int {
        exercises.reduce(0) { total, exercise in
            total + exercise.sets.filter { $0.completed }.count
        }
    }
    
    /// Returns the total number of sets across all exercises
    var totalSets: Int {
        exercises.reduce(0) { total, exercise in
            total + exercise.sets.count
        }
    }
    
    /// Checks if all sets in the workout are completed
    var isCompleted: Bool {
        exercises.allSatisfy { exercise in
            exercise.sets.allSatisfy { $0.completed }
        }
    }
}

#if DEBUG
// MARK: - Preview Helpers

extension WorkoutLog {
    static var preview: WorkoutLog {
        WorkoutLog(
            date: Date(),
            exercises: [
                LoggedExercise(
                    id: UUID(),
                    exercise: Exercise.preview,
                    sets: [
                        ExerciseSet(number: 1, weight: 100, reps: 8, completed: true),
                        ExerciseSet(number: 2, weight: 100, reps: 8, completed: true),
                        ExerciseSet(number: 3, weight: 100, reps: 8, completed: false)
                    ]
                )
            ],
            notes: "Great workout session!"
        )
    }
}
#endif
