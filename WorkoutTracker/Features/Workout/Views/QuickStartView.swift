import SwiftUI
import CoreData

struct QuickStartView: View {
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @EnvironmentObject private var workoutManager: WorkoutManager
    
    // MARK: - State
    @StateObject private var viewModel = QuickStartViewModel()
    @State private var showingExerciseSheet = false
    @State private var showingDiscardAlert = false
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            ZStack {
                if viewModel.exercises.isEmpty {
                    emptyStateView
                } else {
                    workoutView
                }
            }
            .navigationTitle("Quick Start")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    if !viewModel.exercises.isEmpty {
                        Button("Cancel") {
                            showingDiscardAlert = true
                        }
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    if !viewModel.exercises.isEmpty {
                        Button("Save") {
                            Task {
                                await viewModel.saveWorkout()
                            }
                        }
                        .disabled(!viewModel.canSaveWorkout)
                    }
                }
            }
            .sheet(isPresented: $showingExerciseSheet) {
                ExerciseSelectionView(selectedExercises: $viewModel.exercises)
            }
            .alert("Discard Workout?", isPresented: $showingDiscardAlert) {
                Button("Cancel", role: .cancel) { }
                Button("Discard", role: .destructive) {
                    viewModel.discardWorkout()
                }
            } message: {
                Text("Are you sure you want to discard this workout? This action cannot be undone.")
            }
        }
    }
    
    // MARK: - Subviews
    private var emptyStateView: some View {
        VStack(spacing: 16) {
            Spacer()
            
            Image(systemName: "dumbbell.fill")
                .font(.system(size: 60))
                .foregroundColor(.gray)
            
            Text("Start Your Workout")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text("Add exercises to begin tracking your workout")
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
            
            Button(action: {
                showingExerciseSheet = true
            }) {
                Text("Add Exercise")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.blue)
                    .cornerRadius(10)
            }
            .padding(.horizontal, 20)
            .padding(.top, 20)
            
            Spacer()
        }
        .padding()
    }
    
    private var workoutView: some View {
        List {
            ForEach(viewModel.exercises) { exercise in
                ExerciseSetView(exercise: exercise)
            }
            .onMove { from, to in
                viewModel.moveExercise(from: from, to: to)
            }
            .onDelete { indexSet in
                viewModel.removeExercises(at: indexSet)
            }
            
            Button(action: {
                showingExerciseSheet = true
            }) {
                HStack {
                    Image(systemName: "plus.circle.fill")
                    Text("Add Exercise")
                }
            }
        }
    }
}

// MARK: - Preview Provider
struct QuickStartView_Previews: PreviewProvider {
    static var previews: some View {
        QuickStartView()
            .environmentObject(WorkoutManager())
    }
}

// MARK: - View Model
final class QuickStartViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var exercises: [Exercise] = []
    @Published var isLoading = false
    @Published var error: Error?
    
    // MARK: - Computed Properties
    var canSaveWorkout: Bool {
        !exercises.isEmpty && !isLoading
    }
    
    // MARK: - Methods
    func saveWorkout() async {
        guard canSaveWorkout else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            // TODO: Implement workout saving logic
            // This would typically:
            // 1. Create a new WorkoutLog entity
            // 2. Save all exercise data
            // 3. Update local storage
            // 4. Trigger cloud sync
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
    
    func discardWorkout() {
        exercises.removeAll()
    }
    
    func moveExercise(from source: IndexSet, to destination: Int) {
        exercises.move(fromOffsets: source, toOffset: destination)
    }
    
    func removeExercises(at offsets: IndexSet) {
        exercises.remove(atOffsets: offsets)
    }
}

// MARK: - Supporting Views
struct ExerciseSetView: View {
    let exercise: Exercise
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(exercise.name)
                .font(.headline)
            
            // TODO: Implement set tracking view
            // This would include:
            // - Set number
            // - Weight input
            // - Reps input
            // - Set completion status
        }
        .padding(.vertical, 8)
    }
}
