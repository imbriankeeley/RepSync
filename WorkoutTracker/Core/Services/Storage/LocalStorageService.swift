import Foundation
import CoreData

/// Protocol defining the interface for local data storage operations
protocol LocalStorageServiceProtocol {
    // MARK: - Workout Operations
    func saveWorkout(_ workout: WorkoutLog) throws
    func fetchWorkouts() throws -> [WorkoutLog]
    func fetchWorkout(id: UUID) throws -> WorkoutLog?
    func updateWorkout(_ workout: WorkoutLog) throws
    func deleteWorkout(_ workout: WorkoutLog) throws
    func fetchWorkouts(from startDate: Date, to endDate: Date) throws -> [WorkoutLog]
    
    // MARK: - Template Operations
    func saveTemplate(_ template: WorkoutTemplate) throws
    func fetchTemplates() throws -> [WorkoutTemplate]
    func fetchTemplate(id: UUID) throws -> WorkoutTemplate?
    func updateTemplate(_ template: WorkoutTemplate) throws
    func deleteTemplate(_ template: WorkoutTemplate) throws
    
    // MARK: - Exercise Operations
    func saveExercise(_ exercise: Exercise) throws
    func fetchExercises() throws -> [Exercise]
    func fetchExercise(id: UUID) throws -> Exercise?
    func updateExercise(_ exercise: Exercise) throws
    func deleteExercise(_ exercise: Exercise) throws
}

/// Service responsible for handling local data storage using CoreData
class LocalStorageService: LocalStorageServiceProtocol {
    // MARK: - Properties
    
    private let persistenceController: PersistenceController
    private let context: NSManagedObjectContext
    
    // MARK: - Initialization
    
    init(persistenceController: PersistenceController = .shared) {
        self.persistenceController = persistenceController
        self.context = persistenceController.container.viewContext
    }
    
    // MARK: - Workout Operations
    
    func saveWorkout(_ workout: WorkoutLog) throws {
        // TODO: Implement workout saving logic
        // 1. Create managed object
        // 2. Set properties
        // 3. Save context
    }
    
    func fetchWorkouts() throws -> [WorkoutLog] {
        // TODO: Implement workout fetching logic
        // 1. Create fetch request
        // 2. Execute request
        // 3. Convert to model objects
        return []
    }
    
    func fetchWorkout(id: UUID) throws -> WorkoutLog? {
        // TODO: Implement single workout fetching logic
        // 1. Create fetch request with predicate
        // 2. Execute request
        // 3. Convert to model object
        return nil
    }
    
    func updateWorkout(_ workout: WorkoutLog) throws {
        // TODO: Implement workout updating logic
        // 1. Fetch existing managed object
        // 2. Update properties
        // 3. Save context
    }
    
    func deleteWorkout(_ workout: WorkoutLog) throws {
        // TODO: Implement workout deletion logic
        // 1. Fetch existing managed object
        // 2. Delete object
        // 3. Save context
    }
    
    func fetchWorkouts(from startDate: Date, to endDate: Date) throws -> [WorkoutLog] {
        // TODO: Implement date range workout fetching logic
        // 1. Create fetch request with date predicate
        // 2. Execute request
        // 3. Convert to model objects
        return []
    }
    
    // MARK: - Template Operations
    
    func saveTemplate(_ template: WorkoutTemplate) throws {
        // TODO: Implement template saving logic
    }
    
    func fetchTemplates() throws -> [WorkoutTemplate] {
        // TODO: Implement template fetching logic
        return []
    }
    
    func fetchTemplate(id: UUID) throws -> WorkoutTemplate? {
        // TODO: Implement single template fetching logic
        return nil
    }
    
    func updateTemplate(_ template: WorkoutTemplate) throws {
        // TODO: Implement template updating logic
    }
    
    func deleteTemplate(_ template: WorkoutTemplate) throws {
        // TODO: Implement template deletion logic
    }
    
    // MARK: - Exercise Operations
    
    func saveExercise(_ exercise: Exercise) throws {
        // TODO: Implement exercise saving logic
    }
    
    func fetchExercises() throws -> [Exercise] {
        // TODO: Implement exercise fetching logic
        return []
    }
    
    func fetchExercise(id: UUID) throws -> Exercise? {
        // TODO: Implement single exercise fetching logic
        return nil
    }
    
    func updateExercise(_ exercise: Exercise) throws {
        // TODO: Implement exercise updating logic
    }
    
    func deleteExercise(_ exercise: Exercise) throws {
        // TODO: Implement exercise deletion logic
    }
    
    // MARK: - Private Methods
    
    private func save() throws {
        if context.hasChanges {
            try context.save()
        }
    }
}

// MARK: - Error Types

enum LocalStorageError: LocalizedError {
    case saveFailed(Error)
    case fetchFailed(Error)
    case deleteFailed(Error)
    case recordNotFound(String)
    case invalidData(String)
    
    var errorDescription: String? {
        switch self {
        case .saveFailed(let error):
            return "Failed to save data: \(error.localizedDescription)"
        case .fetchFailed(let error):
            return "Failed to fetch data: \(error.localizedDescription)"
        case .deleteFailed(let error):
            return "Failed to delete data: \(error.localizedDescription)"
        case .recordNotFound(let message):
            return "Record not found: \(message)"
        case .invalidData(let message):
            return "Invalid data: \(message)"
        }
    }
}

#if DEBUG
// MARK: - Preview Helpers

extension LocalStorageService {
    static var preview: LocalStorageService {
        LocalStorageService(persistenceController: .preview)
    }
}
