# RepSync Release Notes

---

## v0.1.0-alpha (First Alpha)

**Release date:** *(fill in when you publish)*

This is the first alpha of RepSync: an offline-first Android workout app. We’re releasing early so you can try it and report issues. Expect rough edges and possible bugs.

### What’s in this release

- **No account needed** — Use the app as a guest. Optionally set a local profile (display name, avatar) from the Profile screen; everything stays on your device.
- **Workout templates** — Create workouts with exercises and sets (weight + reps). Save them and start from a template anytime.
- **Quick Workout** — Start a session without a template and add exercises on the fly.
- **Active workout** — Timer, log sets (with “Previous” from your history), add exercises/sets, then finish or cancel (with confirmation dialogs).
- **Calendar** — Month view with completed workouts highlighted. Tap a day to see what you did, copy a workout to another day, or save a day’s workout as a template.
- **Profile** — See “Guest” or your name and total completed workout count; open profile/settings to set name and avatar (local only).
- **Local only** — All data is stored on the device (Room/SQLite). No cloud, no sign-in.

### Requirements

- Android only. Min SDK 26; target SDK 35.
- No Google Play or account required.

### Installation (Obtainium)

1. Install [Obtainium](https://github.com/ImranR98/Obtainium).
2. Add source: this repo’s URL (e.g. `https://github.com/<your-username>/RepSync`).
3. Install or update from the latest release.

You can also download the APK from the **Assets** section of this release and install it manually (enable “Install from unknown sources” if needed).

### Known limitations (alpha)

- This is an early build. Some flows may be incomplete or buggy.
- No cloud sync or backup in this version.
- Spotify integration is planned for a future release and is not included here.

### Feedback

Please open issues in the repository for bugs or feature ideas. Thanks for trying RepSync.
