import Foundation
import CoreData
import Combine

protocol WorkoutDetailViewModelDelegate: AnyObject {
    func workoutWasDeleted()
    func workoutWasUpdated()
}

@MainActor
final class WorkoutDetailViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published private(set) var workout: WorkoutLog
    @Published private(set) var isLoading = false
    @Published private(set) var error: Error?
    @Published var notes: String
    @Published var exercises: [LoggedExercise]
    
    // MARK: - Private Properties
    private let workoutManager: WorkoutManagerProtocol
    private var cancellables = Set<AnyCancellable>()
    weak var delegate: WorkoutDetailViewModelDelegate?
    
    // MARK: - Initialization
    init(workout: WorkoutLog, workoutManager: WorkoutManagerProtocol = WorkoutManager.shared) {
        self.workout = workout
        self.workoutManager = workoutManager
        self.notes = workout.notes ?? ""
        self.exercises = Array(workout.exercises)
        setupBindings()
    }
    
    // MARK: - Public Methods
    
    /// Deletes the current workout
    func deleteWorkout() async throws {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            try await workoutManager.deleteWorkout(workout)
            delegate?.workoutWasDeleted()
        } catch {
            self.error = error
            throw error
        }
    }
    
    /// Updates the workout notes
    func updateNotes(_ newNotes: String) async throws {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            var updatedWorkout = workout
            updatedWorkout.notes = newNotes
            
            try await workoutManager.updateWorkout(updatedWorkout)
            self.workout = updatedWorkout
            self.notes = newNotes
            delegate?.workoutWasUpdated()
        } catch {
            self.error = error
            throw error
        }
    }
    
    /// Updates exercise data for the workout
    func updateExercise(_ exercise: LoggedExercise, at index: Int) async throws {
        guard !isLoading, exercises.indices.contains(index) else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            var updatedExercises = exercises
            updatedExercises[index] = exercise
            
            var updatedWorkout = workout
            updatedWorkout.exercises = updatedExercises
            
            try await workoutManager.updateWorkout(updatedWorkout)
            self.workout = updatedWorkout
            self.exercises = updatedExercises
            delegate?.workoutWasUpdated()
        } catch {
            self.error = error
            throw error
        }
    }
    
    /// Clears any current error
    func clearError() {
        error = nil
    }
    
    // MARK: - Computed Properties
    
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .long
        formatter.timeStyle = .short
        return formatter.string(from: workout.date)
    }
    
    var formattedDuration: String? {
        guard let duration = workout.duration else { return nil }
        
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.hour, .minute]
        formatter.unitsStyle = .abbreviated
        return formatter.string(from: duration)
    }
    
    var totalVolume: Double {
        exercises.reduce(0) { total, exercise in
            total + exercise.sets.reduce(0) { setTotal, set in
                setTotal + (set.weight ?? 0) * Double(set.reps)
            }
        }
    }
    
    // MARK: - Private Methods
    
    private func setupBindings() {
        // Setup any necessary publishers for reactive updates
        $notes
            .debounce(for: .seconds(0.5), scheduler: DispatchQueue.main)
            .sink { [weak self] newNotes in
                guard let self = self, self.notes != self.workout.notes else { return }
                
                Task {
                    try? await self.updateNotes(newNotes)
                }
            }
            .store(in: &cancellables)
    }
}

// MARK: - Preview Helpers

#if DEBUG
extension WorkoutDetailViewModel {
    static var preview: WorkoutDetailViewModel {
        let workout = WorkoutLog() // Create a sample workout for preview
        return WorkoutDetailViewModel(workout: workout)
    }
}
#endif
