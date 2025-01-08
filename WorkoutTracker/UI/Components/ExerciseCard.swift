import SwiftUI

struct ExerciseCard: View {
    // MARK: - Properties
    
    let exercise: Exercise
    var showDetails: Bool = true
    var isSelected: Bool = false
    var onTap: (() -> Void)?
    
    // MARK: - Body
    
    var body: some View {
        Button(action: {
            onTap?()
        }) {
            VStack(alignment: .leading, spacing: 8) {
                // Exercise Name and Equipment
                HStack {
                    Text(exercise.name)
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    Spacer()
                    
                    if !exercise.equipment.isEmpty {
                        equipmentIcons
                    }
                }
                
                if showDetails {
                    // Muscle Groups
                    if !exercise.primaryMuscles.isEmpty {
                        muscleGroupsView
                    }
                    
                    // Custom Indicator
                    if exercise.isCustom {
                        customExerciseIndicator
                    }
                }
            }
            .padding(12)
            .background(cardBackground)
            .cornerRadius(12)
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    // MARK: - Supporting Views
    
    private var equipmentIcons: some View {
        HStack(spacing: 4) {
            // TODO: Add equipment icons
            // This should show small icons for each piece of equipment
        }
    }
    
    private var muscleGroupsView: some View {
        VStack(alignment: .leading, spacing: 4) {
            // TODO: Add muscle group tags
            // This should show primary and secondary muscle groups
        }
    }
    
    private var customExerciseIndicator: some View {
        // TODO: Add custom exercise indicator
        // This should show a badge or icon for custom exercises
        EmptyView()
    }
    
    private var cardBackground: some View {
        Group {
            if isSelected {
                Color.blue.opacity(0.1)
            } else {
                Color(.systemGray6)
            }
        }
    }
}

// MARK: - Preview Provider

#if DEBUG
struct ExerciseCard_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            // Default preview
            ExerciseCard(
                exercise: Exercise.preview,
                showDetails: true,
                isSelected: false
            )
            .padding()
            .previewLayout(.sizeThatFits)
            
            // Selected state
            ExerciseCard(
                exercise: Exercise.preview,
                showDetails: true,
                isSelected: true
            )
            .padding()
            .previewLayout(.sizeThatFits)
            
            // Minimal view (no details)
            ExerciseCard(
                exercise: Exercise.preview,
                showDetails: false,
                isSelected: false
            )
            .padding()
            .previewLayout(.sizeThatFits)
        }
    }
}
#endif
