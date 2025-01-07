import SwiftUI
import CoreData

struct TemplateDetailView: View {
    // MARK: - Properties
    let template: WorkoutTemplate
    
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.dismiss) private var dismiss
    
    // MARK: - State
    @StateObject private var viewModel: TemplateDetailViewModel
    @State private var showingDeleteAlert = false
    @State private var showingExerciseSheet = false
    @State private var showingEditNameSheet = false
    @State private var isEditing = false
    
    // MARK: - Initialization
    init(template: WorkoutTemplate) {
        self.template = template
        _viewModel = StateObject(wrappedValue: TemplateDetailViewModel(template: template))
    }
    
    // MARK: - Body
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Template Header Section
                headerSection
                
                // Exercise List Section
                exercisesSection
            }
            .padding()
        }
        .navigationTitle("Template Details")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
                    Button(action: {
                        showingEditNameSheet = true
                    }) {
                        Label("Rename", systemImage: "pencil")
                    }
                    
                    Button(action: {
                        showingExerciseSheet = true
                    }) {
                        Label("Add Exercise", systemImage: "plus")
                    }
                    
                    Button(action: {
                        isEditing.toggle()
                    }) {
                        Label(isEditing ? "Done Editing" : "Edit Order",
                              systemImage: isEditing ? "checkmark.circle" : "arrow.up.arrow.down")
                    }
                    
                    Button(role: .destructive, action: {
                        showingDeleteAlert = true
                    }) {
                        Label("Delete Template", systemImage: "trash")
                    }
                } label: {
                    Image(systemName: "ellipsis.circle")
                }
            }
        }
        .alert("Delete Template", isPresented: $showingDeleteAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                Task {
                    await viewModel.deleteTemplate()
                    dismiss()
                }
            }
        } message: {
            Text("Are you sure you want to delete this template? This action cannot be undone.")
        }
        .sheet(isPresented: $showingEditNameSheet) {
            TemplateEditNameView(template: template)
        }
        .sheet(isPresented: $showingExerciseSheet) {
            ExerciseSelectionView(selectedExercises: $viewModel.exercises)
        }
    }
    
    // MARK: - Subviews
    private var headerSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(template.name)
                .font(.title2)
                .fontWeight(.bold)
            
            HStack {
                Image(systemName: "dumbbell.fill")
                Text("\(template.exercises.count) exercises")
            }
            .foregroundColor(.secondary)
        }
    }
    
    private var exercisesSection: some View {
        VStack(alignment: .leading, spacing: 16) {
            if isEditing {
                editableExerciseList
            } else {
                staticExerciseList
            }
        }
    }
    
    private var editableExerciseList: some View {
        ForEach(viewModel.exercises) { exercise in
            HStack {
                Image(systemName: "line.3.horizontal")
                    .foregroundColor(.gray)
                Text(exercise.exercise.name)
                Spacer()
                Button(action: {
                    viewModel.removeExercise(exercise)
                }) {
                    Image(systemName: "minus.circle.fill")
                        .foregroundColor(.red)
                }
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(10)
        }
        .onMove { source, destination in
            viewModel.moveExercise(from: source, to: destination)
        }
    }
    
    private var staticExerciseList: some View {
        ForEach(viewModel.exercises) { exercise in
            HStack {
                Text(exercise.exercise.name)
                    .font(.headline)
                Spacer()
                if let setScheme = exercise.setScheme {
                    Text("\(setScheme.sets)×\(setScheme.defaultReps)")
                        .foregroundColor(.secondary)
                }
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(10)
        }
    }
}

// MARK: - View Model
final class TemplateDetailViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var exercises: [TemplateExercise]
    @Published private(set) var isLoading = false
    @Published private(set) var error: Error?
    
    // MARK: - Private Properties
    private let template: WorkoutTemplate
    
    // MARK: - Initialization
    init(template: WorkoutTemplate) {
        self.template = template
        self.exercises = Array(template.exercises)
    }
    
    // MARK: - Methods
    func deleteTemplate() async {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            // TODO: Implement template deletion logic
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
    
    func moveExercise(from source: IndexSet, to destination: Int) {
        exercises.move(fromOffsets: source, toOffset: destination)
        updateExerciseOrder()
    }
    
    func removeExercise(_ exercise: TemplateExercise) {
        exercises.removeAll { $0.id == exercise.id }
        updateExerciseOrder()
    }
    
    private func updateExerciseOrder() {
        // TODO: Implement exercise order update logic
    }
}

// MARK: - Preview Provider
struct TemplateDetailView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            TemplateDetailView(template: WorkoutTemplate.preview)
                .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
        }
    }
}

// MARK: - Preview Helper
extension WorkoutTemplate {
    static var preview: WorkoutTemplate {
        let template = WorkoutTemplate()
        // TODO: Add preview data
        return template
    }
}
