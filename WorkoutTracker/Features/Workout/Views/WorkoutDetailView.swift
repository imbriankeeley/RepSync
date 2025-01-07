import SwiftUI
import CoreData

struct WorkoutDetailView: View {
    // MARK: - Properties
    let workout: WorkoutLog
    
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.dismiss) private var dismiss
    
    // MARK: - State
    @StateObject private var viewModel: WorkoutDetailViewModel
    @State private var showingDeleteAlert = false
    @State private var showingEditSheet = false
    @State private var showingNoteEditor = false
    
    // MARK: - Initialization
    init(workout: WorkoutLog) {
        self.workout = workout
        _viewModel = StateObject(wrappedValue: WorkoutDetailViewModel(workout: workout))
    }
    
    // MARK: - Body
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Workout Summary Section
                summarySection
                
                // Exercise List Section
                exercisesSection
                
                // Notes Section
                if let notes = workout.notes, !notes.isEmpty {
                    notesSection(notes)
                }
            }
            .padding()
        }
        .navigationTitle("Workout Details")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
                    Button(action: {
                        showingEditSheet = true
                    }) {
                        Label("Edit Workout", systemImage: "pencil")
                    }
                    
                    Button(action: {
                        showingNoteEditor = true
                    }) {
                        Label("Add Note", systemImage: "note.text")
                    }
                    
                    Button(role: .destructive, action: {
                        showingDeleteAlert = true
                    }) {
                        Label("Delete Workout", systemImage: "trash")
                    }
                } label: {
                    Image(systemName: "ellipsis.circle")
                }
            }
        }
        .alert("Delete Workout", isPresented: $showingDeleteAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                Task {
                    await viewModel.deleteWorkout()
                    dismiss()
                }
            }
        } message: {
            Text("Are you sure you want to delete this workout? This action cannot be undone.")
        }
        .sheet(isPresented: $showingEditSheet) {
            // TODO: Implement edit workout sheet
        }
        .sheet(isPresented: $showingNoteEditor) {
            // TODO: Implement note editor sheet
        }
    }
    
    // MARK: - Subviews
    private var summarySection: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Date and Time
            HStack {
                Image(systemName: "calendar")
                Text(workout.date, style: .date)
                Text(workout.date, style: .time)
            }
            .font(.headline)
            
            // Duration
            if let duration = workout.duration {
                HStack {
                    Image(systemName: "clock")
                    Text(duration.formatted())
                }
            }
            
            // Exercise Count
            HStack {
                Image(systemName: "dumbbell.fill")
                Text("\(workout.exercises.count) exercises")
            }
        }
        .foregroundColor(.secondary)
    }
    
    private var exercisesSection: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Exercises")
                .font(.title2)
                .fontWeight(.bold)
            
            ForEach(workout.exercises) { exercise in
                ExerciseDetailRow(exercise: exercise)
            }
        }
    }
    
    private func notesSection(_ notes: String) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Notes")
                .font(.title2)
                .fontWeight(.bold)
            
            Text(notes)
                .foregroundColor(.secondary)
        }
    }
}

// MARK: - Supporting Views
private struct ExerciseDetailRow: View {
    let exercise: LoggedExercise
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(exercise.exercise.name)
                .font(.headline)
            
            ForEach(exercise.sets) { set in
                HStack {
                    Text("Set \(set.number)")
                    Spacer()
                    if let weight = set.weight {
                        Text("\(weight, specifier: "%.1f") kg")
                    }
                    Text("\(set.reps) reps")
                        .foregroundColor(.secondary)
                }
                .font(.subheadline)
            }
        }
        .padding()
        .background(Color.gray.opacity(0.1))
        .cornerRadius(10)
    }
}

// MARK: - View Model
final class WorkoutDetailViewModel: ObservableObject {
    // MARK: - Properties
    private let workout: WorkoutLog
    
    // MARK: - Published Properties
    @Published var isLoading = false
    @Published var error: Error?
    
    // MARK: - Initialization
    init(workout: WorkoutLog) {
        self.workout = workout
    }
    
    // MARK: - Methods
    func deleteWorkout() async {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            // TODO: Implement workout deletion logic
            // This should:
            // 1. Delete from Core Data
            // 2. Trigger cloud sync
            // 3. Clean up any related resources
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
}

// MARK: - Preview Provider
struct WorkoutDetailView_Previews: PreviewProvider {
    static var previews: some View {
        // TODO: Create a sample workout for preview
        NavigationView {
            WorkoutDetailView(workout: WorkoutLog())
                .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
        }
    }
}
