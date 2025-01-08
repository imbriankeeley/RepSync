import SwiftUI

/// Defines the color palette for the WorkoutTracker app
enum AppColors {
    // MARK: - Brand Colors
    
    /// Primary brand color
    static let primary = Color("Primary")
    
    /// Secondary brand color
    static let secondary = Color("Secondary")
    
    /// Accent color for highlighting and CTAs
    static let accent = Color("Accent")
    
    // MARK: - Status Colors
    
    /// Color for completed or success states
    static let success = Color("Success")
    
    /// Color for warning or caution states
    static let warning = Color("Warning")
    
    /// Color for error or failure states
    static let error = Color("Error")
    
    /// Color for in-progress or active states
    static let progress = Color("Progress")
    
    // MARK: - UI Element Colors
    
    /// Background color for cards and content areas
    static let cardBackground = Color("CardBackground")
    
    /// Primary text color
    static let textPrimary = Color("TextPrimary")
    
    /// Secondary text color
    static let textSecondary = Color("TextSecondary")
    
    /// Color for disabled states
    static let disabled = Color("Disabled")
    
    /// Color for dividers and separators
    static let divider = Color("Divider")
    
    // MARK: - Exercise Category Colors
    
    /// Color for strength training related elements
    static let strength = Color("Strength")
    
    /// Color for cardio related elements
    static let cardio = Color("Cardio")
    
    /// Color for flexibility related elements
    static let flexibility = Color("Flexibility")
    
    // MARK: - Chart Colors
    
    /// Array of colors for charts and graphs
    static let chartColors: [Color] = [
        Color("ChartColor1"),
        Color("ChartColor2"),
        Color("ChartColor3"),
        Color("ChartColor4"),
        Color("ChartColor5")
    ]
}

// MARK: - Color Extensions

extension Color {
    /// Creates a dynamic color that adapts to light and dark mode
    static func dynamic(light: Color, dark: Color) -> Color {
        return Color(UIColor { traitCollection in
            switch traitCollection.userInterfaceStyle {
            case .dark:
                return UIColor(dark)
            default:
                return UIColor(light)
            }
        })
    }
}

// MARK: - Constants

extension AppColors {
    /// Opacity values for various UI states
    enum Opacity {
        static let disabled: CGFloat = 0.4
        static let dimmed: CGFloat = 0.6
        static let overlay: CGFloat = 0.8
    }
    
    /// Color values for gradients
    enum Gradients {
        static let primaryGradient = LinearGradient(
            gradient: Gradient(colors: [primary, primary.opacity(0.8)]),
            startPoint: .top,
            endPoint: .bottom
        )
        
        static let accentGradient = LinearGradient(
            gradient: Gradient(colors: [accent, accent.opacity(0.8)]),
            startPoint: .leading,
            endPoint: .trailing
        )
    }
}

// MARK: - Usage Examples

#if DEBUG
struct ColorPreviewProvider {
    /// Example of how to use the color system
    static var examples: some View {
        VStack {
            Text("Primary Button")
                .foregroundColor(.white)
                .padding()
                .background(AppColors.primary)
                .cornerRadius(8)
            
            Text("Warning State")
                .foregroundColor(AppColors.warning)
            
            Rectangle()
                .fill(AppColors.cardBackground)
                .frame(height: 100)
                .overlay(
                    Text("Card Example")
                        .foregroundColor(AppColors.textPrimary)
                )
        }
    }
}
#endif
