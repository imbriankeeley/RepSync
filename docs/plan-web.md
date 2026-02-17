# RepSync Web — iOS PWA Plan

A web app that is **functionally identical** to the Android RepSync workout app. iOS users open it in Safari and use **Add to Home Screen** to install it like an app. This document is the single source of truth for building the web version. The UI must match the theme and layout of the same design assets used for the Android app.

---

## 1. Design Reference & Theme (Same as Android)

**Assets in this repo (use as visual spec):**
- `assets/repSyncLogo.png` — App logo (dumbbell + sound-wave motif; supports future music feature).
- `assets/references/IMG_1505.PNG` – `IMG_1538.PNG` — Screens for Home, Workouts, New Workout, active workout, Quick Workout, Profile, Calendar, and dialogs.

**Visual rules (match the photos — identical to Android):**
- **Background:** Dark charcoal / near-black (`#1a1a1a` or similar).
- **Cards & inputs:** Light grey rounded rectangles; text on these is dark grey/black.
- **Primary actions:** Muted sage/olive green (e.g. `#8DAF8E`, `#7E9D7C`) — “Add Exercise”, “Finish Workout”, “Quick Go”, “Start Workout”, checkmarks.
- **Destructive/cancel:** Muted red/pink for “Cancel” in “Cancel Workout?” and the header “X”.
- **Text on dark:** White or very light grey.
- **Corners:** Consistently rounded (e.g. 12–16px for cards, 8px for buttons/inputs). Use `px` or `rem`; match the proportions of the reference images.
- **Typography:** Clean sans-serif; clear hierarchy (title bold, labels smaller).
- **Simplicity:** Minimal chrome; no extra decoration. Match the photos for layout and spacing.

**Web-specific:** Use CSS variables for theme colors. Ensure touch targets are at least 44px for iOS. Support both portrait and narrow viewports; layout should feel like the native app when added to home screen (standalone/safe-area).

---

## 2. Platform & Storage

- **Platform:** Web only for this deliverable. Target **iOS Safari** as the primary install path: user visits the URL and taps **Share → Add to Home Screen**. The app runs in standalone mode (no browser chrome) when launched from the home screen.
- **PWA requirements:**
  - **Web App Manifest** — `name`, `short_name`, `start_url`, `display: standalone` (or `fullscreen`), icons (e.g. 192x192, 512x512 from `assets/repSyncLogo.png`), `theme_color`, `background_color` to match the app theme.
  - **Service Worker** — Cache static assets and (optionally) enable offline-first; app must work offline after first load.
  - **HTTPS** — Required for service worker and Add to Home Screen.
- **Storage:** Local only. No cloud, no backend. Use **IndexedDB** (via a wrapper like idb, Dexie, or similar) for workouts, exercises, sets, completed workouts, and user profile. Data must survive browser/app restarts.
- **No account required:** Same as Android — users use the app as a guest; optional local profile (display name, avatar) only.

---

## 3. User Model (Identical to Android)

- **Default:** User is a **Guest**. No sign-up.
- **Optional profile:** “Create profile” (or “Set up profile”) from Profile screen:
  - Local only: e.g. display name, optional avatar (stored on device, e.g. in IndexedDB or as data URL).
  - No email/password or cloud sync in this version.
- **Profile screen:** Shows “Guest” or profile name, and total **completed** workout count (e.g. “16 Workouts”). Tapping the profile card opens profile/settings (name, avatar).

---

## 4. Data Model (Local — Same Entities as Android)

Define the same logical model; persistence is IndexedDB instead of Room:

- **Workout (template)**
  - id, name, createdAt, list of exercises (order preserved, e.g. orderIndex).
- **Exercise (within a workout)**
  - id, name, workoutId (or template id), orderIndex, list of sets.
- **Set**
  - id, exerciseId, orderIndex, previousWeight (optional), previousReps (optional), weight (+lbs), reps.
- **CompletedWorkout (calendar / history)**
  - id, name (or template id), date (calendar day), startedAt, endedAt (null if abandoned), list of exercises with sets and logged values (weight, reps). Store enough to show “Previous” in future sessions.
- **Quick workout:** Same as CompletedWorkout with a flag or naming (e.g. name “Quick Workout” + date) so it can be found and optionally “Save as template” / copy to another day.

“Previous” column in the UI = last time this exercise was done (from CompletedWorkout history), e.g. “135 lb x 5”.

---

## 5. Core Flows & Screens (Identical to Android)

Implement the **exact same** flows as in the Android plan (docs/plan.md Section 5). Summary:

### 5.1 Home
- Calendar month view, arrows to change month. Days with completed workouts: muted green highlight. Tap day → Day view.
- Buttons: **Workouts** (→ Workouts list), **Quick Go** (→ Quick Workout).
- Bottom nav: Home | Profile.

### 5.2 Workouts List
- Header: Back, “Workouts”, optional search. List of templates; tap row → workout detail. FAB “+” → New Workout.
- Workout detail: name, exercise list, **Start Workout** (green), X to close.

### 5.3 New Workout (Create/Edit template)
- Header: Back, “New Workout”, **Save**. Name input. Per-exercise cards: name, table (Set | Previous | +lbs | Reps), “+ Add Set”. “Add Exercise” (green). Save persists template locally.

### 5.4 Active Workout (from template)
- Header: Timer, workout name, X. Body: exercise sections with Set | Previous | +lbs | Reps, “+ Add Set”, checkmarks. “Add Exercise”, “Finish Workout” (green). X → “Cancel Workout?” (Resume / Cancel). Finish → “Finish Workout?” (Cancel / Finish); on Finish, save as CompletedWorkout for today, update profile count, return to Home.

### 5.5 Quick Workout
- Same UI as Active Workout; title “Quick Workout”; no pre-filled exercises; user adds via “Add Exercise”. Same timer and dialogs; on finish, save as completed for today.

### 5.6 Profile
- Header “Profile”. Card: icon/placeholder, “Guest” or name, “X Workouts”, chevron to profile/settings. Bottom nav: Home | Profile.

### 5.7 Calendar — Day View & Copy
- Tap calendar day → Day view: list of workouts for that day (name, duration, exercises/sets). Options: **Copy to another day**, **Use as template** / Create workout from a day (save as reusable template, including quick workouts).

---

## 6. Feature Checklist (MVP — Same as Android)

- [ ] Guest by default; optional local profile (name, avatar).
- [ ] All data stored only in the browser/device (IndexedDB).
- [ ] Home: calendar with green dots for days with workouts; Workouts + Quick Go buttons; bottom nav Home | Profile.
- [ ] Workouts: list templates, search (optional), FAB “+”, tap row → detail → Start Workout.
- [ ] New Workout: name, add exercises, sets (Previous, +lbs, Reps), Save.
- [ ] Active workout: elapsed timer, optional set timer, add set, add exercise, finish workout, close (X) with “Cancel Workout?” (Resume / Cancel).
- [ ] “Finish Workout?” dialog (Cancel / Finish); on Finish, save to history and increment profile count.
- [ ] Quick Workout: same as active workout, no pre-filled exercises.
- [ ] Profile: show Guest or name, completed workout count, entry to profile/settings.
- [ ] Calendar: tap day → day view; view past workouts; copy workout from one day to another; create template from a day’s workout.
- [ ] PWA: installable on iOS via Add to Home Screen; works offline after first load; standalone display.

---

## 7. Future Update: Spotify (Out of Scope for v1)

Same as Android plan — do not implement in the first version. Plan for a later update (e.g. Spotify API, “now playing”). No Spotify UI in MVP.

---

## 8. Web Project Setup

- **Stack:** Choose one and stick to it: **React** (e.g. Vite + React), **Vue** (Vite + Vue), or **vanilla** (Vite + HTML/JS). TypeScript recommended.
- **Build:** Vite (or similar). Produce a static build that can be deployed to any static host (GitHub Pages, Netlify, Vercel, etc.).
- **PWA:** Use `vite-plugin-pwa` (or equivalent) to generate:
  - `manifest.webmanifest` (or `manifest.json`) with name, short_name, start_url, display standalone, icons, theme_color, background_color.
  - Service worker for caching and offline support.
- **State:** Single source of truth for workouts/calendar. Use React state + context, Vue reactivity, or a minimal store; sync with IndexedDB for persistence.
- **Routing:** Client-side routing (e.g. React Router, Vue Router) for Home, Workouts, New Workout, Active Workout, Quick Workout, Profile, Day view. URLs can be hash-based or history-based; ensure start_url and deep links work when launched from home screen.
- **iOS considerations:** 
  - `viewport` and `apple-mobile-web-app-capable`; `apple-mobile-web-app-status-bar-style`.
  - Safe area insets for notched devices (`env(safe-area-inset-*)`).
  - Icons for Apple touch icon (e.g. 180x180) in manifest and `<link rel="apple-touch-icon">`.

---

## 9. Distribution (Web / iOS Add to Home Screen)

- **Hosting:** Deploy the built static site to a public HTTPS URL (e.g. `https://yourusername.github.io/repsync-web/` or a custom domain).
- **No app store:** Users do not install from the App Store. They:
  1. Open the app URL in **Safari** on iOS.
  2. Tap the **Share** button.
  3. Tap **Add to Home Screen**.
  4. Name the icon (e.g. “RepSync”) and tap Add.
  5. The icon appears on the home screen; opening it launches the app in standalone mode (no Safari UI).
- **README:** Document the deploy URL and the steps above for iOS users. Optionally add a small in-app hint: “Add to Home Screen for the best experience.”

---

## 10. Repo Structure (Web App)

You may keep the web app in the same repo (e.g. `web/` or `repsync-web/`) or a separate repo. Suggested layout if in same repo:

```
RepSync/
├── docs/
│   ├── plan.md             # Android plan
│   ├── plan-web.md         # This file (web/iOS PWA plan)
│   └── ...
├── prompts.md               # Android prompts
├── prompts-web.md           # Web app prompts (phased)
├── assets/
│   ├── repSyncLogo.png     # Shared: logo (use for PWA icons)
│   └── references/         # Shared: design reference screens
├── web/                     # Web app (or separate repo)
│   ├── public/
│   │   ├── manifest.webmanifest
│   │   ├── icons/           # PWA icons from repSyncLogo.png
│   │   └── ...
│   ├── src/
│   │   ├── index.html
│   │   ├── main.tsx (or main.js)
│   │   ├── App.tsx
│   │   ├── theme/          # CSS variables matching design
│   │   ├── stores/ or services/  # IndexedDB, state
│   │   ├── components/
│   │   ├── screens/        # Home, Workouts, New Workout, etc.
│   │   └── ...
│   ├── package.json
│   ├── vite.config.ts
│   └── ...
├── app/                     # Android app (existing)
└── ...
```

- **Theme:** Define colors and typography in CSS (e.g. `src/theme/colors.css` or design tokens) to match `assets/references/` and plan Section 1.
- **Logo:** Use `assets/repSyncLogo.png` to generate PWA icons (e.g. 192x192, 512x512, 180x180 for Apple) and reference in manifest and `<link rel="apple-touch-icon">`.

---

## 11. Summary for Implementers

- **Parity:** The web app must be **functionally identical** to the Android app: same screens, same flows, same data model, same theme. Use the same design assets (`assets/repSyncLogo.png`, `assets/references/IMG_*.PNG`).
- **Platform:** Web PWA. Primary user path: iOS Safari → Add to Home Screen → launch as standalone app. Offline-capable after first load.
- **Storage:** IndexedDB only; no cloud or backend. Guest by default; optional local profile.
- **Deliverable:** A static web app that can be deployed to HTTPS and installed on iOS (and other devices) via Add to Home Screen. No Spotify in v1.

Use this plan together with `prompts-web.md` to implement RepSync Web from scratch and get it onto an iOS home screen.
