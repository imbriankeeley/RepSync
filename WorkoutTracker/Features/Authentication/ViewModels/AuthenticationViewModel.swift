import Foundation
import AuthenticationServices
import Combine

/// Possible authentication states
enum AuthenticationState {
    case unauthenticated
    case authenticating
    case authenticated
}

/// Authentication-related errors
enum AuthenticationError: LocalizedError {
    case invalidCredentials
    case networkError(Error)
    case invalidAppleSignIn
    case userCancelled
    case unknown(Error)
    
    var errorDescription: String? {
        switch self {
        case .invalidCredentials:
            return "Invalid email or password"
        case .networkError(let error):
            return "Network error: \(error.localizedDescription)"
        case .invalidAppleSignIn:
            return "Apple Sign In failed"
        case .userCancelled:
            return "Authentication cancelled by user"
        case .unknown(let error):
            return "An error occurred: \(error.localizedDescription)"
        }
    }
}

/// Protocol defining authentication service capabilities
protocol AuthenticationServiceProtocol {
    func signIn(email: String, password: String) async throws
    func signUp(firstName: String, lastName: String, email: String, password: String) async throws
    func signInWithApple(credential: ASAuthorizationAppleIDCredential) async throws
    func signOut() async throws
    func resetPassword(email: String) async throws
}

/// Main authentication view model responsible for handling all authentication flows
@MainActor
final class AuthenticationViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published private(set) var authenticationState: AuthenticationState = .unauthenticated
    @Published private(set) var error: AuthenticationError?
    @Published private(set) var isLoading = false
    
    // MARK: - Private Properties
    private let authService: AuthenticationServiceProtocol
    private var cancellables = Set<AnyCancellable>()
    
    // MARK: - Initialization
    init(authService: AuthenticationServiceProtocol) {
        self.authService = authService
    }
    
    // MARK: - Public Methods
    
    /// Signs in a user with email and password
    func signIn(email: String, password: String) async {
        guard !isLoading else { return }
        
        isLoading = true
        authenticationState = .authenticating
        
        do {
            try await authService.signIn(email: email, password: password)
            authenticationState = .authenticated
        } catch {
            handleAuthenticationError(error)
            authenticationState = .unauthenticated
        }
        
        isLoading = false
    }
    
    /// Signs up a new user
    func signUp(firstName: String, lastName: String, email: String, password: String) async {
        guard !isLoading else { return }
        
        isLoading = true
        authenticationState = .authenticating
        
        do {
            try await authService.signUp(
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password
            )
            authenticationState = .authenticated
        } catch {
            handleAuthenticationError(error)
            authenticationState = .unauthenticated
        }
        
        isLoading = false
    }
    
    /// Handles Apple Sign In
    func handleAppleSignIn(_ result: Result<ASAuthorization, Error>) async {
        guard !isLoading else { return }
        
        isLoading = true
        authenticationState = .authenticating
        
        do {
            let authorization = try result.get()
            guard let credential = authorization.credential as? ASAuthorizationAppleIDCredential else {
                throw AuthenticationError.invalidAppleSignIn
            }
            
            try await authService.signInWithApple(credential: credential)
            authenticationState = .authenticated
        } catch {
            handleAuthenticationError(error)
            authenticationState = .unauthenticated
        }
        
        isLoading = false
    }
    
    /// Signs out the current user
    func signOut() async {
        guard !isLoading else { return }
        
        isLoading = true
        
        do {
            try await authService.signOut()
            authenticationState = .unauthenticated
        } catch {
            handleAuthenticationError(error)
        }
        
        isLoading = false
    }
    
    /// Initiates password reset process
    func resetPassword(email: String) async {
        guard !isLoading else { return }
        
        isLoading = true
        
        do {
            try await authService.resetPassword(email: email)
        } catch {
            handleAuthenticationError(error)
        }
        
        isLoading = false
    }
    
    /// Clears any current error
    func clearError() {
        error = nil
    }
    
    // MARK: - Private Methods
    
    private func handleAuthenticationError(_ error: Error) {
        if let authError = error as? AuthenticationError {
            self.error = authError
        } else if let asAuthError = error as? ASAuthorizationError {
            switch asAuthError.code {
            case .canceled:
                self.error = .userCancelled
            default:
                self.error = .unknown(error)
            }
        } else {
            self.error = .unknown(error)
        }
    }
}

// MARK: - Preview Helpers

#if DEBUG
class MockAuthenticationService: AuthenticationServiceProtocol {
    func signIn(email: String, password: String) async throws {
        try await Task.sleep(nanoseconds: 1_000_000_000)
        // Simulate successful sign in
    }
    
    func signUp(firstName: String, lastName: String, email: String, password: String) async throws {
        try await Task.sleep(nanoseconds: 1_000_000_000)
        // Simulate successful sign up
    }
    
    func signInWithApple(credential: ASAuthorizationAppleIDCredential) async throws {
        try await Task.sleep(nanoseconds: 1_000_000_000)
        // Simulate successful Apple sign in
    }
    
    func signOut() async throws {
        try await Task.sleep(nanoseconds: 1_000_000_000)
        // Simulate successful sign out
    }
    
    func resetPassword(email: String) async throws {
        try await Task.sleep(nanoseconds: 1_000_000_000)
        // Simulate successful password reset
    }
}

extension AuthenticationViewModel {
    static var preview: AuthenticationViewModel {
        AuthenticationViewModel(authService: MockAuthenticationService())
    }
}
#endif
