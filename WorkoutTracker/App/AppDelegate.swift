import UIKit
import CloudKit
import CoreData

class AppDelegate: NSObject, UIApplicationDelegate {
    // MARK: - Properties
    private let cloudSyncService: CloudSyncServiceProtocol
    private let localStorageService: LocalStorageServiceProtocol
    
    // MARK: - Initialization
    override init() {
        self.cloudSyncService = CloudSyncService()
        self.localStorageService = LocalStorageService()
        super.init()
    }
    
    // MARK: - UIApplicationDelegate
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        // Configure UI appearance
        configureAppearance()
        
        // Register for remote notifications
        registerForRemoteNotifications(application)
        
        // Initialize CloudKit container
        initializeCloudKitContainer()
        
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        // Convert token to string for logging or debugging
        let tokenString = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        print("Successfully registered for notifications with token: \(tokenString)")
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for notifications: \(error.localizedDescription)")
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Trigger a background sync if needed
        Task {
            do {
                try await cloudSyncService.sync()
            } catch {
                print("Background sync failed: \(error.localizedDescription)")
            }
        }
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Perform any necessary cleanup
        saveContext()
    }
    
    // MARK: - Private Methods
    private func configureAppearance() {
        // Configure navigation bar appearance
        let navigationBarAppearance = UINavigationBarAppearance()
        navigationBarAppearance.configureWithDefaultBackground()
        UINavigationBar.appearance().standardAppearance = navigationBarAppearance
        UINavigationBar.appearance().compactAppearance = navigationBarAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navigationBarAppearance
        
        // Configure tab bar appearance
        let tabBarAppearance = UITabBarAppearance()
        tabBarAppearance.configureWithDefaultBackground()
        UITabBar.appearance().standardAppearance = tabBarAppearance
        UITabBar.appearance().scrollEdgeAppearance = tabBarAppearance
    }
    
    private func registerForRemoteNotifications(_ application: UIApplication) {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            guard granted else {
                print("Notification authorization denied")
                return
            }
            
            DispatchQueue.main.async {
                application.registerForRemoteNotifications()
            }
        }
    }
    
    private func initializeCloudKitContainer() {
        // Initialize CloudKit container and verify permissions
        CKContainer.default().accountStatus { [weak self] status, error in
            if let error = error {
                print("CloudKit account status error: \(error.localizedDescription)")
                return
            }
            
            switch status {
            case .available:
                self?.setupCloudKitSubscriptions()
            case .noAccount:
                print("No iCloud account available")
            case .restricted:
                print("iCloud account is restricted")
            case .couldNotDetermine:
                print("Could not determine iCloud account status")
            @unknown default:
                print("Unknown iCloud account status")
            }
        }
    }
    
    private func setupCloudKitSubscriptions() {
        // Set up CloudKit database subscriptions for sync
        let subscription = CKDatabaseSubscription(subscriptionID: "workout-changes")
        let notificationInfo = CKSubscription.NotificationInfo()
        notificationInfo.shouldSendContentAvailable = true
        subscription.notificationInfo = notificationInfo
        
        CKContainer.default().privateCloudDatabase.save(subscription) { _, error in
            if let error = error {
                print("Failed to set up CloudKit subscription: \(error.localizedDescription)")
            }
        }
    }
    
    // MARK: - Core Data
    private func saveContext() {
        let context = PersistenceController.shared.container.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                print("Error saving context: \(error.localizedDescription)")
            }
        }
    }
}
