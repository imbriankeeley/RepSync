# RepSync — Gym App Plan

A simple, offline-first Android workout app. This document is the single source of truth for building the app from scratch. The UI must match the theme and layout of the design assets in this repo.

---

## 1. Design Reference & Theme

**Assets in this repo (use as visual spec):**
- `assets/repSyncLogo.png` — App logo (dumbbell + sound-wave motif; supports future music feature).
- `assets/references/IMG_1505.PNG` – `IMG_1538.PNG` — Screens for Home, Workouts, New Workout, active workout, Quick Workout, Profile, Calendar, and dialogs.

**Visual rules (match the photos):**
- **Background:** Dark charcoal / near-black (`#1a1a1a` or similar).
- **Cards & inputs:** Light grey rounded rectangles; text on these is dark grey/black.
- **Primary actions:** Muted sage/olive green (e.g. `#8DAF8E`, `#7E9D7C`) — “Add Exercise”, “Finish Workout”, “Quick Go”, “Start Workout”, checkmarks.
- **Destructive/cancel:** Muted red/pink for “Cancel” in “Cancel Workout?” and the header “X”.
- **Text on dark:** White or very light grey.
- **Corners:** Consistently rounded (e.g. 12–16dp for cards, 8dp for buttons/inputs).
- **Typography:** Clean sans-serif; clear hierarchy (title bold, labels smaller).
- **Simplicity:** Minimal chrome; no extra decoration. Match the photos for layout and spacing.

---

## 2. Platform & Storage

- **Platform:** Android only (no iOS in initial scope).
- **Storage:** Local only. No cloud, no backend. Use Android persistence that survives app restarts:
  - **Room** (SQLite) recommended for workouts, exercises, sets, and calendar entries.
  - Alternatively a single JSON/file-based store if you prefer (e.g. app-specific internal storage).
- **No account required:** Users can use the app as a guest indefinitely. “Create profile” only affects local identity (e.g. display name, avatar); no login or sync in v1.

---

## 3. User Model

- **Default:** User is a **Guest**. No sign-up.
- **Optional profile:** “Create profile” (or “Set up profile”) from Profile screen:
  - Local only: e.g. display name, optional avatar (stored on device).
  - No email/password or cloud sync in this version.
- **Profile screen:** Shows “Guest” or profile name, and total **completed** workout count (e.g. “16 Workouts”). Tapping the profile card can open profile/settings (name, avatar).

---

## 4. Data Model (Local)

Define at least:

- **Workout (template)**
  - id, name, createdAt, list of exercises (order preserved).
- **Exercise (within a workout)**
  - id, name, workoutId (or template id), orderIndex, list of sets.
- **Set**
  - id, exerciseId, orderIndex, previousWeight (optional), previousReps (optional), weight (+lbs), reps.
- **CompletedWorkout (calendar / history)**
  - id, name (or template id), date (calendar day), startedAt, endedAt (null if abandoned), list of exercises with sets and logged values (weight, reps). Store enough to show “Previous” in future sessions.
- **Quick workout:** Same as CompletedWorkout but with a flag or naming convention (e.g. name “Quick Workout” + date) so it can be found and optionally “Save as template” / copy to another day.

“Previous” column in the UI = last time this exercise was done (from CompletedWorkout history), e.g. “135 lb x 5”.

---

## 5. Core Flows & Screens

### 5.1 Home
- **Calendar:** Month view (e.g. “October 2024”), arrows to change month. Days with at least one completed workout get a muted green highlight. Tapping a day opens “Day view” (see below).
- **Two main buttons:**
  - **Workouts** — navigates to Workouts list.
  - **Quick Go** — starts a Quick Workout (no template; user adds exercises on the fly).
- **Bottom nav:** Home | Profile (active tab indicated).

### 5.2 Workouts List
- **Header:** Back (to Home), title “Workouts”, search icon (optional: filter by name).
- **List:** All saved workout templates (e.g. “Chest Workout”, “Back Workout”). Tapping a row opens a **workout detail modal** (or screen).
- **Workout detail:** Shows workout name and list of exercises (e.g. “2 x Bench Press”, “2 x Chest Press”). Primary button: **Start Workout** (muted green). Close (X) to dismiss.
- **FAB / “+” button:** Create **New Workout** (go to New Workout screen).

### 5.3 New Workout (Create/Edit template)
- **Header:** Back, “New Workout”, **Save** (persists template locally).
- **Name:** Single input, e.g. “Name: Push” (or “Name: [input]”).
- **Exercises:** For each exercise, a card showing:
  - Exercise name (editable).
  - Table: Set | Previous | +lbs | Reps.
  - “+ Add Set” for that exercise.
  - “Add Exercise” (green) to add another exercise to the workout.
- **Previous:** Show last logged weight/reps for that exercise from history, if any.
- **Save:** Stores the template; user can start it later from Workouts or from Calendar (copy to day).

### 5.4 Active Workout (from template or Quick Go)
- **Header:** Timer (e.g. stopwatch icon + “00:01”), workout name (“Chest Workout” or “Quick Workout”), **X** (close) on the right.
- **Timer:** Elapsed time. Option to **set timer length** (e.g. rest timer or target duration) can be a separate small control or settings (exact placement per design; ensure it’s simple).
- **Body:** One section per exercise (same table: Set, Previous, +lbs, Reps). “+ Add Set” per exercise. Green checkmark when set is completed (optional but matches design).
- **Actions:** **Add Exercise** (green), then **Finish Workout** (green, full-width).
- **Close (X):** Show dialog **“Cancel Workout?”** with **Resume** and **Cancel**. Cancel discards the session (do not save to calendar). Resume returns to workout.
- **Finish Workout:** Show dialog **“Finish Workout?”** with **Cancel** and **Finish**. Finish saves as CompletedWorkout for today and returns to Home (or Calendar); update profile workout count.

### 5.5 Quick Workout
- Same UI as Active Workout but title is “Quick Workout” and there are no pre-filled exercises; user adds all exercises via “Add Exercise”. Flow for close/finish and timer is the same. On finish, save as a completed session for today (and optionally allow “Save as template” later).

### 5.6 Profile
- **Header:** “Profile”.
- **Card:** Profile icon (or placeholder), “Guest” (or profile name), “X Workouts” (completed count). Chevron to open profile/settings.
- **Bottom nav:** Home | Profile (Profile selected).

### 5.7 Calendar — Day View & Copy
- **From Home:** Tap a calendar day → **Day view** for that date.
- **Day view:** List of workouts completed (or started) that day. For each: name, duration, list of exercises/sets if desired. Option to **Copy to another day** (pick target date and create a copy as a new scheduled or template for that day).
- **Create workout from a day:** From day view, “Copy workout” (or “Use as template”) so the user can save that day’s workout as a reusable template, or copy it to another day. This covers: “copy workouts from previous days to any other day” and “create workouts by copying workouts from a certain day in the calendar” (including quick workouts they liked).

---

## 6. Feature Checklist (MVP)

- [ ] Guest by default; optional local profile (name, avatar).
- [ ] All data stored only on device (Room or file-based).
- [ ] Home: calendar with green dots for days with workouts; Workouts + Quick Go buttons; bottom nav Home | Profile.
- [ ] Workouts: list templates, search (optional), FAB “+”, tap row → detail → Start Workout.
- [ ] New Workout: name, add exercises, sets (Previous, +lbs, Reps), Save.
- [ ] Active workout: elapsed timer, optional set timer length, add set, add exercise, finish workout, close (X) with “Cancel Workout?” (Resume / Cancel).
- [ ] “Finish Workout?” dialog (Cancel / Finish); on Finish, save to history and increment profile count.
- [ ] Quick Workout: same as active workout, no pre-filled exercises.
- [ ] Profile: show Guest or name, completed workout count, entry to profile/settings.
- [ ] Calendar: tap day → day view; view past workouts; copy workout from one day to another; create template from a day’s workout (e.g. from a quick workout they liked).

---

## 7. Future Update: Spotify (Out of Scope for v1)

- **Do not implement** in the first version. Plan for a later update:
  - Simple Spotify API integration (e.g. OAuth, “now playing”, maybe play/pause/skip).
  - Optional: show current track on workout screen or in a small widget.
- The logo (dumbbell + sound waves) already supports this future theme. No UI for Spotify in MVP.

---

## 8. Android Project Setup

- **Language:** Kotlin preferred; Java acceptable.
- **Min SDK:** 21+ (or 24+ for simpler API). Target latest stable.
- **Architecture:** Single-activity with fragments or multi-screen composable (Jetpack Compose) — choose one and stick to it. MVVM or MVI with a single source of truth for workouts/calendar.
- **Libraries (suggested):**
  - Room for DB (workouts, exercises, sets, completed workouts).
  - ViewModel + LiveData or StateFlow for UI state.
  - Navigation component or Compose Navigation.
- **Build:** Gradle (Kotlin DSL or Groovy). Produce a **release APK** (or AAB) that can be installed on a device.

---

## 9. Distribution via Obtainium (GitHub)

- **Obtainium** installs apps from URLs (e.g. GitHub Releases) by downloading APK/AAB.
- **Repo layout:**
  - Keep app source in the repo (e.g. `/app` module).
  - Use **GitHub Releases** for distribution: each release attaches the built **APK** (or AAB).
- **Steps for users:**
  1. In Obtainium, add source: GitHub repo URL (e.g. `https://github.com/<user>/<repo>`).
  2. Obtainium will look for the latest release and APK/AAB asset.
  3. User installs/updates from there.
- **Build docs:** In the repo README, include:
  - How to build the APK (e.g. `./gradlew assembleRelease`).
  - Where the output is (e.g. `app/build/outputs/apk/release/app-release.apk`).
  - How to create a GitHub Release and upload that APK so Obtainium can see it.

---

## 10. Repo Structure (Current)

```
RepSync/
├── docs/
│   └── plan.md             # This plan (single source of truth)
├── prompts.md              # Claude Code prompt guide for phased implementation
├── assets/
│   ├── repSyncLogo.png     # App icon / logo
│   └── references/         # Design reference screens (do not delete)
│       └── IMG_1505.PNG … IMG_1538.PNG
├── README.md               # How to build, run, and release for Obtainium
├── app/
│   ├── src/main/
│   │   ├── java/...        # or kotlin/...
│   │   ├── res/            # drawables, layouts, values (theme colors!)
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

- **docs/:** Plan and (optionally) other spec docs. Reference design assets under `assets/`.
- **prompts.md:** Step-by-step prompts for Claude Code; use with this plan when implementing.
- **assets/:** Logo and design references. Use `assets/repSyncLogo.png` for launcher icon; match UI to screens in `assets/references/`.
- **Theme colors:** Define in `app/src/main/res/values/colors.xml` (and themes.xml) to match the design (dark background, muted green, destructive red, light grey cards).
- **Logo:** Use `assets/repSyncLogo.png` for launcher icon (copy into `res` or reference as needed) and optionally in-app branding.

---

## 11. Summary for Implementers

- **UI:** Match the provided PNGs: dark theme, rounded cards, muted green primary actions, simple typography.
- **Behavior:** Guest-first, local-only storage; create workouts and exercises; start from template or Quick Go; timer; finish or cancel with clear dialogs; calendar with history and copy-from-day.
- **Scope:** No cloud, no Spotify in v1. Spotify is a documented future update.
- **Deliverable:** Android app buildable from this repo and installable via GitHub Releases + Obtainium.

Use this plan as the single spec to implement RepSync from scratch and get it onto an Android device.
