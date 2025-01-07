import SwiftUI
import CoreData

struct WorkoutListView: View {
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @EnvironmentObject private var workoutManager: WorkoutManager
    
    // MARK: - State
    @StateObject private var viewModel = WorkoutListViewModel()
    @State private var showingFilterSheet = false
    @State private var searchText = ""
    
    // MARK: - FetchRequest
    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \WorkoutLog.date, ascending: false)],
        animation: .default)
    private var workouts: FetchedResults<WorkoutLog>
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            Group {
                if workouts.isEmpty {
                    emptyStateView
                } else {
                    workoutListView
                }
            }
            .navigationTitle("Workouts")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    filterButton
                }
            }
            .searchable(text: $searchText, prompt: "Search workouts")
            .sheet(isPresented: $showingFilterSheet) {
                WorkoutFilterView(
                    filter: $viewModel.filter,
                    isPresented: $showingFilterSheet
                )
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
            
            Text("No Workouts Yet")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text("Your completed workouts will appear here")
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
            
            NavigationLink(destination: QuickStartView()) {
                Text("Start Workout")
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
    
    private var workoutListView: some View {
        List {
            ForEach(filteredWorkouts) { workout in
                NavigationLink(destination: WorkoutDetailView(workout: workout)) {
                    WorkoutRowView(workout: workout)
                }
            }
        }
        .listStyle(.inset)
    }
    
    private var filterButton: some View {
        Button(action: {
            showingFilterSheet = true
        }) {
            Image(systemName: "line.3.horizontal.decrease.circle")
                .symbolVariant(viewModel.filter.isActive ? .fill : .none)
        }
    }
    
    // MARK: - Computed Properties
    private var filteredWorkouts: [WorkoutLog] {
        viewModel.filterWorkouts(Array(workouts), searchText: searchText)
    }
}

// MARK: - Supporting Views
struct WorkoutRowView: View {
    let workout: WorkoutLog
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Date and Time
            Text(workout.date, style: .date)
                .font(.headline)
            
            // Workout Summary
            HStack {
                Label("\(workout.exercises.count) exercises", systemImage: "dumbbell.fill")
                Spacer()
                if let duration = workout.duration {
                    Label(duration.formatted(), systemImage: "clock")
                }
            }
            .font(.subheadline)
            .foregroundColor(.gray)
        }
        .padding(.vertical, 4)
    }
}

// MARK: - View Model
final class WorkoutListViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var filter = WorkoutFilter()
    
    // MARK: - Filter Methods
    func filterWorkouts(_ workouts: [WorkoutLog], searchText: String) -> [WorkoutLog] {
        var filtered = workouts
        
        // Apply search filter
        if !searchText.isEmpty {
            filtered = filtered.filter { workout in
                // TODO: Implement search logic
                // This would filter based on:
                // - Workout notes
                // - Exercise names
                // - Date (if search text matches date format)
                return true
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
        
        return filtered
    }
}

// MARK: - Supporting Types
struct WorkoutFilter {
    var startDate: Date?
    var endDate: Date?
    var selectedExercises: Set<Exercise> = []
    
    var isActive: Bool {
        startDate != nil || endDate != nil || !selectedExercises.isEmpty
    }
}

// MARK: - Preview Provider
struct WorkoutListView_Previews: PreviewProvider {
    static var previews: some View {
        WorkoutListView()
            .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
            .environmentObject(WorkoutManager())
    }
}
