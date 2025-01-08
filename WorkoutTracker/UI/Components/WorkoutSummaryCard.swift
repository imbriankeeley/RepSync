import SwiftUI

struct WorkoutSummaryCard: View {
    // MARK: - Properties
    
    /// The workout to display
    let workout: WorkoutLog
    
    /// Optional template information if this workout was based on a template
    var template: WorkoutTemplate?
    
    /// Whether to show detailed information
    var showDetails: Bool = true
    
    /// Action to perform when the card is tapped
    var onTap: (() -> Void)?
    
    // MARK: - Environment
    @Environment(\.colorScheme) private var colorScheme
    
    // MARK: - Body
    var body: some View {
        Button(action: {
            onTap?()
        }) {
            VStack(alignment: .leading, spacing: 12) {
                // Header with date and status
                headerView
                
                if showDetails {
                    // Exercise summary
                    exerciseSummaryView
                    
                    // Stats row (duration, volume, etc.)
                    statsView
                    
                    // Template name if applicable
                    if let template = template {
                        templateView(template)
                    }
                    
                    // Notes preview if available
                    if let notes = workout.notes, !notes.isEmpty {
                        notesView(notes)
                    }
                }
            }
            .padding()
            .background(cardBackground)
            .cornerRadius(12)
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    // MARK: - Supporting Views
    
    private var headerView: some View {
        HStack {
            // TODO: Implement header view
            // Show date, time, and completion status
            EmptyView()
        }
    }
    
    private var exerciseSummaryView: some View {
        VStack(alignment: .leading, spacing: 8) {
            // TODO: Implement exercise summary
            // Show total exercises and completion progress
            EmptyView()
        }
    }
    
    private var statsView: some View {
        HStack {
            // TODO: Implement stats view
            // Show duration, total volume, etc.
            EmptyView()
        }
    }
    
    private func templateView(_ template: WorkoutTemplate) -> some View {
        // TODO: Implement template view
        // Show template name and info
        EmptyView()
    }
    
    private func notesView(_ notes: String) -> some View {
        // TODO: Implement notes view
        // Show truncated notes preview
        EmptyView()
    }
    
    private var cardBackground: some View {
        Group {
            if colorScheme == .dark {
                Color(.systemGray6)
            } else {
                Color(.systemBackground)
            }
        }
        .shadow(radius: 2)
    }
}

// MARK: - Preview Provider

#if DEBUG
struct WorkoutSummaryCard_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            // Default preview
            WorkoutSummaryCard(
                workout: WorkoutLog.preview,
                showDetails: true
            )
            .padding()
            .previewLayout(.sizeThatFits)
            
            // Minimal preview (no details)
            WorkoutSummaryCard(
                workout: WorkoutLog.preview,
                showDetails: false
            )
            .padding()
            .previewLayout(.sizeThatFits)
            
            // With template
            WorkoutSummaryCard(
                workout: WorkoutLog.preview,
                template: WorkoutTemplate.preview,
                showDetails: true
            )
            .padding()
            .previewLayout(.sizeThatFits)
        }
    }
}
#endif
