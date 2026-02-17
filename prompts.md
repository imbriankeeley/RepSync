# RepSync — Claude Code Prompt Guide

Use this guide to implement RepSync step-by-step in Claude Code. **Always keep `docs/plan.md` open or in context** — it is the single source of truth. Design assets are in the repo root: `repSyncLogo.png`, `IMG_1505.PNG` through `IMG_1538.PNG`; match layouts and theme to those screens.

Copy the prompt for each phase into Claude Code. Each prompt is self-contained and includes requirements and deliverables for that phase.

---

## How to Use

1. Start with **Phase 1** and work in order; later phases depend on earlier ones.
2. Before each phase, ensure the repo has the structure and files from previous phases.
3. After each phase, verify the checklist items for that phase before moving on.
4. If you use Compose, stick to Compose for all UI; if you use Views/Fragments, stick to that. Do not mix.

---

## Phase 1: Android Project Setup & Theme

**Paste this prompt:**

```
I'm building RepSync, an offline-first Android workout app. Use docs/plan.md as the single source of truth.

Do Phase 1 only:

1. Create the Android project structure as in plan.md Section 10:
   - Kotlin, Gradle (Kotlin DSL preferred). Min SDK 21+ (or 24+), target latest stable.
   - Single-activity with fragments OR Jetpack Compose for all screens — choose one and state which.
   - Repo root: app/ module, build.gradle, settings.gradle. No iOS or cloud.

2. Define the theme to match the design assets (IMG_1505.PNG through IMG_1538.PNG in repo):
   - Background: dark charcoal / near-black (#1a1a1a or similar).
   - Cards & inputs: light grey rounded rectangles; text on cards dark grey/black.
   - Primary actions: muted sage/olive green (e.g. #8DAF8E, #7E9D7C) — buttons like "Add Exercise", "Finish Workout", "Quick Go", "Start Workout", checkmarks.
   - Destructive/cancel: muted red/pink for "Cancel" and header "X".
   - Text on dark: white or very light grey.
   - Corners: 12–16dp for cards, 8dp for buttons/inputs.
   - Typography: clean sans-serif; clear hierarchy (title bold, labels smaller).
   Put these in res/values/colors.xml and themes.xml (or Compose theme). Keep the UI minimal and match the reference images.

3. Use repSyncLogo.png for the launcher icon (configure in manifest and res).

4. Add a minimal README.md at repo root with:
   - One-line project description.
   - How to build the APK (e.g. ./gradlew assembleRelease).
   - Where the output is (e.g. app/build/outputs/apk/release/app-release.apk).
   - Note that GitHub Releases + Obtainium will be used for distribution (details in a later phase).

Do not implement any screens or data yet — only project skeleton and theme. Confirm architecture choice (Compose vs Fragments) in your reply.
```

**Done when:** Project builds; theme colors and logo are applied; README has build instructions.

---

## Phase 2: Data Layer (Room & Models)

**Paste this prompt:**

```
RepSync app — follow docs/plan.md. Phase 1 (project + theme) is done. Do Phase 2 only: local data layer.

1. Add Room (and Kotlin coroutines/Flow if using) to app/build.gradle. No cloud, no backend.

2. Define entities and DB per plan.md Section 4:
   - Workout (template): id, name, createdAt, list of exercises (order preserved — use orderIndex or similar).
   - Exercise (within a workout): id, name, workoutId (or template id), orderIndex, list of sets.
   - Set: id, exerciseId, orderIndex, previousWeight (optional), previousReps (optional), weight (+lbs), reps.
   - CompletedWorkout: id, name (or template id), date (calendar day), startedAt, endedAt (null if abandoned), list of exercises with sets and logged values (weight, reps). Design so we can show "Previous" (last time this exercise was done) in future sessions.
   - Support "Quick workout": same as CompletedWorkout with a flag or naming (e.g. name "Quick Workout" + date) so it can be found and optionally "Save as template" / copy to another day later.

3. User model (plan.md Section 3): Guest by default. Optional local profile: display name, optional avatar (stored on device). No email/password or cloud. Store in Room or SharedPreferences/DataStore — your choice.

4. Create DAOs and the Room Database class. Single source of truth for workouts, exercises, sets, completed workouts, and user profile.

5. Provide a way to query "previous" for an exercise: last logged weight/reps for that exercise from CompletedWorkout history (e.g. "135 lb x 5").

Do not build UI screens yet. Ensure the project still builds and all entities/DAOs are testable (e.g. from a simple test or preview).
```

**Done when:** Room DB builds; all entities and DAOs exist; "previous" for an exercise can be queried; user (Guest + optional profile) is modeled.

---

## Phase 3: Home Screen & Navigation

**Paste this prompt:**

```
RepSync — docs/plan.md is the spec. Phases 1 and 2 are done (project, theme, Room + models). Do Phase 3 only: Home screen and app navigation.

1. Set up navigation (Navigation Component or Compose Navigation) so we have:
   - Home (default)
   - Profile
   - Bottom nav: Home | Profile (active tab indicated). Match design assets for layout.

2. Home screen (plan.md Section 5.1):
   - Calendar: month view (e.g. "October 2024"), arrows to change month. Days with at least one completed workout get a muted green highlight. Tapping a day should eventually open "Day view" (you can use a placeholder or simple toast for now; full Day view is Phase 8).
   - Two main buttons: "Workouts" (navigates to Workouts list) and "Quick Go" (starts a Quick Workout — can navigate to a placeholder screen for now; we'll implement in Phase 6).
   - Bottom nav: Home | Profile. Match theme (dark background, rounded cards, muted green for primary buttons).

3. Use ViewModel + LiveData or StateFlow for UI state. Single activity; stick to the same UI approach (Compose or Fragments) chosen in Phase 1.

4. Wire calendar to real data: days with completed workouts (from Room) must show the muted green highlight.

Implement only Home + bottom nav + navigation to Workouts and to a placeholder for Quick Go. Do not build Workouts list, Profile, or Active Workout yet.
```

**Done when:** Home shows calendar with green dots for days with workouts; Workouts and Quick Go buttons navigate correctly; bottom nav switches between Home and Profile (Profile can be a placeholder screen).

---

## Phase 4: Workouts List, Detail & New Workout (Templates)

**Paste this prompt:**

```
RepSync — follow docs/plan.md. Phases 1–3 are done. Do Phase 4 only: Workouts list, workout detail, and New Workout (create/edit template).

1. Workouts list (plan.md Section 5.2):
   - Header: Back (to Home), title "Workouts", optional search icon (filter by name).
   - List: all saved workout templates from Room. Tapping a row opens workout detail.
   - FAB or "+" button: Create "New Workout" → go to New Workout screen.

2. Workout detail (modal or screen):
   - Show workout name and list of exercises (e.g. "2 x Bench Press", "2 x Chest Press").
   - Primary button: "Start Workout" (muted green). Close (X) to dismiss. Starting workout goes to Active Workout screen (can be placeholder for now; we implement in Phase 5).

3. New Workout screen (plan.md Section 5.3) — create/edit template:
   - Header: Back, "New Workout", "Save" (persists template locally).
   - Name: single input, e.g. "Name: Push" (or "Name: [input]").
   - Exercises: for each exercise, a card with:
     - Exercise name (editable).
     - Table: Set | Previous | +lbs | Reps.
     - "+ Add Set" for that exercise.
     - "Add Exercise" (green) to add another exercise to the workout.
   - Previous: show last logged weight/reps for that exercise from history (use the "previous" query from Phase 2), if any.
   - Save: store the template in Room; user can start it later from Workouts.

Match design assets: dark theme, light grey cards, muted green for "Add Exercise" and primary actions, rounded corners. No cloud; all local.
```

**Done when:** Workouts list shows templates; search (optional) works; tap row → detail → Start Workout; New Workout allows name + exercises + sets with Previous column and Save; data persists in Room.

---

## Phase 5: Active Workout (from Template)

**Paste this prompt:**

```
RepSync — docs/plan.md is the spec. Phases 1–4 are done. Do Phase 5 only: Active Workout screen when starting from a template.

1. Active Workout screen (plan.md Section 5.4):
   - Header: Timer (e.g. stopwatch icon + "00:01"), workout name (e.g. "Chest Workout"), X (close) on the right.
   - Timer: elapsed time (stopwatch). Optional: small control or settings for rest timer or target duration — keep it simple per design.
   - Body: one section per exercise with table: Set, Previous, +lbs, Reps. "+ Add Set" per exercise. Green checkmark when set is completed (if in design).
   - Actions: "Add Exercise" (green), then "Finish Workout" (green, full-width).
   - Close (X): show dialog "Cancel Workout?" with "Resume" and "Cancel". Cancel discards the session (do not save to calendar). Resume returns to workout.
   - Finish Workout: show dialog "Finish Workout?" with "Cancel" and "Finish". On Finish: save as CompletedWorkout for today, update profile workout count, then return to Home (or Calendar).

2. Wire "Start Workout" from Workout detail (Phase 4) to this Active Workout screen with the selected template loaded. Pre-fill exercises and sets from the template; "Previous" from history.

3. Persist in-progress state if needed (e.g. so closing the app doesn't lose the session — optional for MVP; at minimum, finishing or canceling must behave as above).

Match design: muted green for primary actions, destructive red/pink for Cancel/X, dark background, rounded cards. All local; no cloud.
```

**Done when:** Starting a template opens Active Workout with timer and exercise/set table; Add Set and Add Exercise work; Cancel Workout dialog (Resume/Cancel) and Finish Workout dialog (Cancel/Finish) work; on Finish, data saves to Room and profile count updates; user returns to Home.

---

## Phase 6: Quick Workout

**Paste this prompt:**

```
RepSync — follow docs/plan.md. Phases 1–5 are done. Do Phase 6 only: Quick Workout flow.

1. Quick Workout (plan.md Section 5.5):
   - Same UI as Active Workout, but title is "Quick Workout" and there are no pre-filled exercises. User adds all exercises via "Add Exercise".
   - Timer, Add Set, Add Exercise, Finish Workout, and Close (X) with "Cancel Workout?" behave the same as in Phase 5.
   - On Finish: save as a completed session for today (CompletedWorkout with Quick Workout naming/flag). Optionally allow "Save as template" in a later phase; for now just save the completed workout.

2. Wire the "Quick Go" button on Home to open this Quick Workout screen (replace any placeholder from Phase 3).

3. Reuse the same Active Workout screen/logic where possible; differentiate only by "from template" vs "quick" (no template).

Match theme and behavior to plan.md and design assets. No cloud.
```

**Done when:** Quick Go opens Quick Workout; user can add exercises and sets, finish or cancel; completed Quick Workout is saved for today and appears in history/calendar.

---

## Phase 7: Profile Screen

**Paste this prompt:**

```
RepSync — docs/plan.md is the spec. Phases 1–6 are done. Do Phase 7 only: Profile screen.

1. Profile screen (plan.md Section 5.6):
   - Header: "Profile".
   - Card: profile icon (or placeholder), "Guest" (or profile name if set), "X Workouts" (completed workout count from Room). Chevron (or tap) to open profile/settings.
   - Bottom nav: Home | Profile (Profile selected when on this screen).

2. Profile/settings (plan.md Section 3): local only — e.g. display name, optional avatar (stored on device). No email/password or cloud. "Create profile" or "Set up profile" from here only affects local identity.

3. Workout count must reflect total completed workouts (from CompletedWorkout); update when user finishes a workout (already done in Phase 5/6).

Match design: dark background, light grey card, muted green if needed for actions. No sign-up or sync.
```

**Done when:** Profile shows Guest or name and completed workout count; tapping the card opens profile/settings where user can set name and optionally avatar; data is local only.

---

## Phase 8: Calendar Day View & Copy Workout

**Paste this prompt:**

```
RepSync — follow docs/plan.md. Phases 1–7 are done. Do Phase 8 only: Calendar day view and copy/create-from-day.

1. Day view (plan.md Section 5.7):
   - From Home, tapping a calendar day opens Day view for that date.
   - Day view: list of workouts completed (or started) that day. For each: name, duration, list of exercises/sets if desired.
   - Option to "Copy to another day": pick target date and create a copy (e.g. as a new CompletedWorkout or template for that day — per plan, "copy workouts from previous days to any other day").
   - Option to "Create workout from a day" / "Use as template": save that day's workout as a reusable template (so user can use it from Workouts list later). This includes quick workouts they liked.

2. Implement "Copy to another day" and "Save as template" (from day view and, if you added a placeholder, from Quick Workout completion) so the user can:
   - Copy a workout from one day to another.
   - Create a new workout template from a past day (including quick workouts).

Match design and theme. All local; no cloud.
```

**Done when:** Tapping a day opens Day view with list of workouts; user can copy a workout to another day and create a template from a day's workout; Quick Workout can be saved as template if desired.

---

## Phase 9: README, Build & Obtainium Distribution

**Paste this prompt:**

```
RepSync — docs/plan.md is the spec. Phases 1–8 are done. Do Phase 9 only: README and distribution.

1. Update README.md at repo root to include:
   - Short project description (offline-first Android workout app).
   - How to build the APK (e.g. ./gradlew assembleRelease).
   - Where the output is (e.g. app/build/outputs/apk/release/app-release.apk).
   - How to create a GitHub Release and upload the APK so Obtainium can install/update the app from this repo (plan.md Section 9): add source in Obtainium with GitHub repo URL; Obtainium looks for latest release and APK/AAB asset.
   - Optional: signing for release (e.g. how to configure signing in build.gradle for release builds).

2. Ensure the project produces a release APK (or AAB) that can be installed on a device. If signing is required, document the steps (without committing keystore or passwords).

3. Confirm repo structure matches plan.md Section 10 (app/, plan.md, logo, design assets, README). Do not delete design reference images.

Do not add Spotify or cloud features; plan.md Section 7 says Spotify is future only.
```

**Done when:** README has full build and Obtainium release instructions; release APK builds successfully; repo structure is correct.

---

## Full MVP Checklist (from plan.md Section 6)

After all phases, confirm:

- [ ] Guest by default; optional local profile (name, avatar).
- [ ] All data stored only on device (Room).
- [ ] Home: calendar with green dots for days with workouts; Workouts + Quick Go buttons; bottom nav Home | Profile.
- [ ] Workouts: list templates, search (optional), FAB "+", tap row → detail → Start Workout.
- [ ] New Workout: name, add exercises, sets (Previous, +lbs, Reps), Save.
- [ ] Active workout: elapsed timer, optional set timer, add set, add exercise, finish workout, close (X) with "Cancel Workout?" (Resume / Cancel).
- [ ] "Finish Workout?" dialog (Cancel / Finish); on Finish, save to history and increment profile count.
- [ ] Quick Workout: same as active workout, no pre-filled exercises.
- [ ] Profile: show Guest or name, completed workout count, entry to profile/settings.
- [ ] Calendar: tap day → day view; view past workouts; copy workout from one day to another; create template from a day's workout.

---

## One-Shot Full Build Prompt (Optional)

If you prefer to give Claude the whole scope in one go (e.g. in a new chat with plan.md and this file), use the following. Prefer the phased prompts above for better control and debugging.

```
Build RepSync from scratch using docs/plan.md as the single source of truth. Match the design assets in the repo (repSyncLogo.png, IMG_1505.PNG–IMG_1538.PNG): dark theme, muted sage green primary actions, light grey cards, rounded corners, clean typography.

Requirements:
- Android only (Kotlin, Gradle). Min SDK 21+ or 24+, target latest. Single-activity; choose Jetpack Compose OR Fragments and use consistently.
- Local only: Room for workouts, exercises, sets, completed workouts; no cloud, no account required. Guest by default; optional local profile (name, avatar).
- Implement all flows in plan.md Sections 5.1–5.7: Home (calendar, Workouts, Quick Go), Workouts list + detail + New Workout, Active Workout (timer, sets, Finish/Cancel dialogs), Quick Workout, Profile, Calendar day view with copy-to-day and create-template-from-day.
- Feature checklist: plan.md Section 6 (all items). No Spotify in v1 (Section 7).
- README: how to build APK and distribute via GitHub Releases + Obtainium (Section 9). Repo structure per Section 10.

Deliver a buildable Android app that can be installed on a device and updated via Obtainium.
```

---

*End of prompt guide. Keep docs/plan.md and design assets in context for every phase.*
