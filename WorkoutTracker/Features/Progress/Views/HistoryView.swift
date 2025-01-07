import SwiftUI
import CoreData
import Charts

struct HistoryView: View {
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @EnvironmentObject private var workoutManager: WorkoutManager
    
    // MARK: - State
    @StateObject private var viewModel = HistoryViewModel()
    @State private var selectedTimeRange: TimeRange = .month
    @State private var selectedExercise: Exercise?
    @State private var showingExerciseSelection = false
    
    // MARK: - FetchRequest
    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \WorkoutLog.date, ascending: false)],
        animation: .default)
    private var workouts: FetchedResults<WorkoutLog>
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Time Range Selector
                    timeRangeSelector
                    
                    // Summary Statistics
                    summaryCard
                    
                    // Progress Charts
                    if let selectedExercise = selectedExercise {
                        exerciseProgressChart(for: selectedExercise)
                    } else {
                        workoutFrequencyChart
                    }
                    
                    // Exercise Selection
                    exerciseSelectionSection
                    
                    // Recent Workouts
                    recentWorkoutsSection
                }
                .padding()
            }
            .navigationTitle("Progress")
            .navigationBarTitleDisplayMode(.large)
            .sheet(isPresented: $showingExerciseSelection) {
                ExerciseSelectionView(selectedExercise: $selectedExercise)
            }
        }
    }
    
    // MARK: - Subviews
    private var timeRangeSelector: some View {
        Picker("Time Range", selection: $selectedTimeRange) {
            ForEach(TimeRange.allCases) { range in
                Text(range.displayName).tag(range)
            }
        }
        .pickerStyle(.segmented)
    }
    
    private var summaryCard: some View {
        VStack(spacing: 16) {
            // TODO: Implement summary statistics view
            // This should show:
            // - Total workouts
            // - Total volume
            // - Average duration
            // - Workout frequency
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
    
    private var workoutFrequencyChart: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Workout Frequency")
                .font(.headline)
            
            // TODO: Implement workout frequency chart
            // This should show:
            // - Bar chart of workout frequency
            // - X-axis: time periods
            // - Y-axis: number of workouts
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
    
    private func exerciseProgressChart(for exercise: Exercise) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("\(exercise.name) Progress")
                .font(.headline)
            
            // TODO: Implement exercise progress chart
            // This should show:
            // - Line chart of exercise progress
            // - X-axis: workout dates
            // - Y-axis: weight/reps/volume
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
    
    private var exerciseSelectionSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Track Exercise Progress")
                .font(.headline)
            
            Button(action: {
                showingExerciseSelection = true
            }) {
                HStack {
                    if let exercise = selectedExercise {
                        Text(exercise.name)
                        Spacer()
                        Button(action: {
                            selectedExercise = nil
                        }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.gray)
                        }
                    } else {
                        Text("Select Exercise")
                        Spacer()
                        Image(systemName: "chevron.right")
                    }
                }
                .padding()
                .background(Color(.systemBackground))
                .cornerRadius(8)
                .shadow(radius: 1)
            }
        }
    }
    
    private var recentWorkoutsSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Recent Workouts")
                .font(.headline)
            
            ForEach(viewModel.filteredWorkouts(workouts, timeRange: selectedTimeRange)) { workout in
                NavigationLink(destination: WorkoutDetailView(workout: workout)) {
                    WorkoutHistoryRow(workout: workout)
                }
            }
        }
    }
}

// MARK: - Supporting Views
private struct WorkoutHistoryRow: View {
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
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(8)
        .shadow(radius: 1)
    }
}

// MARK: - View Model
final class HistoryViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published private(set) var isLoading = false
    @Published private(set) var error: Error?
    
    // MARK: - Methods
    func filteredWorkouts(_ workouts: FetchedResults<WorkoutLog>, timeRange: TimeRange) -> [WorkoutLog] {
        guard let startDate = Calendar.current.date(byAdding: timeRange.dateComponent,
                                                  value: -timeRange.value,
                                                  to: Date()) else {
            return Array(workouts)
        }
        
        return workouts.filter { $0.date >= startDate }
    }
    
    func calculateStatistics(_ workouts: [WorkoutLog]) -> WorkoutStatistics {
        // TODO: Implement statistics calculation
        // This should calculate:
        // - Total workouts
        // - Average duration
        // - Total volume
        // - Most frequent exercises
        return WorkoutStatistics()
    }
    
    func exerciseProgress(_ exercise: Exercise, workouts: [WorkoutLog]) -> [ExerciseProgressPoint] {
        // TODO: Implement exercise progress calculation
        // This should track:
        // - Weight progression
        // - Volume progression
        // - Rep progression
        return []
    }
}

// MARK: - Supporting Types
enum TimeRange: Int, CaseIterable, Identifiable {
    case week = 0
    case month = 1
    case threeMonths = 2
    case sixMonths = 3
    case year = 4
    
    var id: Int { rawValue }
    
    var displayName: String {
        switch self {
        case .week: return "Week"
        case .month: return "Month"
        case .threeMonths: return "3 Months"
        case .sixMonths: return "6 Months"
        case .year: return "Year"
        }
    }
    
    var dateComponent: Calendar.Component {
        switch self {
        case .week: return .weekOfYear
        case .month: return .month
        case .threeMonths: return .month
        case .sixMonths: return .month
        case .year: return .year
        }
    }
    
    var value: Int {
        switch self {
        case .week: return 1
        case .month: return 1
        case .threeMonths: return 3
        case .sixMonths: return 6
        case .year: return 1
        }
    }
}

struct WorkoutStatistics {
    var totalWorkouts: Int = 0
    var averageDuration: TimeInterval = 0
    var totalVolume: Double = 0
    var mostFrequentExercises: [Exercise] = []
}

struct ExerciseProgressPoint: Identifiable {
    let id = UUID()
    let date: Date
    let weight: Double
    let reps: Int
    let volume: Double
}

// MARK: - Preview Provider
struct HistoryView_Previews: PreviewProvider {
    static var previews: some View {
        HistoryView()
            .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
            .environmentObject(WorkoutManager())
    }
}
