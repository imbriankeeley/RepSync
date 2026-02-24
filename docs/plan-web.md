# RepSync Web — iOS PWA Plan

A web app that is **functionally identical** to the Android RepSync workout app. iOS users open it in Safari and use **Add to Home Screen** to install it like an app. This document is the single source of truth for building the web version. The UI must match the theme and layout of the same design assets used for the Android app.

---

## 1. Design Reference & Theme (Same as Android)

**Assets in this repo (use as visual spec):**
- `assets/repSyncLogo.png` — App logo (dumbbell + sound-wave motif; supports future music feature).
- `assets/references/IMG_1505.PNG` – `IMG_1538.PNG` — Screens for Home, Workouts, New Workout, active workout, Quick Workout, Profile, Calendar, and dialogs.

**Visual rules (match the photos — identical to Android):**
- **Background:** Pure black `#000000` (outer), dark charcoal `#1A1A1A` (surface), `#2C2C2E` (cards).
- **Cards & elevated cards:** `#2C2C2E` for cards, `#3A3A3C` for elevated card elements (set badges, buttons within cards).
- **Input backgrounds:** `#48484A` for text input fields.
- **Primary actions:** Muted sage/olive green `#8DAF8E`, dark variant `#7E9D7C` — "Add Exercise", "Finish Workout", "Quick Go", "Start Workout", checkmarks, selected days.
- **Destructive/cancel:** Muted rose/pink `#C48B8B` for "Cancel" buttons and close "X".
- **Checkmark green:** `#4CAF50` for completed set checkmarks.
- **Dividers:** `#3A3A3C`.
- **Text on dark:** White `#FFFFFF`, secondary `#B0B0B0`.
- **Text on light cards:** Dark `#1C1C1E`, secondary `#6C6C6E` (for exercise tables in Day View).
- **Light cards:** `#D1D1D6` (exercise table backgrounds in Day View).
- **Corners:** 16px for cards, 12px for buttons/overlays, 8px for inputs, full-round for circular badges.
- **Typography:** Clean sans-serif; clear hierarchy (title bold, labels smaller).

**Web-specific:** Use CSS variables for all theme colors. Ensure touch targets are at least 44px for iOS. Support both portrait and narrow viewports; layout should feel like the native app when added to home screen (standalone/safe-area).

---

## 2. Platform & Storage

- **Platform:** Web only for this deliverable. Target **iOS Safari** as the primary install path: user visits the URL and taps **Share → Add to Home Screen**. The app runs in standalone mode (no browser chrome) when launched from the home screen.
- **PWA requirements:**
  - **Web App Manifest** — `name`, `short_name`, `start_url`, `display: standalone`, icons (192x192, 512x512 from `assets/repSyncLogo.png`), `theme_color`, `background_color` to match the app theme.
  - **Service Worker** — Cache static assets and enable offline-first; app must work offline after first load.
  - **HTTPS** — Required for service worker and Add to Home Screen.
- **Storage:** Local only. No cloud, no backend. Use **IndexedDB** (via a wrapper like idb, Dexie, or similar) for all data. Call `navigator.storage.persist()` on app startup to request persistent storage.
- **Data backup:** Implement JSON export/import so users can back up and restore their data manually. iOS Safari can evict IndexedDB data under storage pressure; this is the safety net.
- **No account required:** Same as Android — users use the app as a guest; optional local profile (display name, avatar) only.

---

## 3. User Model (Identical to Android)

- **Default:** User is a **Guest**. No sign-up.
- **Optional profile:** "Edit Profile" from Profile screen:
  - Local only: display name, optional avatar (stored in IndexedDB as data URL).
  - No email/password or cloud sync.
- **Profile screen:** Shows "Guest" or profile name, completed workout count, streak badge, bodyweight tracking section.
- **Workout schedule:** User can set which days of the week are workout days (used for streak calculation).
- **Reminders:** Web Notifications API equivalent — prompt user for notification permission, schedule reminders on workout days with custom message and time. Use service worker for background notification scheduling.

---

## 4. Data Model (Local — Same Entities as Android)

Define the same logical model; persistence is IndexedDB instead of Room:

- **Workout (template)**
  - id, name, createdAt, **orderIndex** (for drag-to-reorder), list of exercises.
- **Exercise (within a workout)**
  - id, name, workoutId, **orderIndex** (for drag-to-reorder), list of sets.
- **ExerciseSet**
  - id, exerciseId, orderIndex.
- **CompletedWorkout (calendar / history)**
  - id, name (or template id), date (calendar day, `yyyy-MM-dd` string), startedAt, endedAt, **isQuickWorkout** flag, elapsedSeconds, list of exercises with sets and logged values (weight, reps, isCompleted).
- **CompletedExercise**
  - id, completedWorkoutId, name, orderIndex, list of completed sets.
- **CompletedSet**
  - id, completedExerciseId, orderIndex, weight (nullable), reps (nullable).
- **UserProfile** (single record)
  - id (always 1), displayName, avatarUri (data URL), totalCompletedWorkouts.
- **BodyweightEntry**
  - id, date (`yyyy-MM-dd` string), weight (double).
- **WorkoutDays** — Stored as a preference/config: set of days of the week that are scheduled workout days.
- **ReminderSettings** — Stored as preference: enabled (boolean), message (string), hour (int), minute (int).

"Previous" column in the UI = last time this exercise was done (from CompletedWorkout history), matching by exercise name and set index.

---

## 5. Core Flows & Screens (Identical to Android)

### 5.1 Home
- **Calendar:** Month view with month/year header and left/right arrows. Days with completed workouts get a muted green dot/highlight. Today is visually distinct. Tapping a day navigates to Day View.
- **Motivational GIF:** Below the calendar, display a random anime GIF from a curated list (same URLs as Android). New GIF on each app open (random selection). Fallback emoji combos if offline/load fails.
- **Streak Badge:** NOT on home screen (moved to Profile). Remove from home if present.
- **Buttons:** "Workouts" (→ Workouts list), "Quick Go" (→ Quick Workout).
- **Bottom nav:** Home | Profile (only visible on Home and Profile screens).

### 5.2 Workouts List
- **Header:** Back arrow, "Workouts" title, search toggle button.
- **Search bar:** Animated visibility, filters workouts by name. Search mode disables drag-to-reorder.
- **Workout list:** Templates from IndexedDB, ordered by `orderIndex`. Each item shows name and exercise count.
  - **Drag-to-reorder:** Long-press (or touch-hold) to drag workouts to new position. Visual feedback: slight elevation/opacity change. Order persists via `orderIndex`. Disabled during search.
  - Tap → workout detail overlay.
- **FAB "+"** → New Workout screen.
- **Workout detail overlay (modal):** Name, exercise list ("X x ExerciseName"), "Start Workout" (green), "Edit Workout", close (X).
- **Empty state:** "No workouts yet. Tap + to create one." / "No workouts found" when searching.

### 5.3 New Workout / Edit Workout (Create/Edit template)
- **Header:** Back, "New Workout" or "Edit Workout", Save button (disabled when name is blank or no exercises).
- **Workout name input:** Text field with "Name: " prefix label and placeholder.
- **Exercise cards** (drag-to-reorder):
  - Exercise name field with autocomplete suggestions from history.
  - Delete exercise button (X).
  - **Drag-to-reorder:** Long-press to drag exercises. Visual feedback: opacity + shadow. Order saved automatically (exercises use list position as orderIndex on save).
  - Sets section: "Sets" label with numbered badges. Tapping a badge removes that set (if >1). "X total" label. "+ Add Set" button.
- **"Add Exercise" button** (green, full-width).
- **Save:** Persists template to IndexedDB. New workouts get `orderIndex = count` (appended to bottom).

### 5.4 Active Workout (from template or Quick Workout)
- **Header bar:**
  - Stopwatch icon (tappable → rest timer config dialog).
  - Elapsed timer display (HH:MM:SS).
  - Workout name (centered).
  - Close button (X, destructive red) → Cancel Workout dialog.
- **Rest timer banner:** When active, shows countdown with "Rest" label and "Skip" button. Appears below header.
- **Exercise cards** (drag-to-reorder):
  - Exercise name field with autocomplete.
  - **Hourglass icon (⏳)** next to exercise name — tappable to view exercise history. Visible when exercise has a name.
  - Delete exercise button (X).
  - **Drag-to-reorder:** Long-press to drag. Same visual feedback as workouts.
  - Set table: columns Set | Previous | +lbs | Reps | ✓
    - Set number badge (tappable to remove if >1 set).
    - "Previous" shows last logged data for this exercise (from history). Shows current set data in green when completed.
    - Weight input (decimal keyboard). Auto-select all text on focus.
    - Reps input (number keyboard). Auto-select all text on focus.
    - Checkmark button: toggle set completed (green when done).
  - "+ Add Set" button per exercise.
- **"Add Exercise" button** (green).
- **"Finish Workout" button** (green, bold).
- **Rest timer dialog:** Presets (30s, 1m, 1m 30s, 2m) + custom seconds input. Set/Cancel.
- **Cancel Workout dialog:** "Cancel Workout?" with Resume / Cancel (destructive).
- **Finish Workout dialog:** "Finish Workout?" with Cancel / Finish (green).
- **On Finish:** Save as CompletedWorkout for today, increment profile workout count, return to Home.
- **Rest timer (web implementation):** Use `setInterval` for countdown. On completion, play alarm sound via Web Audio API (or `Audio` element) and trigger vibration via `navigator.vibrate()`. Sound should play even if the tab is in the background (use `AudioContext`). Show browser notification if permission granted.

### 5.5 Quick Workout
- Same UI as Active Workout; title "Quick Workout"; no pre-filled exercises; user adds all via "Add Exercise". `isQuickWorkout = true` on the CompletedWorkout.

### 5.6 Profile
- **Header:** "Profile".
- **Profile card:** Avatar (or placeholder icon), display name ("Guest" if not set), "X Workouts" count. Tappable to navigate to Edit Profile.
- **Streak Badge:** Fire emoji + "X Day Streak" (only shown if streak > 0). Placed between profile card and bodyweight section.
  - Streak calculation: No schedule → count consecutive days with workouts going backward from today/yesterday. With schedule → scheduled days require a workout to continue streak, rest days never break it, workouts on rest days count as bonus.
- **Bodyweight section:**
  - Latest weight display (green text).
  - **Weight progression chart** (line chart with green line/dots, grid, axis labels). Requires 2+ entries to show.
  - "+" button → Add Bodyweight dialog (decimal input, Save/Cancel).
  - **"View All Entries" button** → Bodyweight Entries screen.
- **Bottom nav:** Home | Profile.

### 5.7 Edit Profile
- **Header:** Back arrow, "Edit Profile", Save button.
- **Avatar:** Tappable to pick photo (file input → data URL stored in IndexedDB). "Tap to change photo" hint.
- **Display name input.**
- **Workout count display** (read-only).
- **Workout schedule:** Day-of-week picker (Su–Sa). Selected days highlighted in green. Used for streak calculation and reminders.
- **Workout reminders:** Toggle switch. When enabled: custom message input, time picker (hour + minute). Use Web Notifications API + service worker for scheduling.

### 5.8 Calendar Day View
- **Header:** Back arrow, formatted date (e.g., "February 24, 2026").
- **Completed workout cards** (one per workout on that day):
  - Workout name, duration (MM:SS), "Quick Workout" label if applicable.
  - **Exercise rows** (expandable):
    - Exercise name (green, tappable → Exercise History). Hourglass icon.
    - Column headers: SET | WEIGHT | REPS.
    - Set rows: set number, trophy (🏆) for best set, weight + "lbs", reps + "reps".
    - **Expandable:** Tap exercise row to expand/collapse full set details. Chevron icon indicates state.
  - **Action buttons:**
    - "Copy" — Copy workout to another day (opens mini calendar date picker).
    - "Template" — Save as reusable template (opens name dialog).
    - "Remove" — Delete workout from this day (destructive, with confirmation).
  - **Success banners:** Auto-dismiss after 2s ("Template saved!", "Workout copied!", "Workout removed!").
- **Empty state:** "No workouts on this day".

### 5.9 Exercise History
- **Header:** Back arrow, exercise name.
- **Stats row** (3 cards):
  - **PR:** All-time max weight.
  - **Volume:** Total weight × reps (formatted as K/M for large numbers).
  - **Sessions:** Count of workouts featuring this exercise.
- **Weight progression chart:** Same chart component as Profile bodyweight chart (green line/dots, grid, axis labels).
- **History section:**
  - "History" title.
  - **Date range filter:** "Filter by Date" button → date picker dialog. "Clear Filter" when active. Shows active range.
  - "Showing last X of Y sessions" label in default mode.
  - **Session cards:** Date, workout name, set rows ("Set N: X lbs × Y reps").
- **Date range picker dialog:** Start date input (MM/DD/YYYY), auto-calculates 2-week window, preview, Apply/Cancel.
- **Empty states:** "No history for this exercise" / "No sessions in this date range".

### 5.10 Bodyweight Entries
- **Header:** Back arrow, "Bodyweight Entries" title.
- **Date range filter:** Button showing "All Time" or date range. Opens date range picker dialog. "Clear" button when range is active.
- **Entries list** (reverse chronological):
  - Date, weight value.
  - Edit button (pencil icon) → Edit Weight dialog.
  - Delete button (X, destructive red).
- **Edit Weight dialog:** Shows date, decimal input pre-filled with current weight, Save/Cancel.
- **Date range picker dialog:** Start/end date inputs with preview, Apply/Cancel.
- **Empty states:** "No entries" / "No entries in selected range".

---

## 6. Feature Checklist (Full Parity with Android)

- [ ] Guest by default; optional local profile (name, avatar).
- [ ] All data stored only in the browser/device (IndexedDB). `navigator.storage.persist()` called on startup.
- [ ] Data export/import (JSON backup) for user data safety.
- [ ] Home: calendar with green dots for workout days; motivational anime GIF (random on each open); Workouts + Quick Go buttons; bottom nav Home | Profile.
- [ ] Workouts: list templates with drag-to-reorder, search, FAB "+", tap row → detail → Start Workout / Edit Workout.
- [ ] New Workout / Edit Workout: name, add exercises with autocomplete, sets, drag-to-reorder exercises, Save. New workouts append to bottom of list.
- [ ] Active workout: elapsed timer, rest timer (configurable duration, audio alert, vibration, background notification), exercise cards with drag-to-reorder, set table (Previous, +lbs, Reps, checkmark), exercise history via hourglass icon, add set/exercise, finish/cancel dialogs.
- [ ] Quick Workout: same as active workout, no pre-filled exercises, flagged as quick workout.
- [ ] Profile: avatar + name + workout count card, streak badge, bodyweight section (chart, add entry, view all entries).
- [ ] Edit Profile: avatar picker, display name, workout schedule (day picker), reminders (toggle, message, time).
- [ ] Calendar Day View: workout cards with expandable exercise/set details, copy to another day, save as template, delete workout.
- [ ] Exercise History: stats (PR, volume, sessions), weight progression chart, session history with date range filter.
- [ ] Bodyweight Entries: full list with date range filter, edit weight, delete entry.
- [ ] Auto-select text on focus for weight/reps inputs.
- [ ] Exercise name autocomplete from history.
- [ ] PWA: installable on iOS via Add to Home Screen; works offline after first load; standalone display.

---

## 7. Web-Specific Implementation Notes

### Drag-to-Reorder
Use a drag-and-drop library for touch devices (e.g., `@dnd-kit/core`, `react-beautiful-dnd`, or similar). Must support:
- Long-press to initiate drag (not immediate — prevents conflict with scrolling).
- Visual feedback: opacity change + shadow/elevation on dragged item.
- Reorder callback to update state and persist orderIndex.

### Rest Timer
- Use `setInterval` for countdown (not requestAnimationFrame — needs to work in background tabs).
- On completion: play alarm sound via `new Audio()` or Web Audio API (`AudioContext`). Use `STREAM_ALARM` equivalent by setting volume to max.
- Vibrate via `navigator.vibrate([400, 200, 400, 200, 400, 200, 400])`.
- Show `Notification` if permission granted (works even when tab is backgrounded on iOS if PWA is open).
- Sound duration: ~1200ms.

### Charts
Use a lightweight chart library (e.g., `recharts`, `chart.js`, or custom Canvas/SVG) for:
- Weight progression chart (Profile bodyweight section).
- Weight progression chart (Exercise History).
Both show green line with dots, grid lines, and axis labels.

### GIF Loading
Use `<img>` tags with the same Giphy/Tenor URLs as Android. Fallback emoji if load fails. Random selection on each page load (not daily).

### Keyboard Handling
CSS `env(keyboard-inset-height)` or `visualViewport` API to handle virtual keyboard. Ensure inputs scroll into view when focused.

---

## 8. Future Update: Spotify (Out of Scope for v1)

Same as Android plan — do not implement in the first version. No Spotify UI in MVP.

---

## 9. Web Project Setup

- **Stack:** Vite + React + TypeScript (recommended) or Vite + Vue + TypeScript. Static build only.
- **Build:** Vite. Produce a static build deployable to any static host (GitHub Pages, Netlify, Vercel).
- **PWA:** Use `vite-plugin-pwa` to generate manifest and service worker.
- **State:** Single source of truth. Use React state + context (or Zustand/Jotai) synced with IndexedDB.
- **Routing:** Client-side routing (React Router). Routes:
  - `/` — Home
  - `/profile` — Profile
  - `/profile/edit` — Edit Profile
  - `/workouts` — Workouts List
  - `/workouts/new` — New Workout
  - `/workouts/edit/:id` — Edit Workout
  - `/workout/:id` — Active Workout (from template)
  - `/quick-workout` — Quick Workout
  - `/day/:date` — Day View
  - `/exercise-history/:name` — Exercise History
  - `/bodyweight-entries` — Bodyweight Entries
- **iOS considerations:**
  - `viewport` meta tag with `viewport-fit=cover`.
  - `apple-mobile-web-app-capable` and `apple-mobile-web-app-status-bar-style`.
  - Safe area insets: `env(safe-area-inset-*)`.
  - Apple touch icon (180x180) in manifest and `<link>`.

---

## 10. Distribution (Web / iOS Add to Home Screen)

- **Hosting:** Deploy to a public HTTPS URL.
- **No app store.** Users:
  1. Open the app URL in **Safari** on iOS.
  2. Tap the **Share** button.
  3. Tap **Add to Home Screen**.
  4. Name the icon (e.g., "RepSync") and tap Add.
  5. Launch from home screen in standalone mode.
- **In-app hint:** Optionally show "Add to Home Screen for the best experience" for Safari users not in standalone mode.

---

## 11. Repo Structure

```
RepSync/
├── docs/
│   ├── plan-web.md         # This file
│   └── prompts-web.md      # Web app prompts (phased)
├── assets/
│   ├── repSyncLogo.png     # Shared: logo (use for PWA icons)
│   └── references/         # Shared: design reference screens
├── web/                     # Web app
│   ├── public/
│   │   ├── manifest.webmanifest
│   │   ├── icons/           # PWA icons from repSyncLogo.png
│   │   └── ...
│   ├── src/
│   │   ├── index.html
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── theme/           # CSS variables matching design
│   │   ├── db/              # IndexedDB wrapper, models
│   │   ├── stores/          # State management
│   │   ├── components/      # Shared UI (charts, badges, etc.)
│   │   ├── screens/         # All screens listed in Section 5
│   │   └── utils/           # Formatters, helpers
│   ├── package.json
│   ├── vite.config.ts
│   └── ...
├── app/                     # Android app (existing)
└── ...
```

---

## 12. Summary for Implementers

- **Parity:** The web app must be **functionally identical** to the Android app: same screens, same flows, same data model, same theme. Use the same design assets.
- **Platform:** Web PWA. Primary: iOS Safari → Add to Home Screen → standalone. Offline-capable.
- **Storage:** IndexedDB only; no cloud. `navigator.storage.persist()` + JSON export/import backup.
- **Key features not in a basic workout app:** Drag-to-reorder (workouts + exercises), rest timer with audio alert, exercise history with stats/charts, bodyweight tracking with chart, streak calculation with workout schedule, motivational anime GIFs.
- **Deliverable:** A static web app deployable to HTTPS, installable on iOS home screen, with identical behavior to the Android app.

Use this plan together with `docs/prompts-web.md` to implement RepSync Web.
