import SwiftUI
import CoreData

struct CalendarView: View {
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @EnvironmentObject private var workoutManager: WorkoutManager
    
    // MARK: - State
    @StateObject private var viewModel = CalendarViewModel()
    @State private var selectedDate: Date?
    @State private var showingWorkoutDetails = false
    
    // MARK: - FetchRequest
    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \WorkoutLog.date, ascending: true)],
        animation: .default
    )
    private var workouts: FetchedResults<WorkoutLog>
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Month Navigation
                monthNavigationView
                
                // Calendar Grid
                calendarGridView
                
                // Selected Date Workouts
                if let selectedDate = selectedDate {
                    workoutListView(for: selectedDate)
                }
            }
            .navigationTitle("Calendar")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        viewModel.navigateToToday()
                    }) {
                        Text("Today")
                    }
                }
            }
        }
    }
    
    // MARK: - Subviews
    private var monthNavigationView: some View {
        HStack {
            Button(action: {
                viewModel.navigateToPreviousMonth()
            }) {
                Image(systemName: "chevron.left")
            }
            
            Spacer()
            
            Text(viewModel.currentMonthYearString)
                .font(.headline)
            
            Spacer()
            
            Button(action: {
                viewModel.navigateToNextMonth()
            }) {
                Image(systemName: "chevron.right")
            }
        }
        .padding()
    }
    
    private var calendarGridView: some View {
        VStack(spacing: 0) {
            // Weekday headers
            weekdayHeaderView
            
            // Calendar days grid
            LazyVGrid(columns: viewModel.gridColumns, spacing: 0) {
                ForEach(viewModel.calendarDays, id: \.self) { date in
                    CalendarDayCell(
                        date: date,
                        isSelected: selectedDate == date,
                        hasWorkout: viewModel.hasWorkout(on: date, workouts: workouts),
                        isCurrentMonth: viewModel.isInCurrentMonth(date)
                    )
                    .onTapGesture {
                        selectedDate = date
                    }
                }
            }
        }
    }
    
    private var weekdayHeaderView: some View {
        HStack {
            ForEach(viewModel.weekdaySymbols, id: \.self) { symbol in
                Text(symbol)
                    .font(.caption)
                    .frame(maxWidth: .infinity)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 8)
    }
    
    private func workoutListView(for date: Date) -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Workouts")
                .font(.headline)
                .padding(.horizontal)
            
            if let dateWorkouts = viewModel.workouts(for: date, from: workouts) {
                List(dateWorkouts) { workout in
                    NavigationLink(destination: WorkoutDetailView(workout: workout)) {
                        WorkoutRowView(workout: workout)
                    }
                }
            } else {
                Text("No workouts on this day")
                    .foregroundColor(.secondary)
                    .padding()
            }
        }
    }
}

// MARK: - Supporting Views
private struct CalendarDayCell: View {
    let date: Date
    let isSelected: Bool
    let hasWorkout: Bool
    let isCurrentMonth: Bool
    
    var body: some View {
        VStack {
            Text("\(Calendar.current.component(.day, from: date))")
                .font(.subheadline)
                .foregroundColor(isCurrentMonth ? .primary : .secondary)
            
            if hasWorkout {
                Circle()
                    .fill(Color.blue)
                    .frame(width: 6, height: 6)
            }
        }
        .frame(height: 44)
        .background(isSelected ? Color.blue.opacity(0.2) : Color.clear)
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

// MARK: - View Model
final class CalendarViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var currentDate = Date()
    @Published private(set) var calendarDays: [Date] = []
    
    // MARK: - Properties
    let gridColumns = Array(repeating: GridItem(.flexible()), count: 7)
    let weekdaySymbols = Calendar.current.veryShortWeekdaySymbols
    
    // MARK: - Initialization
    init() {
        generateCalendarDays()
    }
    
    // MARK: - Navigation Methods
    func navigateToToday() {
        currentDate = Date()
        generateCalendarDays()
    }
    
    func navigateToPreviousMonth() {
        guard let newDate = Calendar.current.date(
            byAdding: .month,
            value: -1,
            to: currentDate
        ) else { return }
        
        currentDate = newDate
        generateCalendarDays()
    }
    
    func navigateToNextMonth() {
        guard let newDate = Calendar.current.date(
            byAdding: .month,
            value: 1,
            to: currentDate
        ) else { return }
        
        currentDate = newDate
        generateCalendarDays()
    }
    
    // MARK: - Helper Methods
    var currentMonthYearString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy"
        return formatter.string(from: currentDate)
    }
    
    func isInCurrentMonth(_ date: Date) -> Bool {
        Calendar.current.isDate(date, equalTo: currentDate, toGranularity: .month)
    }
    
    func hasWorkout(on date: Date, workouts: FetchedResults<WorkoutLog>) -> Bool {
        workouts.contains { Calendar.current.isDate($0.date, inSameDayAs: date) }
    }
    
    func workouts(for date: Date, from workouts: FetchedResults<WorkoutLog>) -> [WorkoutLog]? {
        let dateWorkouts = workouts.filter {
            Calendar.current.isDate($0.date, inSameDayAs: date)
        }
        return dateWorkouts.isEmpty ? nil : Array(dateWorkouts)
    }
    
    // MARK: - Private Methods
    private func generateCalendarDays() {
        // TODO: Generate calendar days for the current month view
        // This should include:
        // - Days from the previous month that appear in the first week
        // - All days of the current month
        // - Days from the next month that appear in the last week
    }
}

// MARK: - Preview Provider
struct CalendarView_Previews: PreviewProvider {
    static var previews: some View {
        CalendarView()
            .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
            .environmentObject(WorkoutManager())
    }
}
