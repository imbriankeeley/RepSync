import SwiftUI
import AuthenticationServices

struct SignUpView: View {
    // MARK: - Environment
    @EnvironmentObject private var appState: AppState
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) private var colorScheme
    
    // MARK: - State
    @StateObject private var viewModel = SignUpViewModel()
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    // MARK: - Body
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // App Logo/Branding
                Image("AppLogo")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 120, height: 120)
                    .padding(.top, 40)
                
                // Title
                Text("Create Account")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                // Sign Up Form
                signUpForm
                
                // Divider
                HStack {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(height: 1)
                    Text("or")
                        .foregroundColor(.gray)
                        .padding(.horizontal)
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(height: 1)
                }
                .padding(.horizontal)
                
                // Sign in with Apple Button
                SignInWithAppleButton { request in
                    request.requestedScopes = [.email]
                } onCompletion: { result in
                    Task {
                        await viewModel.handleAppleSignUp(result)
                    }
                }
                .frame(height: 50)
                .padding(.horizontal)
                
                // Terms and Privacy Policy
                termsAndPrivacyView
                
                // Sign In Link
                HStack {
                    Text("Already have an account?")
                        .foregroundColor(.gray)
                    Button("Sign In") {
                        dismiss()
                    }
                    .foregroundColor(.blue)
                }
                .padding(.top)
            }
            .padding()
        }
        .navigationBarTitleDisplayMode(.inline)
        .alert("Error", isPresented: $showingAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(alertMessage)
        }
        .onReceive(viewModel.$error) { error in
            if let error = error {
                alertMessage = error.localizedDescription
                showingAlert = true
            }
        }
    }
    
    // MARK: - Sign Up Form
    private var signUpForm: some View {
        VStack(spacing: 16) {
            // Name Fields
            TextField("First Name", text: $viewModel.firstName)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.givenName)
                .autocapitalization(.words)
            
            TextField("Last Name", text: $viewModel.lastName)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.familyName)
                .autocapitalization(.words)
            
            // Email Field
            TextField("Email", text: $viewModel.email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.emailAddress)
                .keyboardType(.emailAddress)
                .autocapitalization(.none)
                .disableAutocorrection(true)
            
            // Password Fields
            SecureField("Password", text: $viewModel.password)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.newPassword)
            
            SecureField("Confirm Password", text: $viewModel.confirmPassword)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.newPassword)
            
            // Sign Up Button
            Button(action: {
                Task {
                    await viewModel.signUp()
                }
            }) {
                if viewModel.isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.blue)
                        .cornerRadius(10)
                } else {
                    Text("Create Account")
                        .fontWeight(.semibold)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.blue)
                        .cornerRadius(10)
                }
            }
            .disabled(viewModel.isLoading || !viewModel.isValid)
        }
        .padding(.horizontal)
    }
    
    // MARK: - Terms and Privacy View
    private var termsAndPrivacyView: some View {
        VStack(spacing: 8) {
            Text("By creating an account, you agree to our")
                .foregroundColor(.gray)
            HStack(spacing: 4) {
                Button("Terms of Service") {
                    viewModel.showTerms()
                }
                Text("and")
                    .foregroundColor(.gray)
                Button("Privacy Policy") {
                    viewModel.showPrivacyPolicy()
                }
            }
        }
        .font(.footnote)
    }
}

// MARK: - Preview Provider
struct SignUpView_Previews: PreviewProvider {
    static var previews: some View {
        SignUpView()
            .environmentObject(AppState())
    }
}

// MARK: - View Model
final class SignUpViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var firstName = ""
    @Published var lastName = ""
    @Published var email = ""
    @Published var password = ""
    @Published var confirmPassword = ""
    @Published var isLoading = false
    @Published var error: Error?
    
    // MARK: - Computed Properties
    var isValid: Bool {
        !firstName.isEmpty &&
        !lastName.isEmpty &&
        !email.isEmpty &&
        email.contains("@") &&
        password.count >= 8 &&
        password == confirmPassword &&
        isPasswordValid(password)
    }
    
    // MARK: - Methods
    func signUp() async {
        guard isValid else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            // TODO: Implement actual sign up logic
            // This would typically involve your authentication service
            throw NSError(domain: "Not Implemented", code: -1, userInfo: nil)
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
    
    func handleAppleSignUp(_ result: Result<ASAuthorization, Error>) async {
        do {
            let authorization = try result.get()
            guard let credential = authorization.credential as? ASAuthorizationAppleIDCredential else {
                throw NSError(domain: "Invalid Credential", code: -1, userInfo: nil)
            }
            
            // TODO: Implement Apple Sign Up logic
            // This would typically involve your authentication service
            print("User ID: \(credential.user)")
            if let email = credential.email {
                print("Email: \(email)")
            }
            
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
    
    func showTerms() {
        // TODO: Implement terms of service navigation
    }
    
    func showPrivacyPolicy() {
        // TODO: Implement privacy policy navigation
    }
    
    // MARK: - Private Methods
    private func isPasswordValid(_ password: String) -> Bool {
        // Password must be at least 8 characters long and contain:
        // - At least one uppercase letter
        // - At least one lowercase letter
        // - At least one number
        let passwordRegex = NSPredicate(format: "SELF MATCHES %@",
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        return passwordRegex.evaluate(with: password)
    }
}
