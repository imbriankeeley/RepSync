import SwiftUI
import CoreData

struct TemplateListView: View {
    // MARK: - Environment
    @Environment(\.managedObjectContext) private var viewContext
    @EnvironmentObject private var workoutManager: WorkoutManager
    
    // MARK: - State
    @StateObject private var viewModel = TemplateListViewModel()
    @State private var showingCreateSheet = false
    @State private var searchText = ""
    @State private var selectedTemplate: WorkoutTemplate?
    
    // MARK: - FetchRequest
    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \WorkoutTemplate.name, ascending: true)],
        animation: .default)
    private var templates: FetchedResults<WorkoutTemplate>
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            Group {
                if templates.isEmpty {
                    emptyStateView
                } else {
                    templateListView
                }
            }
            .navigationTitle("Templates")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showingCreateSheet = true
                    }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .searchable(text: $searchText, prompt: "Search templates")
            .sheet(isPresented: $showingCreateSheet) {
                TemplateCreateView()
            }
            .sheet(item: $selectedTemplate) { template in
                TemplateDetailView(template: template)
            }
        }
    }
    
    // MARK: - Subviews
    private var emptyStateView: some View {
        VStack(spacing: 16) {
            Spacer()
            
            Image(systemName: "doc.text")
                .font(.system(size: 60))
                .foregroundColor(.gray)
            
            Text("No Templates Yet")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text("Create workout templates to quickly start your routines")
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)
            
            Button(action: {
                showingCreateSheet = true
            }) {
                Text("Create Template")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.blue)
                    .cornerRadius(10)
            }
            .padding(.horizontal, 20)
            .padding(.top, 20)
            
            Spacer()
        }
        .padding()
    }
    
    private var templateListView: some View {
        List {
            ForEach(filteredTemplates) { template in
                TemplateRowView(template: template)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        selectedTemplate = template
                    }
            }
            .onDelete { indexSet in
                Task {
                    await viewModel.deleteTemplates(at: indexSet, from: templates)
                }
            }
        }
        .listStyle(.inset)
    }
    
    // MARK: - Computed Properties
    private var filteredTemplates: [WorkoutTemplate] {
        viewModel.filterTemplates(Array(templates), searchText: searchText)
    }
}

// MARK: - Supporting Views
struct TemplateRowView: View {
    let template: WorkoutTemplate
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Template Name
            Text(template.name)
                .font(.headline)
            
            // Exercise Summary
            HStack {
                Label("\(template.exercises.count) exercises", systemImage: "dumbbell.fill")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .foregroundColor(.gray)
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - View Model
final class TemplateListViewModel: ObservableObject {
    // MARK: - Published Properties
    @Published var isLoading = false
    @Published var error: Error?
    
    // MARK: - Methods
    func deleteTemplates(at indexSet: IndexSet, from templates: FetchedResults<WorkoutTemplate>) async {
        guard !isLoading else { return }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            // TODO: Implement template deletion logic
            // This should:
            // 1. Delete from Core Data
            // 2. Trigger cloud sync
            // 3. Clean up any related resources
        } catch {
            await MainActor.run {
                self.error = error
            }
        }
    }
    
    func filterTemplates(_ templates: [WorkoutTemplate], searchText: String) -> [WorkoutTemplate] {
        guard !searchText.isEmpty else { return templates }
        
        return templates.filter { template in
            template.name.localizedCaseInsensitiveContains(searchText) ||
            template.exercises.contains { exercise in
                exercise.exercise.name.localizedCaseInsensitiveContains(searchText)
            }
        }
    }
}

// MARK: - Preview Provider
struct TemplateListView_Previews: PreviewProvider {
    static var previews: some View {
        TemplateListView()
            .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
            .environmentObject(WorkoutManager())
    }
}
