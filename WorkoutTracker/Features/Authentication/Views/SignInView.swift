import SwiftUI
import AuthenticationServices

struct SignInView: View {
    // MARK: - Environment
    @EnvironmentObject private var appState: AppState
    @Environment(\.colorScheme) private var colorScheme
    
    // MARK: - State
    @StateObject private var viewModel = SignInViewModel()
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 24) {
                    // App Logo/Branding
                    Image("AppLogo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 120, height: 120)
                        .padding(.top, 40)
                    
                    // Title
                    Text("Welcome Back")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    // Email Sign In Form
                    emailSignInForm
                    
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
                            await viewModel.handleAppleSignIn(result)
                        }
                    }
                    .frame(height: 50)
                    .padding(.horizontal)
                    
                    // Sign Up Link
                    HStack {
                        Text("Don't have an account?")
                            .foregroundColor(.gray)
                        NavigationLink("Sign Up", destination: SignUpView())
                            .foregroundColor(.blue)
                    }
                    .padding(.top)
                }
                .padding()
            }
            .navigationBarHidden(true)
            .alert("Error", isPresented: $showingAlert) {
                Button("OK", role: .cancel) {}
            } message: {
                Text(alertMessage)
            }
        }
        .onReceive(viewModel.$error) { error in
            if let error = error {
                alertMessage = error.localizedDescription
                showingAlert = true
            }
        }
    }
    
    // MARK: - Email Sign In Form
    private var emailSignInForm: some View {
        VStack(spacing: 16) {
            // Email Field
            TextField("Email", text: $viewModel.email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.emailAddress)
                .keyboardType(.emailAddress)
                .autocapitalization(.none)
                .disableAutocorrection(true)
            
            // Password Field
            SecureField("Password", text: $viewModel.password)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .textContentType(.password)
            
            // Sign In Button
            Button(action: {
                Task {
                    await viewModel.signIn()
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
                    Text("Sign In")
                        .fontWeight(.semibold)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.blue)
                        .cornerRadius(10)
                }
            }
            .disabled(viewModel.isLoading || !viewModel.isValid)
            
            // Forgot Password Link
            Button("Forgot Password?") {
                viewModel.forgotPassword()
            }
            .foregroundColor(.blue)
        }
        .padding(.horizontal)
    }
}

// MARK: - Preview Provider
struct SignInView_Previews: PreviewProvider {
    static var previews: some View {
        SignInView()
            .environmentObject(AppState())
    }
}

// MARK: - View Model
final class SignInViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var email = ""
    @Published var password = ""
    @Published var isLoading = false
    @Published var error: Error?
    
    // MARK: - Computed Properties
    var isValid: Bool {
        !email.isEmpty && email.contains("@") && password.count >= 6
    }
    
    // MARK: - Methods
    func signIn() async {
        guard isValid else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            // TODO: Implement actual sign in logic
            // This would typically involve your authentication service
            throw NSError(domain: "Not Implemented", code: -1, userInfo: nil)
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
    
    func handleAppleSignIn(_ result: Result<ASAuthorization, Error>) async {
        do {
            let authorization = try result.get()
            guard let credential = authorization.credential as? ASAuthorizationAppleIDCredential else {
                throw NSError(domain: "Invalid Credential", code: -1, userInfo: nil)
            }
            
            // TODO: Implement Apple Sign In logic
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
    
    func forgotPassword() {
        // TODO: Implement forgot password logic
        // This would typically navigate to a password reset flow
    }
}
