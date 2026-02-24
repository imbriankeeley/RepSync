# RepSync Web — Claude Code Prompt Guide (iOS PWA)

Use this guide to implement the **web app** version of RepSync step-by-step in Claude Code. **Always keep `docs/plan-web.md` open or in context** — it is the single source of truth for the web build. The app must be **functionally identical** to the Android app; iOS users install it via **Add to Home Screen** in Safari.

Copy the prompt for each phase into Claude Code. Each prompt is self-contained and includes requirements and deliverables for that phase.

---

## Design Assets (where to look)

Same as the Android app. All design references live under **`assets/`**:

| Asset | Path | Use for |
|-------|------|--------|
| App logo | `assets/repSyncLogo.png` | PWA icons, in-app branding |
| Screen mocks | `assets/references/IMG_1505.PNG` through `assets/references/IMG_1538.PNG` | All screens: Home, Workouts, New Workout, active workout, Quick Workout, Profile, Calendar, dialogs |

- **Theme and layout:** Match colors, spacing, rounded corners, and typography to the reference screens. Use CSS variables; touch targets >= 44px for iOS.
- **plan-web.md Section 1** lists exact color values; the PNGs show layout and hierarchy.

---

## How to Use

1. Start with **Phase 1** and work in order; later phases depend on earlier ones.
2. Before each phase, ensure the repo has the structure and files from previous phases.
3. **For each phase, reference the assets listed in that phase's prompt** — open the relevant images to match layout and theme.
4. After each phase, verify the checklist items for that phase before moving on.
5. Use React + Vite + TypeScript as the stack.

---

## Phase 1: Web Project Setup, Theme & PWA Shell

**Paste this prompt:**

```
I'm building RepSync Web — a PWA that is functionally identical to the Android RepSync workout app. Use docs/plan-web.md as the single source of truth. The Android app is in the app/ directory for reference.

Design assets to reference: assets/repSyncLogo.png (logo), and assets/references/IMG_1505.PNG through IMG_1538.PNG (all screen mocks for theme, colors, and layout). Open these and match theme/layout to them.

Do Phase 1 only:

1. Create the web project using Vite + React + TypeScript. Place it in a web/ folder (plan-web.md Section 11). No backend; static build only.

2. Define the theme in CSS variables to match the design assets (plan-web.md Section 1):
   - Background: pure black #000000 (outer), dark charcoal #1A1A1A (surface), #2C2C2E (cards).
   - Elevated cards: #3A3A3C. Input backgrounds: #48484A.
   - Primary actions: muted sage green #8DAF8E, dark variant #7E9D7C.
   - Destructive: muted rose #C48B8B.
   - Checkmark green: #4CAF50.
   - Text on dark: white #FFFFFF, secondary #B0B0B0.
   - Corners: 16px cards, 12px buttons, 8px inputs, full-round for circular badges.
   Use CSS variables. Touch targets at least 44px. Support safe-area-inset for notched devices.

3. PWA setup:
   - Web App Manifest: name "RepSync", short_name "RepSync", start_url, display "standalone", theme_color and background_color matching #000000, icons (generate from assets/repSyncLogo.png — 192x192, 512x512, and 180x180 for Apple touch icon).
   - Service worker: use vite-plugin-pwa; cache static assets for offline-first.
   - In index.html: viewport with viewport-fit=cover, apple-mobile-web-app-capable, apple-mobile-web-app-status-bar-style, and <link rel="apple-touch-icon">.

4. Set up React Router with routes matching plan-web.md Section 9. All can be placeholder pages for now.

5. Call navigator.storage.persist() on app startup.

Do not implement screens or data layer yet — only project skeleton, theme, routing stubs, and PWA shell.
```

**Done when:** Project runs (npm run dev); theme CSS variables match design; manifest and service worker work; all routes exist as stubs; app is installable via Add to Home Screen.

---

## Phase 2: Data Layer (IndexedDB & Models)

**Paste this prompt:**

```
RepSync Web — follow docs/plan-web.md. Phase 1 (project, theme, PWA shell) is done. Do Phase 2 only: local data layer with IndexedDB.

1. Add Dexie.js (or idb) and define the data model matching plan-web.md Section 4:
   - Workout (template): id, name, createdAt, orderIndex.
   - Exercise: id, name, workoutId, orderIndex.
   - ExerciseSet: id, exerciseId, orderIndex.
   - CompletedWorkout: id, name, date (yyyy-MM-dd string), startedAt, endedAt, isQuickWorkout, elapsedSeconds.
   - CompletedExercise: id, completedWorkoutId, name, orderIndex.
   - CompletedSet: id, completedExerciseId, orderIndex, weight (nullable number), reps (nullable number).
   - UserProfile: id (always 1), displayName, avatarUri (data URL string), totalCompletedWorkouts.
   - BodyweightEntry: id, date (yyyy-MM-dd string), weight (number).

2. Create a data service layer with methods for:
   - CRUD for workouts, exercises, sets (templates).
   - CRUD for completed workouts with nested exercises/sets.
   - "Previous" query: given exercise name and set index, find the most recent completed set matching that exercise name and set index.
   - getAllWorkouts ordered by orderIndex ASC.
   - updateWorkoutOrder(id, orderIndex), getWorkoutCount().
   - getDatesWithCompletedWorkouts() for calendar dots.
   - getCompletedWorkoutsForDate(date) for day view.
   - Exercise history: getCompletedSetsForExercise(name) for stats and chart.
   - Bodyweight: insert, update, delete, getAllChronological, getLatest.
   - UserProfile: get, update.
   - Exercise name suggestions: getDistinctExerciseNames() from completed workouts.
   - searchWorkouts(query).

3. Add JSON export/import functions for all data (backup/restore).

All reads/writes go through this service layer. No UI yet.
```

**Done when:** IndexedDB stores all entities; service layer has all query methods; export/import works; project builds.

---

## Phase 3: Home Screen & Navigation

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md is the spec. Phases 1-2 are done. Do Phase 3 only: Home screen and navigation shell.

Design assets: assets/references/ — Home screen with calendar, buttons, and bottom nav.

1. Bottom navigation bar: Home | Profile. Only visible on Home and Profile routes. Active tab indicated visually. Match dark theme.

2. Home screen (plan-web.md Section 5.1):
   - Calendar: month view with month/year header and left/right arrows. Days with completed workouts (from IndexedDB) get a muted green dot. Today is visually distinct. Tapping a day navigates to /day/:date.
   - Motivational GIF: Below calendar, show a random anime GIF from a curated list (use the same Giphy URLs from the Android app's MotivationalGif.kt — read that file for the URL list). Random selection on each page load. Fallback emoji combos if load fails. Use <img> tag.
   - Two buttons: "Workouts" (navigate to /workouts), "Quick Go" (navigate to /quick-workout).

3. Wire calendar to real data from IndexedDB.

Only Home + bottom nav + navigation. Profile, Workouts, etc. remain stubs.
```

**Done when:** Home shows calendar with green dots; GIF loads randomly; buttons navigate correctly; bottom nav works between Home and Profile stub.

---

## Phase 4: Workouts List, Detail & New Workout

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md. Phases 1-3 done. Do Phase 4: Workouts list, workout detail, and New/Edit Workout.

Design assets: assets/references/ — Workouts list, workout detail modal, and New Workout screens.

1. Workouts List (plan-web.md Section 5.2):
   - Header: Back (to Home), "Workouts", search toggle.
   - Search bar: animated visibility, filters by name. Disables reorder when active.
   - Workout list items: name + exercise count. Ordered by orderIndex.
   - Drag-to-reorder: Use @dnd-kit/core (or similar touch-friendly library). Long-press to initiate. Visual feedback: opacity 0.85 + shadow when dragging. Save orderIndex on drop. Disabled during search.
   - Tap item → workout detail overlay (modal).
   - FAB "+" → /workouts/new.

2. Workout detail overlay:
   - Workout name, exercise list ("X x ExerciseName" sorted by orderIndex), "Start Workout" (green), "Edit Workout", close (X).

3. New Workout / Edit Workout (plan-web.md Section 5.3):
   - Header: Back, "New Workout" / "Edit Workout", Save (disabled if no name or no exercises).
   - Workout name input with "Name: " prefix.
   - Exercise cards with drag-to-reorder (same library). Long-press to drag. Visual feedback matches workouts list.
   - Each exercise: name field with autocomplete suggestions, delete (X), sets section with numbered badges (tap to remove if >1), "+ Add Set".
   - "Add Exercise" button (green).
   - Save: persist to IndexedDB. New workouts: orderIndex = current count.
   - Edit: load existing workout, preserve orderIndex.

4. Exercise name autocomplete: query distinct exercise names from completed workout history.

Match dark theme, rounded cards, muted green primary actions per design assets.
```

**Done when:** Workouts list with drag-to-reorder and search; detail overlay; New/Edit Workout with drag-to-reorder exercises, autocomplete, and save; all persists to IndexedDB.

---

## Phase 5: Active Workout & Quick Workout

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md. Phases 1-4 done. Do Phase 5: Active Workout and Quick Workout.

Design assets: assets/references/ — Active workout screen, set table, dialogs.

1. Active Workout screen (plan-web.md Section 5.4):
   - Header: stopwatch icon (tappable → rest timer dialog), elapsed timer, workout name, close X (destructive red).
   - Rest timer banner: when active, shows countdown with "Rest" label and "Skip" button.
   - Exercise cards with drag-to-reorder (same library as Phase 4):
     - Exercise name field with autocomplete.
     - Hourglass icon (⏳) next to name when name is not blank — tappable → /exercise-history/:name.
     - Delete (X).
     - Set table: Set# | Previous | +lbs | Reps | ✓
       - Set number badge (tappable to remove if >1).
       - Previous: shows last logged data from history (query by exercise name + set index). Shows current data in green when completed.
       - Weight input: decimal keyboard, auto-select all text on focus.
       - Reps input: number keyboard, auto-select all text on focus.
       - Checkmark: toggle completed (green background when done).
     - "+ Add Set".
   - "Add Exercise" (green), "Finish Workout" (green, bold).

2. Rest timer (plan-web.md Section 7):
   - Dialog: presets (30s, 1m, 1m 30s, 2m) + custom seconds input. Set/Cancel.
   - Countdown via setInterval. Updates banner every second.
   - On complete: play alarm sound (new Audio with alarm sound or Web Audio API beep at max volume for 1200ms), vibrate (navigator.vibrate([400, 200, 400, 200, 400, 200, 400])), show browser Notification if permitted.
   - "Skip" stops timer immediately.
   - Timer persists duration preference in localStorage or IndexedDB.

3. Dialogs:
   - Cancel Workout: "Cancel Workout?" → Resume / Cancel (destructive). Cancel discards session.
   - Finish Workout: "Finish Workout?" → Cancel / Finish (green). On Finish: save CompletedWorkout with all exercises/sets/values, increment profile workout count, navigate to Home.

4. Quick Workout (/quick-workout):
   - Same screen as Active Workout. Title "Quick Workout". No pre-filled exercises. isQuickWorkout = true on save.

5. Wire "Start Workout" from workout detail to Active Workout with template loaded.

6. Auto-select text on focus for weight/reps inputs: when field gains focus, select all text so typing replaces the value.

Match design assets: dark theme, muted green actions, destructive red for cancel/X, rounded cards.
```

**Done when:** Active Workout works end-to-end with timer, rest timer (with sound/vibration), drag-to-reorder exercises, set completion, finish/cancel dialogs, exercise history link; Quick Workout works; data saves to IndexedDB.

---

## Phase 6: Profile & Edit Profile

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md. Phases 1-5 done. Do Phase 6: Profile and Edit Profile screens.

Design assets: assets/references/ — Profile screen and settings.

1. Profile screen (plan-web.md Section 5.6):
   - Header: "Profile".
   - Profile card: avatar (or placeholder icon), display name ("Guest" if not set), "X Workouts" count. Tappable → /profile/edit.
   - Streak Badge: fire emoji + "X Day Streak". Only shown if streak > 0. Placed between profile card and bodyweight section.
     - Streak calculation (same as Android): Without schedule, count consecutive days with workouts backward from today/yesterday. With schedule, scheduled days need workouts to continue streak, rest days never break it, bonus workouts on rest days count.
   - Bodyweight section:
     - Latest weight in green.
     - Weight progression chart (green line with dots, grid lines, y-axis min/mid/max, x-axis first/last dates). Show when 2+ entries exist. Use recharts, chart.js, or custom SVG.
     - "+" button → Add Bodyweight dialog (decimal input, Save/Cancel).
     - "View All Entries" button → /bodyweight-entries.
   - Bottom nav: Home | Profile (Profile selected).

2. Edit Profile screen (plan-web.md Section 5.7):
   - Header: Back, "Edit Profile", Save.
   - Avatar: tappable → file input (accept image/*). Convert to data URL, store in IndexedDB. "Tap to change photo" hint.
   - Display name input.
   - Workout count (read-only).
   - Workout schedule: 7 day-of-week buttons (Su-Sa). Selected = green. Stored in IndexedDB/localStorage.
   - Workout reminders: toggle switch. When enabled: custom message input, time picker (hour 0-23, minute 0-59). Use Notification API + service worker to schedule. Request notification permission when enabled.

Match dark theme per design assets.
```

**Done when:** Profile shows avatar, name, count, streak, bodyweight chart, add/view entries; Edit Profile saves avatar, name, schedule, reminders; streak calculates correctly.

---

## Phase 7: Calendar Day View

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md. Phases 1-6 done. Do Phase 7: Calendar Day View.

Design assets: assets/references/ — Day view screens.

1. Day View screen (plan-web.md Section 5.8):
   - Header: Back arrow, formatted date (e.g., "February 24, 2026").
   - Per completed workout on that day:
     - Workout name, duration (MM:SS), "Quick Workout" label if applicable.
     - Exercise rows (expandable on tap):
       - Exercise name in green (tappable → /exercise-history/:name). Hourglass icon.
       - Chevron icon (▼ expanded, ▸ collapsed).
       - When expanded: set detail rows with columns SET | WEIGHT | REPS.
         - Set number, trophy (🏆) for best set (highest weight), weight + "lbs", reps + "reps".
         - Sort sets by orderIndex.
     - Action buttons row:
       - "Copy" → date picker dialog (mini calendar). Copies workout to selected date as a new CompletedWorkout.
       - "Template" → dialog with name input. Saves as a reusable Workout template.
       - "Remove" → confirmation dialog. Permanently deletes this completed workout.
     - Success banners: auto-dismiss after 2 seconds ("Template saved!", "Workout copied!", "Workout removed!").
   - Empty state: "No workouts on this day".

2. Date picker for "Copy": simple calendar or date input to pick target date.

3. Save as template: create new Workout from CompletedWorkout data (name, exercises, set counts).

Match dark theme. Exercise name in green text (#8DAF8E). Light card background (#D1D1D6) for expanded set details if matching Android.
```

**Done when:** Tapping calendar day opens Day View; exercises expandable with full set details; Copy, Template, Remove all work; success banners show.

---

## Phase 8: Exercise History & Bodyweight Entries

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md. Phases 1-7 done. Do Phase 8: Exercise History and Bodyweight Entries screens.

1. Exercise History (plan-web.md Section 5.9):
   - Header: Back, exercise name.
   - Stats row (3 cards): PR (max weight), Volume (total weight x reps, formatted as K/M), Sessions (count).
   - Weight progression chart: same chart component as Profile bodyweight chart (green line/dots, grid, axis labels). Shows max weight per session over time.
   - History section:
     - "History" title.
     - Date range filter: "Filter by Date" button → date range picker dialog. "Clear Filter" when active. Shows "Jan 1 – Jan 14" or similar.
     - Default shows "Showing last X of Y sessions".
     - Session cards: date, workout name, set rows ("Set 1: 135 lbs x 10 reps").
   - Date range picker: start date input (MM/DD/YYYY), auto-calculates 2-week window, preview, Apply/Cancel.
   - Empty states: "No history for this exercise" / "No sessions in this date range".

2. Bodyweight Entries (plan-web.md Section 5.10):
   - Header: Back, "Bodyweight Entries".
   - Date range filter: "All Time" or active range display. "Filter by Date" / "Clear" button.
   - Entries list (reverse chronological): date, weight value, edit (pencil icon), delete (X, destructive).
   - Edit Weight dialog: shows date, decimal input pre-filled, Save/Cancel.
   - Date range picker: start/end date inputs, preview, Apply/Cancel.
   - Empty states: "No entries" / "No entries in selected range".

3. Wire exercise history navigation: hourglass icon in Active Workout and exercise name in Day View both navigate to /exercise-history/:name.

4. Wire "View All Entries" on Profile to /bodyweight-entries.

Reuse the chart component from Profile for both screens.
```

**Done when:** Exercise History shows stats, chart, filterable session history; Bodyweight Entries shows filterable list with edit/delete; navigation from Active Workout and Day View works.

---

## Phase 9: PWA Polish, Data Safety & README

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md. Phases 1-8 done. Do Phase 9: PWA polish, data safety, and documentation.

1. PWA polish:
   - Verify manifest: correct start_url, display standalone, all icons, theme_color #000000, background_color #000000.
   - Service worker caches app shell + static assets for offline. Test that app works offline after first load.
   - Test Add to Home Screen on iOS Safari: app launches in standalone mode with correct icon and splash.

2. Data safety (plan-web.md Section 2):
   - Implement data export: button in Edit Profile or Profile. Exports all data (workouts, exercises, sets, completed workouts, bodyweight, profile, settings) as a single JSON file. Trigger browser download.
   - Implement data import: button in Edit Profile. File picker for JSON. Validate structure, merge or replace data, confirm with user.
   - navigator.storage.persist() is already called on startup (Phase 1). Verify it's working.

3. In-app install hint: If running in Safari (not standalone), show a dismissible banner: "Add to Home Screen for the best experience" with brief instructions.

4. Update README (web/ folder) with:
   - Description: RepSync Web — workout tracker PWA, identical to the Android app.
   - How to run dev: npm run dev in web/.
   - How to build: npm run build. Output in web/dist.
   - How to deploy: deploy dist/ to any static HTTPS host.
   - iOS install steps: Open URL in Safari → Share → Add to Home Screen.
   - Note: data is stored locally only; use export/import for backup.

5. Final testing checklist:
   - All screens match design assets and dark theme.
   - Drag-to-reorder works on touch devices (workouts + exercises).
   - Rest timer sound plays in background.
   - Calendar dots update after finishing a workout.
   - Streak calculates correctly with and without schedule.
   - Charts render correctly with various data amounts.
   - Auto-select text on weight/reps input focus.
   - Exercise autocomplete works.
   - Offline mode works after first load.
```

**Done when:** PWA installable on iOS; offline works; data export/import works; README complete; all features match Android app.

---

## Full MVP Checklist (from plan-web.md Section 6)

After all phases, confirm:

- [ ] Guest by default; optional local profile (name, avatar).
- [ ] All data in IndexedDB. navigator.storage.persist() called. JSON export/import available.
- [ ] Home: calendar with green dots, motivational anime GIF (random each open), Workouts + Quick Go buttons, bottom nav.
- [ ] Workouts: list with drag-to-reorder, search, FAB "+", detail overlay → Start / Edit.
- [ ] New/Edit Workout: name, exercises with autocomplete + drag-to-reorder, sets, Save.
- [ ] Active Workout: timer, rest timer (sound + vibration + notification), exercise cards with drag-to-reorder, set table (Previous, +lbs, Reps, checkmark), exercise history via hourglass, add set/exercise, finish/cancel dialogs.
- [ ] Quick Workout: same as active, no pre-filled exercises, flagged as quick.
- [ ] Profile: avatar + name + count, streak badge, bodyweight section (chart, add, view all).
- [ ] Edit Profile: avatar, name, workout schedule, reminders.
- [ ] Day View: expandable exercise/set details, copy to day, save as template, delete workout.
- [ ] Exercise History: stats (PR, volume, sessions), chart, filterable session history.
- [ ] Bodyweight Entries: filterable list, edit weight, delete.
- [ ] Auto-select text on focus, exercise autocomplete.
- [ ] PWA: installable on iOS, works offline, standalone display.
- [ ] Data export/import for backup safety.

---

## One-Shot Full Build Prompt (Optional)

If you prefer to give Claude the whole scope in one go:

```
Build RepSync Web from scratch using docs/plan-web.md as the single source of truth. The app must be functionally identical to the Android RepSync app (in app/ directory). Design assets: assets/repSyncLogo.png and assets/references/IMG_1505.PNG through IMG_1538.PNG.

Stack: Vite + React + TypeScript in web/ folder. IndexedDB via Dexie.js. PWA with vite-plugin-pwa. @dnd-kit for drag-to-reorder.

Theme: pure black background, dark charcoal surface (#1A1A1A), cards (#2C2C2E), sage green primary (#8DAF8E), muted rose destructive (#C48B8B), white text, rounded corners. CSS variables. Touch targets >= 44px.

All features from plan-web.md Sections 5.1-5.10:
- Home: calendar with workout dots, random anime GIF, Workouts/Quick Go buttons.
- Workouts list: drag-to-reorder, search, detail overlay, Start/Edit.
- New/Edit Workout: exercises with drag-to-reorder + autocomplete, sets, Save.
- Active Workout: timer, rest timer (audio alert + vibration), drag-to-reorder exercises, set table with Previous/weight/reps/checkmark, exercise history link, finish/cancel dialogs.
- Quick Workout: same as active, no template.
- Profile: avatar/name/count card, streak badge, bodyweight chart + add + view all entries.
- Edit Profile: avatar picker, name, workout schedule, reminders.
- Day View: expandable set details, copy to day, save as template, delete workout.
- Exercise History: stats (PR/volume/sessions), chart, filterable session list.
- Bodyweight Entries: filterable list, edit/delete.
- Data export/import (JSON backup).
- PWA: installable on iOS via Add to Home Screen, offline-first.

Deliver a complete, deployable static web app with README.
```

---

*End of prompt guide. Keep docs/plan-web.md in context for every phase, and reference assets/ when building UI.*
