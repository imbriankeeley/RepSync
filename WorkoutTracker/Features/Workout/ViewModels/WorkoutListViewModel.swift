import Foundation
import CoreData
import Combine

/// Filter options for workout list
struct WorkoutFilter {
    var startDate: Date?
    var endDate: Date?
    var selectedExercises: Set<Exercise> = []
    var sortOrder: WorkoutSortOrder = .dateDescending
    
    var isActive: Bool {
        startDate != nil || endDate != nil || !selectedExercises.isEmpty
    }
}

/// Sort options for workout list
enum WorkoutSortOrder {
    case dateAscending
    case dateDescending
    case exerciseCount
    case duration
}

@MainActor
final class WorkoutListViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var filter = WorkoutFilter()
    @Published var searchText = ""
    @Published var workouts: [WorkoutLog] = []
    @Published private(set) var isLoading = false
    @Published private(set) var error: Error?
    
    // MARK: - Private Properties
    private let workoutManager: WorkoutManagerProtocol
    private var cancellables = Set<AnyCancellable>()
    
    // MARK: - Initialization
    init(workoutManager: WorkoutManagerProtocol) {
        self.workoutManager = workoutManager
        setupBindings()
    }
    
    // MARK: - Public Methods
    
    /// Fetches workouts from local storage and updates the published workouts array
    func fetchWorkouts() async {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            let fetchedWorkouts = try await workoutManager.fetchWorkouts()
            self.workouts = applyFilters(to: fetchedWorkouts)
        } catch {
            self.error = error
        }
    }
    
    /// Deletes a workout at the specified index
    func deleteWorkout(at indexSet: IndexSet) async {
        guard let index = indexSet.first else { return }
        let workoutToDelete = workouts[index]
        
        do {
            try await workoutManager.deleteWorkout(workoutToDelete)
            await fetchWorkouts()
        } catch {
            self.error = error
        }
    }
    
    /// Clears any current error
    func clearError() {
        error = nil
    }
    
    // MARK: - Private Methods
    
    private func setupBindings() {
        // Combine publishers for filter and search updates
        Publishers.CombineLatest($filter, $searchText)
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .sink { [weak self] _, _ in
                Task {
                    await self?.fetchWorkouts()
                }
            }
            .store(in: &cancellables)
    }
    
    private func applyFilters(to workouts: [WorkoutLog]) -> [WorkoutLog] {
        var filtered = workouts
        
        // Apply search filter
        if !searchText.isEmpty {
            filtered = filtered.filter { workout in
                // Search in workout notes
                if let notes = workout.notes?.localizedCaseInsensitiveContains(searchText) {
                    if notes { return true }
                }
                
                // Search in exercise names
                let exerciseMatch = workout.exercises.contains { exercise in
                    exercise.exercise.name.localizedCaseInsensitiveContains(searchText)
                }
                if exerciseMatch { return true }
                
                // Search in date (if search text matches date format)
                let dateFormatter = DateFormatter()
                dateFormatter.dateStyle = .medium
                let dateString = dateFormatter.string(from: workout.date)
                return dateString.localizedCaseInsensitiveContains(searchText)
            }
        }
        
        // Apply date range filter
        if let startDate = filter.startDate {
            filtered = filtered.filter { $0.date >= startDate }
        }
        if let endDate = filter.endDate {
            filtered = filtered.filter { $0.date <= endDate }
        }
        
        // Apply exercise filter
        if !filter.selectedExercises.isEmpty {
            filtered = filtered.filter { workout in
                workout.exercises.contains { exercise in
                    filter.selectedExercises.contains(exercise.exercise)
                }
            }
        }
        
        // Apply sort order
        switch filter.sortOrder {
        case .dateDescending:
            filtered.sort { $0.date > $1.date }
        case .dateAscending:
            filtered.sort { $0.date < $1.date }
        case .exerciseCount:
            filtered.sort { $0.exercises.count > $1.exercises.count }
        case .duration:
            filtered.sort { ($0.duration ?? 0) > ($1.duration ?? 0) }
        }
        
        return filtered
    }
}

// MARK: - Preview Helpers

#if DEBUG
extension WorkoutListViewModel {
    static var preview: WorkoutListViewModel {
        let viewModel = WorkoutListViewModel(workoutManager: MockWorkoutManager())
        // Add sample data for preview if needed
        return viewModel
    }
}

private class MockWorkoutManager: WorkoutManagerProtocol {
    func fetchWorkouts() async throws -> [WorkoutLog] {
        // Return sample workouts for preview
        return []
    }
    
    func deleteWorkout(_ workout: WorkoutLog) async throws {
        // Mock deletion
    }
}
#endif
