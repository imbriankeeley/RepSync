// File: WorkoutTracker/App/WorkoutTrackerApp.swift
import SwiftUI
import CoreData

@main
struct WorkoutTrackerApp: App {
    // MARK: - Properties
    @StateObject private var appState = AppState()
    let persistenceController = PersistenceController.shared
    
    // MARK: - Environment Objects
    private let cloudSyncService: CloudSyncServiceProtocol
    private let localStorageService: LocalStorageServiceProtocol
    private let encryptionService: EncryptionServiceProtocol
    
    // MARK: - Initialization
    init() {
        // Initialize core services
        cloudSyncService = CloudSyncService()
        localStorageService = LocalStorageService()
        encryptionService = EncryptionService()
        
        // Configure appearance
        configureAppearance()
    }
    
    // MARK: - App Scene
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
                .environmentObject(appState)
                .task {
                    await setupApp()
                }
        }
    }
    
    // MARK: - Private Methods
    private func configureAppearance() {
        // Set up default appearance for navigation bars, etc.
        let appearance = UINavigationBarAppearance()
        appearance.configureWithDefaultBackground()
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
    
    private func setupApp() async {
        // Check authentication status
        await appState.checkAuthStatus()
        
        // Initialize sync if user is authenticated
        if appState.isAuthenticated {
            do {
                try await cloudSyncService.sync()
            } catch {
                // Handle sync error appropriately
                appState.showError(error)
            }
        }
    }
}

// MARK: - App State
class AppState: ObservableObject {
    @Published var isAuthenticated = false
    @Published var errorMessage: String?
    @Published var showError = false
    
    func checkAuthStatus() async {
        // Implement authentication check logic
        // This would typically check keychain, biometrics, etc.
    }
    
    func showError(_ error: Error) {
        DispatchQueue.main.async {
            self.errorMessage = error.localizedDescription
            self.showError = true
        }
    }
}

// MARK: - Persistence Controller
struct PersistenceController {
    static let shared = PersistenceController()
    
    let container: NSPersistentCloudKitContainer
    
    init(inMemory: Bool = false) {
        container = NSPersistentCloudKitContainer(name: "WorkoutTracker")
        
        if inMemory {
            container.persistentStoreDescriptions.first?.url = URL(fileURLWithPath: "/dev/null")
        }
        
        container.loadPersistentStores { description, error in
            if let error = error {
                fatalError("Error: \(error.localizedDescription)")
            }
        }
        
        container.viewContext.automaticallyMergesChangesFromParent = true
        container.viewContext.mergePolicy = NSMergeByPropertyObjectTrumpMergePolicy
    }
}
