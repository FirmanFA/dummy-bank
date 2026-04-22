# Dummy Banking App

A modern, robust Android banking simulation app built with Jetpack Compose. This project demonstrates best practices in Android development, including Clean Architecture, MVVM, Dependency Injection, and reactive UI patterns.

## 🚀 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Declarative UI)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: Hilt (Dagger-based)
- **JSON Parsing**: Moshi (Reflective & Kotlin-friendly)
- **Image Loading**: Painter Resource (Local vectors)
- **Navigation**: Jetpack Navigation Compose
- **Concurrency**: Kotlin Coroutines & Flow
- **Pagination**: Paging 3 (for transaction history)
- **UI Components**: Material Design 3 (M3)

---

## 📱 Page-by-Page Breakdown

### 1. Login Screen (`LoginScreen.kt`)
- **Purpose**: Authenticates the user against a simulated database.
- **Mechanism**: Uses `AuthViewModel` to manage `LoginUiState`. It performs field validation (username not blank, password length) and handles the "Loading" and "Error" states gracefully.
- **Benefit**: Decouples UI from authentication logic. `LaunchedEffect` ensures navigation happens exactly once upon success.
- **Alternative**: `Activity`-based login. **Why not?** It makes sharing state and navigation harder in a modern Compose-only app.
- **Possible Bug**: Lack of "Hide Password" toggle state persistence across configuration changes if not handled correctly (though currently handled via `remember`).

### 2. Home Screen (`HomeScreen.kt`)
- **Purpose**: The user's dashboard showing balance, quick actions (Transfer, QRIS), and recent transactions.
- **Mechanism**: Uses `HomeViewModel` to fetch both user data and a preview of transactions. Features a dynamic scroll-aware header using `derivedStateOf`.
- **Benefit**: High performance through `LazyColumn`. Dynamic UI changes (like the fading toolbar) improve UX.
- **Alternative**: `ScrollView` with a `Column`. **Why not?** It loads all items into memory at once, which fails as transaction lists grow.
- **Possible Bug**: If the `transactions` list in the repository is modified while the user is on the screen, the "Recent" list might get out of sync without a Refresh mechanism (Swipe-to-refresh).

### 3. Transfer Screen (`TransferScreen.kt`)
- **Purpose**: Allows users to send funds to other accounts.
- **Mechanism**: Input validation for account numbers (numeric only) and amounts. It features a two-step confirmation dialog before executing the "API" call.
- **Benefit**: The confirmation dialog prevents accidental transfers—a critical banking requirement.
- **Alternative**: Single-page form without confirmation. **Why not?** High risk of user error leading to financial loss in a real app.
- **Possible Bug**: **Double-click vulnerability**. In production, the "Transfer" button should be disabled immediately after the first click to prevent duplicate transactions if the network is slow.

### 4. Transfer Success Screen (`TransferSuccessScreen.kt`)
- **Purpose**: Provides visual confirmation of a completed transaction.
- **Mechanism**: Receives data via Navigation arguments (SafeArgs-like pattern).
- **Benefit**: Clearly separates the "Action" phase from the "Result" phase, making the app feel more professional.
- **Alternative**: Showing a Toast or Snackbar on the Home screen. **Why not?** Banking users expect a "Receipt" they can screenshot or verify.

### 5. History Screen (`HistoryScreen.kt`)
- **Purpose**: Displays a paginated list of all transactions.
- **Mechanism**: Implements **Paging 3** with a custom `PagingSource`.
- **Benefit**: Handles large datasets efficiently by only loading what's visible. Built-in support for loading/error states in the list.
- **Alternative**: Manual list loading with `onScroll` listeners. **Why not?** Paging 3 handles edge cases (like retries and memory management) much better than manual implementations.
- **Possible Bug**: Jumping to the top of the list when data refreshes if the `diffCallback` isn't perfectly implemented.

---

## 🛠 Architectural Decisions: Pros & Cons

| Decision | Pros | Cons |
| :--- | :--- | :--- |
| **Hilt (DI)** | Clean code, easy to swap mock repositories for real ones, automated scoping. | Slightly increased build times and learning curve for new developers. |
| **Moshi over Gson** | Better Kotlin integration (Null safety), faster performance, avoids "magical" reflection issues. | Requires more setup (adapters) for complex custom types. |
| **StateFlow (UDF)** | Unidirectional Data Flow makes the UI predictable and easy to debug. | Requires "Boilerplate" classes for UI states (Loading, Success, Error). |
| **SessionManager** | Centralized auth state. | Uses `SharedPreferences` which isn't encrypted by default (Production risk). |

---

## ⚠️ Real-World Production Considerations

1.  **Security**:
    - **Current**: Plaintext `SharedPreferences`.
    - **Production**: Must use `EncryptedSharedPreferences` and Biometric authentication (Fingerprint/FaceID).
2.  **Sensitive Data**:
    - **Current**: Password is handled as a plain `String`.
    - **Production**: Should be cleared from memory as soon as possible and never stored in logs.
3.  **Error Handling**:
    - **Current**: Simple `Exception` messages.
    - **Production**: Needs localized error codes (e.g., "NSF" for Insufficient Funds) and retry logic with exponential backoff.
4.  **Analytics**:
    - **Current**: None.
    - **Production**: Every screen view and transaction attempt should be logged to tools like Firebase Analytics for auditing.
5.  **Offline Support**:
    - **Current**: Mocked data.
    - **Production**: Use **Room Database** as a local cache to allow users to see their balance/history without internet.

---

## 📂 Project Structure

- `ui/`: Compose screens and reusable components.
- `viewmodel/`: State management and business logic orchestration.
- `repository/`: Data fetching logic (Local vs Remote).
- `model/`: Data classes and Domain entities.
- `di/`: Hilt modules for dependency provision.
- `utils/`: Formatters and Session managers.
