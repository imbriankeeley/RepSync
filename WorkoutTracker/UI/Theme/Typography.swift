import SwiftUI

/// Defines the typography system for the WorkoutTracker app
enum AppTypography {
    // MARK: - Font Families
    
    /// System font families used in the app
    enum FontFamily {
        /// Primary font family for most text
        static let primary = "SF Pro"
        
        /// Secondary font family for specific use cases
        static let secondary = "SF Pro Rounded"
        
        /// Monospace font family for numerical data
        static let mono = "SF Mono"
    }
    
    // MARK: - Font Sizes
    
    /// Standard font sizes used throughout the app
    enum FontSize {
        /// Extra small text (10pt)
        static let xs: CGFloat = 10
        
        /// Small text (12pt)
        static let sm: CGFloat = 12
        
        /// Regular body text (16pt)
        static let base: CGFloat = 16
        
        /// Large text (18pt)
        static let lg: CGFloat = 18
        
        /// Extra large text (20pt)
        static let xl: CGFloat = 20
        
        /// Double extra large text (24pt)
        static let xxl: CGFloat = 24
        
        /// Triple extra large text (32pt)
        static let xxxl: CGFloat = 32
    }
    
    // MARK: - Line Heights
    
    /// Standard line heights for different text sizes
    enum LineHeight {
        /// Tight line height (1.2)
        static let tight: CGFloat = 1.2
        
        /// Normal line height (1.5)
        static let normal: CGFloat = 1.5
        
        /// Relaxed line height (1.7)
        static let relaxed: CGFloat = 1.7
    }
    
    // MARK: - Font Weights
    
    /// Font weights used in the app
    enum FontWeight {
        /// Regular weight (400)
        static let regular = Font.Weight.regular
        
        /// Medium weight (500)
        static let medium = Font.Weight.medium
        
        /// Semibold weight (600)
        static let semibold = Font.Weight.semibold
        
        /// Bold weight (700)
        static let bold = Font.Weight.bold
    }
    
    // MARK: - Text Styles
    
    /// Predefined text styles for common use cases
    enum TextStyle {
        /// Style for main headings
        static let heading = Font.system(size: FontSize.xxl, weight: FontWeight.bold)
        
        /// Style for subheadings
        static let subheading = Font.system(size: FontSize.xl, weight: FontWeight.semibold)
        
        /// Style for body text
        static let body = Font.system(size: FontSize.base, weight: FontWeight.regular)
        
        /// Style for captions and helper text
        static let caption = Font.system(size: FontSize.sm, weight: FontWeight.regular)
        
        /// Style for buttons and calls to action
        static let button = Font.system(size: FontSize.base, weight: FontWeight.semibold)
        
        /// Style for numerical data
        static let number = Font.monospacedDigit(size: FontSize.base, weight: FontWeight.medium)
    }
}

// MARK: - View Modifiers

extension View {
    /// Applies heading style to text
    func headingStyle() -> some View {
        self.font(AppTypography.TextStyle.heading)
    }
    
    /// Applies subheading style to text
    func subheadingStyle() -> some View {
        self.font(AppTypography.TextStyle.subheading)
    }
    
    /// Applies body style to text
    func bodyStyle() -> some View {
        self.font(AppTypography.TextStyle.body)
    }
    
    /// Applies caption style to text
    func captionStyle() -> some View {
        self.font(AppTypography.TextStyle.caption)
    }
    
    /// Applies button style to text
    func buttonStyle() -> some View {
        self.font(AppTypography.TextStyle.button)
    }
    
    /// Applies number style to text
    func numberStyle() -> some View {
        self.font(AppTypography.TextStyle.number)
    }
}

// MARK: - Dynamic Type Support

extension AppTypography {
    /// Scales for Dynamic Type support
    enum DynamicTypeSize {
        /// Minimum supported text size scale
        static let minimum: CGFloat = 0.8
        
        /// Maximum supported text size scale
        static let maximum: CGFloat = 1.5
    }
}

// MARK: - Preview Examples

#if DEBUG
struct TypographyPreview: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text("Heading Example")
                .headingStyle()
            
            Text("Subheading Example")
                .subheadingStyle()
            
            Text("Body text example with multiple lines to demonstrate the line height and general appearance of longer content blocks.")
                .bodyStyle()
            
            Text("Caption text example")
                .captionStyle()
            
            Text("Button Label")
                .buttonStyle()
            
            Text("12.345")
                .numberStyle()
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
