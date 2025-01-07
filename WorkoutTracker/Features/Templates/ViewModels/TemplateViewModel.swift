import Foundation
import CoreData
import Combine

/// View model responsible for managing workout template data and operations
@MainActor
final class TemplateViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published private(set) var template: WorkoutTemplate
    @Published private(set) var exercises: [TemplateExercise] = []
    @Published private(set) var isLoading = false
    @Published private(set) var error: Error?
    
    // MARK: - Private Properties
    private let templateService: TemplateServiceProtocol
    private var cancellables = Set<AnyCancellable>()
    
    // MARK: - Initialization
    init(
        template: WorkoutTemplate? = nil,
        templateService: TemplateServiceProtocol = TemplateService()
    ) {
        self.template = template ?? WorkoutTemplate()
        self.templateService = templateService
        setupBindings()
    }
    
    // MARK: - Public Methods
    
    /// Saves the current template
    func saveTemplate() async throws {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            try validateTemplate()
            try await templateService.saveTemplate(template)
        } catch {
            self.error = error
            throw error
        }
    }
    
    /// Updates template name
    func updateName(_ name: String) {
        template.name = name.trimmingCharacters(in: .whitespacesAndNewlines)
    }
    
    /// Adds an exercise to the template
    func addExercise(_ exercise: Exercise) {
        let templateExercise = TemplateExercise(
            exercise: exercise,
            order: exercises.count,
            setScheme: template.defaultScheme
        )
        exercises.append(templateExercise)
        updateTemplate()
    }
    
    /// Removes an exercise from the template
    func removeExercise(at index: Int) {
        guard exercises.indices.contains(index) else { return }
        exercises.remove(at: index)
        reorderExercises()
        updateTemplate()
    }
    
    /// Moves an exercise to a new position
    func moveExercise(from source: IndexSet, to destination: Int) {
        exercises.move(fromOffsets: source, toOffset: destination)
        reorderExercises()
        updateTemplate()
    }
    
    /// Updates the set scheme for an exercise
    func updateSetScheme(_ setScheme: SetScheme, forExerciseAt index: Int) {
        guard exercises.indices.contains(index) else { return }
        exercises[index].setScheme = setScheme
        updateTemplate()
    }
    
    // MARK: - Private Methods
    
    private func setupBindings() {
        // TODO: Setup any necessary Combine publishers
    }
    
    private func validateTemplate() throws {
        // TODO: Implement template validation
    }
    
    private func reorderExercises() {
        for (index, exercise) in exercises.enumerated() {
            exercise.order = index
        }
    }
    
    private func updateTemplate() {
        template.exercises = exercises
    }
}

// MARK: - Preview Helpers

#if DEBUG
extension TemplateViewModel {
    static var preview: TemplateViewModel {
        let template = WorkoutTemplate()
        // Add preview data here
        return TemplateViewModel(template: template)
    }
}
#endif
