# 📱 Messenger App — Kotlin + Firebase

A full-featured Android messenger app built with Kotlin, XML layouts, Firebase Realtime Database, and MVVM architecture. Supports 1-on-1 chats, groups, and channels with a modern dark UI.

---

## ✨ Features

| Feature | Details |
|---|---|
| 🔐 Login | Phone number login, saved to Firebase, no OTP |
| 💬 Chats | Real-time 1-on-1 messaging with bubble UI |
| 👥 Groups | Create groups, add members, all can send |
| 📢 Channels | Admin-only broadcast, subscribers read-only |
| 🌙 Dark Theme | Deep dark background, purple accents |
| ⚡ Real-time | Firebase Realtime Database listeners |
| 🏗️ MVVM | ViewModel + Repository + StateFlow |

---

## 🗂️ Project Structure

```
app/src/main/java/com/messenger/app/
├── data/
│   ├── model/          # User, Message, Chat, Group, Channel
│   ├── repository/     # ChatRepo, GroupRepo, ChannelRepo, UserRepo
│   └── firebase/       # FirebaseManager (singleton refs)
├── ui/
│   ├── auth/           # LoginActivity + LoginViewModel
│   ├── home/           # HomeActivity, HomePagerAdapter, HomeViewModel
│   ├── chat/           # ChatActivity, ChatsFragment, MessagesAdapter, ChatsAdapter, ChatViewModel
│   ├── group/          # GroupChatActivity, GroupsFragment, CreateGroupActivity, GroupViewModel
│   ├── channel/        # ChannelActivity, ChannelsFragment, CreateChannelActivity, ChannelViewModel
│   └── common/         # NewChatBottomSheet, UserPickerAdapter
└── utils/
    ├── SessionManager.kt
    └── Extensions.kt

app/src/main/res/
├── layout/             # All XML layouts
├── drawable/           # Shapes, selectors, vectors
├── values/             # colors, strings, themes, dimens
├── color/              # tab_icon_selector
└── menu/               # home_menu
```

---

## 🚀 Setup Instructions

### Step 1 — Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click **Add project** → name it (e.g. `MessengerApp`)
3. Disable Google Analytics (optional) → **Create project**

### Step 2 — Add Android App

1. In your project, click the **Android** icon
2. Package name: `com.messenger.app`
3. App nickname: `Messenger`
4. Click **Register app**
5. Download `google-services.json`
6. **Replace** `app/google-services.json` with the downloaded file

### Step 3 — Enable Realtime Database

1. In Firebase Console → **Build** → **Realtime Database**
2. Click **Create Database**
3. Choose a location → **Start in test mode**
4. Click **Enable**

### Step 4 — Set Database Rules

In Realtime Database → **Rules** tab, paste:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

> ⚠️ These are open rules for development. Add proper auth rules before production.

### Step 5 — Build & Run

```bash
# Open in Android Studio
File → Open → select MessengerApp/

# Sync Gradle
# Run on device or emulator (API 24+)
```

---

## 🎨 UI Design Decisions

| Element | Value |
|---|---|
| Background | `#0D0D0D` (near black) |
| Surface | `#1A1A1A` |
| Primary | `#7C4DFF` (deep purple) |
| Accent | `#00E5FF` (cyan) |
| Sent bubble | Purple `#7C4DFF`, right-rounded |
| Received bubble | Dark `#2A2A2A`, left-rounded |
| Font | System sans-serif |
| Corner radius | 18dp bubbles, 14dp buttons, 12dp cards |

---

## 🏗️ Architecture

```
UI (Activity/Fragment)
    ↓ observes
ViewModel (StateFlow / LiveData)
    ↓ calls
Repository
    ↓ reads/writes
Firebase Realtime Database
```

- **MVVM** — ViewModels survive rotation, expose StateFlow
- **Repository pattern** — all Firebase logic isolated from UI
- **Coroutines + Flow** — async Firebase operations with `callbackFlow`
- **ViewBinding** — no `findViewById`, type-safe views

---

## 📦 Dependencies

```gradle
// Firebase
firebase-database-ktx      // Realtime Database
firebase-auth-ktx           // Auth (anonymous / future)

// Lifecycle
lifecycle-viewmodel-ktx     // ViewModel
lifecycle-livedata-ktx      // LiveData

// UI
material:1.11.0             // MaterialComponents, TabLayout, FAB
constraintlayout            // All main layouts
viewpager2                  // Home tab pager

// Coroutines
kotlinx-coroutines-android
kotlinx-coroutines-play-services  // .await() on Firebase Tasks
```

---

## 🔧 Key Files Reference

| File | Purpose |
|---|---|
| `SessionManager.kt` | SharedPrefs: saves uid, phone, name |
| `FirebaseManager.kt` | Singleton Database references |
| `Extensions.kt` | `toTimeString()`, `toInitials()`, `show/hide` |
| `MessagesAdapter.kt` | Dual ViewHolder sent/received bubbles |
| `UserPickerAdapter.kt` | Checkbox multi-select or single-click user list |
| `NewChatBottomSheet.kt` | Bottom sheet to pick user and open/create chat |

---

## 🔮 Future Improvements

- [ ] Firebase Auth (phone OTP)
- [ ] Image messages (Firebase Storage)
- [ ] Push notifications (FCM)
- [ ] Message read receipts
- [ ] Online/offline presence indicators
- [ ] Message search
- [ ] User profile editing
- [ ] End-to-end encryption

---

## ⚠️ Notes

- The `google-services.json` in the repo is a **placeholder** — replace it with your own
- Firebase rules are open for development — restrict them before shipping
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
