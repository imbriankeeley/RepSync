# RepSync Web — Claude Code Prompt Guide (iOS PWA)

Use this guide to implement the **web app** version of RepSync step-by-step in Claude Code. **Always keep `docs/plan-web.md` open or in context** — it is the single source of truth for the web build. The app must be **functionally identical** to the Android app; iOS users install it via **Add to Home Screen** in Safari.

Copy the prompt for each phase into Claude Code. Each prompt is self-contained and includes requirements and deliverables for that phase.

---

## Design Assets (where to look)

Same as the Android app. All design references live under **`assets/`**:

| Asset | Path | Use for |
|-------|------|--------|
| App logo | `assets/repSyncLogo.png` | PWA icons, in-app branding |
| Screen mocks | `assets/references/IMG_1505.PNG` through `assets/references/IMG_1538.PNG` | Home, Workouts, New Workout, active workout, Quick Workout, Profile, Calendar, dialogs |

- **Theme and layout:** Match colors, spacing, rounded corners, and typography to the reference screens. Use CSS variables; touch targets ≥ 44px for iOS.
- **plan-web.md Section 1** lists exact color values; the PNGs show layout and hierarchy.

---

## How to Use

1. Start with **Phase 1** and work in order; later phases depend on earlier ones.
2. Before each phase, ensure the repo has the structure and files from previous phases.
3. **For each phase, reference the assets listed in that phase’s prompt** — open the relevant images to match layout and theme.
4. After each phase, verify the checklist items for that phase before moving on.
5. Choose one stack (e.g. React + Vite or Vue + Vite) in Phase 1 and use it consistently.

---

## Phase 1: Web Project Setup, Theme & PWA Shell

**Paste this prompt:**

```
I'm building RepSync Web — a PWA that is functionally identical to the Android RepSync workout app. Use docs/plan-web.md as the single source of truth. The Android spec is in docs/plan.md for flow reference; plan-web.md defines platform (web, IndexedDB, PWA, iOS Add to Home Screen).

Design assets to reference for this phase: assets/repSyncLogo.png (logo), and assets/references/IMG_1505.PNG through IMG_1538.PNG (all screen mocks for theme, colors, and layout). Open these and match theme/layout to them.

Do Phase 1 only:

1. Create the web project (e.g. Vite + React + TypeScript, or Vite + Vue + TypeScript — choose one and state which). Place it in a web/ folder (or as specified in plan-web.md Section 10). No backend; static build only.

2. Define the theme in CSS to match the design assets (plan-web.md Section 1, same as Android):
   - Background: dark charcoal / near-black (#1a1a1a or similar).
   - Cards & inputs: light grey rounded rectangles; text on cards dark grey/black.
   - Primary actions: muted sage/olive green (#8DAF8E, #7E9D7C) — e.g. "Add Exercise", "Finish Workout", "Quick Go", "Start Workout", checkmarks.
   - Destructive/cancel: muted red/pink for "Cancel" and header "X".
   - Text on dark: white or very light grey.
   - Corners: 12–16px for cards, 8px for buttons/inputs; typography: clean sans-serif, clear hierarchy.
   Use CSS variables. Ensure touch targets are at least 44px for iOS. Support safe-area-inset for notched devices.

3. PWA setup:
   - Web App Manifest: name, short_name, start_url, display "standalone", theme_color and background_color matching the app, icons (generate from assets/repSyncLogo.png — e.g. 192x192, 512x512, and 180x180 for Apple touch icon).
   - Service worker: use vite-plugin-pwa (or equivalent) to generate; cache static assets so the app works offline after first load.
   - In index.html: viewport, apple-mobile-web-app-capable, apple-mobile-web-app-status-bar-style, and <link rel="apple-touch-icon"> pointing to the 180x180 icon.

4. Add a minimal README in the web app folder (or root) with: one-line description (RepSync Web — iOS PWA), how to run dev (e.g. npm run dev), how to build (npm run build), and that iOS users can Add to Home Screen from Safari (details in a later phase).

Do not implement any screens or data layer yet — only project skeleton, theme, and PWA shell. Confirm stack choice (React+Vite or Vue+Vite) in your reply.
```

**Done when:** Project runs (npm run dev); theme matches design; manifest and service worker are in place; app is installable (Add to Home Screen works in Safari when served over HTTPS or localhost with flags). Logo used for PWA icons.

---

## Phase 2: Data Layer (IndexedDB & Models)

**Paste this prompt:**

```
RepSync Web — follow docs/plan-web.md. Phase 1 (project, theme, PWA shell) is done. Do Phase 2 only: local data layer with IndexedDB.

No UI assets needed this phase (data/models only). Keep assets/references/ in mind for future screens.

1. Add an IndexedDB wrapper (e.g. idb, Dexie, or similar) and define the same data model as the Android app (plan-web.md Section 4 = plan.md Section 4):
   - Workout (template): id, name, createdAt, list of exercises (order preserved via orderIndex).
   - Exercise: id, name, workoutId, orderIndex, list of sets.
   - Set: id, exerciseId, orderIndex, previousWeight (optional), previousReps (optional), weight, reps.
   - CompletedWorkout: id, name (or template id), date (calendar day), startedAt, endedAt (null if abandoned), list of exercises with sets and logged values. Support "Quick workout" via flag or naming (e.g. "Quick Workout" + date).

2. User model (plan-web.md Section 3): Guest by default. Optional local profile: display name, optional avatar (stored in IndexedDB, e.g. as data URL). No email/password or cloud.

3. Provide a way to query "previous" for an exercise: last logged weight/reps for that exercise from CompletedWorkout history (e.g. "135 lb x 5"). Same behavior as Android.

4. Single source of truth: all reads/writes go through this layer; UI will subscribe to it in later phases.

Do not build UI screens yet. Ensure the project still builds and the data layer can be exercised (e.g. from dev console or a simple test page).
```

**Done when:** IndexedDB stores workouts, exercises, sets, completed workouts, and user profile; "previous" for an exercise can be queried; no cloud.

---

## Phase 3: Home Screen & Navigation

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md is the spec. Phases 1 and 2 are done. Do Phase 3 only: Home screen and app navigation. The flows must match the Android app (docs/plan.md Section 5.1).

Design assets to reference for this phase: assets/references/ — use the screens that show Home (calendar month view), "Workouts" and "Quick Go" buttons, and bottom nav (Home | Profile). Match layout, spacing, and green highlight on days with workouts.

1. Set up client-side routing (e.g. React Router or Vue Router) so we have:
   - Home (default route)
   - Profile
   - Bottom nav: Home | Profile (active tab indicated). Match design assets for layout. Use hash or history mode; ensure start_url works when launched from home screen.

2. Home screen (plan-web.md Section 5.1 = plan.md 5.1):
   - Calendar: month view (e.g. "October 2024"), arrows to change month. Days with at least one completed workout get a muted green highlight. Tapping a day should eventually open Day view (placeholder or simple alert for now; full Day view is Phase 8).
   - Two main buttons: "Workouts" (navigate to Workouts list), "Quick Go" (navigate to Quick Workout — placeholder for now; Phase 6).
   - Bottom nav: Home | Profile. Match theme (dark background, rounded cards, muted green for primary buttons) per assets/references/ and plan-web.md Section 1.

3. Wire calendar to real data: days with completed workouts (from IndexedDB) must show the muted green highlight.

Implement only Home + bottom nav + navigation to Workouts and to a placeholder for Quick Go. Do not build Workouts list, Profile, or Active Workout yet.
```

**Done when:** Home shows calendar with green dots for days with workouts; Workouts and Quick Go buttons navigate correctly; bottom nav switches between Home and Profile (Profile can be a placeholder).

---

## Phase 4: Workouts List, Detail & New Workout (Templates)

**Paste this prompt:**

```
RepSync Web — follow docs/plan-web.md. Phases 1–3 are done. Do Phase 4 only: Workouts list, workout detail, and New Workout (create/edit template). Flows must match Android (docs/plan.md Sections 5.2, 5.3).

Design assets to reference for this phase: assets/references/ — use the screens for Workouts list (header, back, search, FAB "+"), workout detail (name, exercise list, "Start Workout" button, X), and New Workout (name input, exercise cards, Set | Previous | +lbs | Reps table, "Add Set", "Add Exercise" green button, Save). Match layout and styling.

1. Workouts list (plan-web.md Section 5.2):
   - Header: Back (to Home), title "Workouts", optional search (filter by name).
   - List: all saved workout templates from IndexedDB. Tap row → workout detail.
   - FAB or "+" button: "New Workout" → go to New Workout screen.

2. Workout detail (modal or dedicated route):
   - Workout name and list of exercises (e.g. "2 x Bench Press", "2 x Chest Press").
   - Primary button: "Start Workout" (muted green). Close (X). Start Workout → Active Workout screen (placeholder for now; Phase 5).

3. New Workout screen (plan-web.md Section 5.3):
   - Header: Back, "New Workout", "Save" (persists template to IndexedDB).
   - Name: single input (e.g. "Name: Push").
   - Exercises: per-exercise card with editable name; table Set | Previous | +lbs | Reps; "+ Add Set"; "Add Exercise" (green).
   - Previous: show last logged weight/reps for that exercise from history (use "previous" query from Phase 2), if any.
   - Save: store template in IndexedDB; user can start it later from Workouts.

Match the design assets in assets/references/ (Workouts + New Workout): dark theme, light grey cards, muted green primary actions, rounded corners. No cloud; all local.
```

**Done when:** Workouts list shows templates; search (optional) works; tap row → detail → Start Workout; New Workout allows name + exercises + sets with Previous and Save; data persists in IndexedDB.

---

## Phase 5: Active Workout (from Template)

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md is the spec. Phases 1–4 are done. Do Phase 5 only: Active Workout screen when starting from a template. Flow must match Android (docs/plan.md Section 5.4).

Design assets to reference for this phase: assets/references/ — use the screens for active workout (timer/stopwatch, workout name, X close), exercise/set table (Set, Previous, +lbs, Reps, "+ Add Set", checkmarks), "Add Exercise" and "Finish Workout" green buttons, and dialogs "Cancel Workout?" (Resume / Cancel) and "Finish Workout?" (Cancel / Finish). Match layout and destructive red/pink for Cancel/X.

1. Active Workout screen (plan-web.md Section 5.4):
   - Header: Timer (e.g. stopwatch + "00:01"), workout name (e.g. "Chest Workout"), X (close) on the right.
   - Timer: elapsed time. Optional: small control for rest/target duration — keep it simple.
   - Body: one section per exercise with table Set, Previous, +lbs, Reps; "+ Add Set" per exercise; green checkmark when set completed (if in design).
   - Actions: "Add Exercise" (green), "Finish Workout" (green, full-width).
   - Close (X): dialog "Cancel Workout?" with Resume and Cancel. Cancel discards session (do not save). Resume returns to workout.
   - Finish Workout: dialog "Finish Workout?" with Cancel and Finish. On Finish: save as CompletedWorkout for today, update profile workout count, return to Home.

2. Wire "Start Workout" from Workout detail (Phase 4) to this Active Workout screen with the selected template loaded. Pre-fill exercises/sets from template; "Previous" from history.

3. Optionally persist in-progress state (e.g. so closing the tab doesn’t lose the session); at minimum, Finish and Cancel must behave as above.

Match the design assets in assets/references/ (active workout + dialogs): muted green primary actions, destructive red/pink for Cancel/X, dark background, rounded cards. All local.
```

**Done when:** Starting a template opens Active Workout with timer and exercise/set table; Add Set and Add Exercise work; Cancel Workout and Finish Workout dialogs work; on Finish, data saves to IndexedDB and profile count updates; user returns to Home.

---

## Phase 6: Quick Workout

**Paste this prompt:**

```
RepSync Web — follow docs/plan-web.md. Phases 1–5 are done. Do Phase 6 only: Quick Workout flow. Same as Android (docs/plan.md Section 5.5).

Design assets to reference for this phase: same as Phase 5 — assets/references/ screens for active/Quick Workout (title "Quick Workout", same table and buttons). Reuse the same UI; only difference is no pre-filled exercises.

1. Quick Workout (plan-web.md Section 5.5):
   - Same UI as Active Workout; title "Quick Workout"; no pre-filled exercises; user adds all via "Add Exercise".
   - Timer, Add Set, Add Exercise, Finish Workout, Close (X) with "Cancel Workout?" — same as Phase 5.
   - On Finish: save as completed session for today (CompletedWorkout with Quick Workout naming/flag). Optionally "Save as template" in a later phase; for now just save.

2. Wire the "Quick Go" button on Home to this Quick Workout screen (replace placeholder from Phase 3).

3. Reuse the same Active Workout screen/logic; differentiate only by "from template" vs "quick" (no template).

Match theme and behavior to plan-web.md and assets/references/. No cloud.
```

**Done when:** Quick Go opens Quick Workout; user can add exercises and sets, finish or cancel; completed Quick Workout is saved for today and appears in history/calendar.

---

## Phase 7: Profile Screen

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md is the spec. Phases 1–6 are done. Do Phase 7 only: Profile screen. Same as Android (docs/plan.md Section 5.6).

Design assets to reference for this phase: assets/references/ — use the Profile screen (header "Profile", profile card with icon/placeholder, "Guest" or name, "X Workouts" count, chevron to settings). Match layout and card style.

1. Profile screen (plan-web.md Section 5.6):
   - Header: "Profile".
   - Card: profile icon (or placeholder), "Guest" or profile name, "X Workouts" (completed count from IndexedDB). Chevron (or tap) to open profile/settings.
   - Bottom nav: Home | Profile (Profile selected when on this screen).

2. Profile/settings (plan-web.md Section 3): local only — display name, optional avatar (stored in IndexedDB). No email/password or cloud. "Create profile" / "Set up profile" only affects local identity.

3. Workout count reflects total completed workouts (from CompletedWorkout); update when user finishes a workout (already done in Phase 5/6).

Match the Profile design in assets/references/: dark background, light grey card, muted green if needed. No sign-up or sync.
```

**Done when:** Profile shows Guest or name and completed workout count; tapping the card opens profile/settings (name, avatar); data is local only.

---

## Phase 8: Calendar Day View & Copy Workout

**Paste this prompt:**

```
RepSync Web — follow docs/plan-web.md. Phases 1–7 are done. Do Phase 8 only: Calendar day view and copy/create-from-day. Same as Android (docs/plan.md Section 5.7).

Design assets to reference for this phase: assets/references/ — use the Calendar and day view screens (tap day → day view, list of workouts for that day, "Copy to another day", "Use as template" / "Create workout from a day"). Match layout and actions.

1. Day view (plan-web.md Section 5.7):
   - From Home, tapping a calendar day opens Day view for that date.
   - Day view: list of workouts completed (or started) that day; for each: name, duration, exercises/sets if desired.
   - Option "Copy to another day": pick target date and create a copy (e.g. as CompletedWorkout or template for that day).
   - Option "Create workout from a day" / "Use as template": save that day's workout as a reusable template (use from Workouts list later). Includes quick workouts.

2. Implement "Copy to another day" and "Save as template" (from day view and, if desired, from Quick Workout completion) so the user can:
   - Copy a workout from one day to another.
   - Create a new workout template from a past day (including quick workouts).

Match the Calendar/day view design in assets/references/ and plan-web.md theme. All local; no cloud.
```

**Done when:** Tapping a day opens Day view with list of workouts; user can copy a workout to another day and create a template from a day's workout; Quick Workout can be saved as template if desired.

---

## Phase 9: PWA Polish, iOS Add to Home Screen & README

**Paste this prompt:**

```
RepSync Web — docs/plan-web.md is the spec. Phases 1–8 are done. Do Phase 9 only: PWA polish, iOS install instructions, and README.

No new UI this phase. Repo structure (plan-web.md Section 10) includes docs/, prompts-web.md, assets/repSyncLogo.png, assets/references/, web/ app. Do not delete or move design assets.

1. PWA polish:
   - Ensure manifest has correct start_url, display standalone, icons (including Apple touch icon), theme_color, background_color.
   - Service worker caches app shell and (as appropriate) static assets so the app works offline after first load. Test that Add to Home Screen still works and app launches in standalone mode.

2. Document for iOS users (in README and optionally a short in-app hint):
   - Open the app URL in Safari on iOS.
   - Tap Share → Add to Home Screen.
   - Name the icon (e.g. "RepSync") and tap Add.
   - Launch from home screen for full-screen app experience.
   Mention that data is stored only on the device and no account is required.

3. Update README (web app or root) to include:
   - Short description: RepSync Web — same app as Android, installable on iOS via Add to Home Screen.
   - How to run dev (e.g. npm run dev in web/).
   - How to build (npm run build) and where output is (e.g. web/dist).
   - How to deploy (e.g. deploy dist to GitHub Pages, Netlify, or any static host over HTTPS).
   - iOS install steps (as above). Note: HTTPS required for service worker and Add to Home Screen.

4. Confirm repo structure matches plan-web.md Section 10. Do not add Spotify or cloud; plan-web.md Section 7 says Spotify is future only.
```

**Done when:** PWA is installable on iOS via Add to Home Screen; app works offline after first load; README has dev, build, deploy, and iOS install instructions; repo structure is correct.

---

## Full MVP Checklist (from plan-web.md Section 6)

After all phases, confirm:

- [ ] Guest by default; optional local profile (name, avatar).
- [ ] All data stored only in browser/device (IndexedDB).
- [ ] Home: calendar with green dots for days with workouts; Workouts + Quick Go buttons; bottom nav Home | Profile.
- [ ] Workouts: list templates, search (optional), FAB "+", tap row → detail → Start Workout.
- [ ] New Workout: name, add exercises, sets (Previous, +lbs, Reps), Save.
- [ ] Active workout: elapsed timer, optional set timer, add set, add exercise, finish workout, close (X) with "Cancel Workout?" (Resume / Cancel).
- [ ] "Finish Workout?" dialog (Cancel / Finish); on Finish, save to history and increment profile count.
- [ ] Quick Workout: same as active workout, no pre-filled exercises.
- [ ] Profile: show Guest or name, completed workout count, entry to profile/settings.
- [ ] Calendar: tap day → day view; view past workouts; copy workout from one day to another; create template from a day's workout.
- [ ] PWA: installable on iOS via Add to Home Screen; works offline after first load; standalone display.

---

## One-Shot Full Build Prompt (Optional)

If you prefer to give Claude the whole scope in one go, use the following. Prefer the phased prompts above for better control.

```
Build RepSync Web from scratch using docs/plan-web.md as the single source of truth. The app must be functionally identical to the Android RepSync app (docs/plan.md for flows). Design assets: assets/repSyncLogo.png and assets/references/IMG_1505.PNG through IMG_1538.PNG. Match layout, spacing, and theme: dark background, muted sage green primary actions, light grey cards, rounded corners, clean typography.

Requirements:
- Web PWA (Vite + React or Vue + TypeScript). Static build; no backend. IndexedDB for all data (same model as Android: Workout, Exercise, Set, CompletedWorkout, User). Guest by default; optional local profile.
- PWA: Web App Manifest (standalone, icons from repSyncLogo.png, theme_color, background_color), service worker for offline. iOS: Add to Home Screen from Safari; support safe-area and touch targets ≥ 44px.
- Implement all flows in plan-web.md Section 5 (same as plan.md 5.1–5.7): Home, Workouts list + detail + New Workout, Active Workout, Quick Workout, Profile, Calendar day view with copy-to-day and create-template-from-day.
- Feature checklist: plan-web.md Section 6. No Spotify in v1 (Section 7).
- README: how to run dev, build, deploy (HTTPS), and iOS Add to Home Screen steps.

Deliver a web app that can be deployed to HTTPS and installed on iOS home screen with identical behavior to the Android app.
```

---

*End of prompt guide. Keep docs/plan-web.md in context for every phase, and reference assets in assets/ (repSyncLogo.png) and assets/references/ (IMG_1505.PNG–IMG_1538.PNG) when building UI.*
