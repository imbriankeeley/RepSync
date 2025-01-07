import UIKit
import SwiftUI
import CoreData

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?
    private let persistenceController = PersistenceController.shared
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = scene as? UIWindowScene else { return }
        
        // Create the SwiftUI view and set the context as the value for the managedObjectContext environment keyPath.
        let contentView = ContentView()
            .environment(\.managedObjectContext, persistenceController.container.viewContext)
            .environmentObject(AppState())
        
        // Use a UIHostingController as window root view controller.
        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = UIHostingController(rootView: contentView)
        self.window = window
        window.makeKeyAndVisible()
        
        // Handle any URL context from connection options
        if let urlContext = connectionOptions.urlContexts.first {
            handleIncomingURL(urlContext.url)
        }
    }
    
    func sceneDidDisconnect(_ scene: UIScene) {
        // Called when the scene is being released by the system
        // Save any pending changes to CoreData
        saveContext()
    }
    
    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state
        // Reset the badge count when app becomes active
        UIApplication.shared.applicationIconBadgeNumber = 0
    }
    
    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will resign active state
        // Perform any cleanup or state preservation
        saveContext()
    }
    
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called when the scene moves to the background
        // Save data and trigger background tasks if needed
        saveContext()
        
        // Trigger background sync if needed
        triggerBackgroundSync()
    }
    
    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called when the scene moves from background to foreground
        // Refresh data and UI state if needed
        refreshApplicationState()
    }
    
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        // Handle incoming URLs (e.g., deep links)
        guard let url = URLContexts.first?.url else { return }
        handleIncomingURL(url)
    }
    
    // MARK: - Private Methods
    
    private func saveContext() {
        let context = persistenceController.container.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                // Handle save error appropriately
                print("Error saving context: \(error)")
            }
        }
    }
    
    private func triggerBackgroundSync() {
        Task {
            do {
                let cloudSyncService = CloudSyncService()
                try await cloudSyncService.sync()
            } catch {
                print("Background sync failed: \(error)")
            }
        }
    }
    
    private func refreshApplicationState() {
        // Refresh app state when coming to foreground
        Task {
            do {
                let cloudSyncService = CloudSyncService()
                try await cloudSyncService.pullChanges()
            } catch {
                print("Failed to refresh application state: \(error)")
            }
        }
    }
    
    private func handleIncomingURL(_ url: URL) {
        // Handle deep linking logic here
        // This could include navigating to specific workouts, templates, etc.
        // Example: workouttracker://template/UUID
        
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true),
              let host = components.host else {
            return
        }
        
        switch host {
        case "template":
            if let templateId = components.queryItems?.first(where: { $0.name == "id" })?.value {
                // Handle template deep link
                print("Opening template with ID: \(templateId)")
            }
        case "workout":
            if let workoutId = components.queryItems?.first(where: { $0.name == "id" })?.value {
                // Handle workout deep link
                print("Opening workout with ID: \(workoutId)")
            }
        default:
            print("Unhandled deep link path: \(host)")
        }
    }
}
