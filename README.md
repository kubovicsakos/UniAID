# UniAID: A University Study App
UniAID is a native Android app written in Kotlin, aimed at hungarian university students giving them the opportunity to manage different aspects of their study life within one app. Its streamlined user interfaces allow the use of three main functions: note taking, study calendar management, subject and statistics management.

## Downloads

Download the [latest version of UniAID](https://github.com/kubovicsakos/UniAID/releases/latest).

## System requirements:
### Software:
- Operating system: Android 10+
- Permissions: There are no addtional permissions needed.
### Recomended hardware: 
- CPU: 1.5 GHz with 4 or more cores 
- RAM: 2GB or more
- Storage: min 50MB or more
- Display: 5.5"+ size with, minimum 720*1280 resolution
### Network:
- Internet connectivity is not needed beyond downloading the app.

## Installation Guide
Installation and Update Steps
Currently, the app can only be installed via APK file. This may change in future updates.

### Step 1: Enable Unknown Sources
1. Open Settings on your phone
2. Navigate to "Security & Privacy" or "Apps & notifications" (may vary by device)
3. Select "Install unknown apps"
4. Find the app you'll use for installation (e.g. Chrome, Files)
5. Toggle "Allow from this source" on (For security reasons, turn this off after installation)
### Step 2: Install the APK
1. Download the UniAID.apk file
2. Open your File Manager or Downloads app
3. Locate and tap on the APK file
4. Tap "Install" when prompted
5- Wait for installation to complete (typically a few seconds)
6. The app will appear in your app drawer or home screen when finished
### Step 3: Updating the App
To update an existing installation:

- Follow the same process as installation
- The system will recognize it as an update rather than a new installation

## Features
### Notes
- Create, edit, study notes. 
- Toggle between light and dark themes for comfortable reading and editing
- Associate notes with specific subjects for better organization
- Sort notes by title and last modified date
### Calendar
- Manage academic schedule and deadlines
- Create events with detailed information (title, location, time)
- Associate events with specific subjects
- Support for recurring events
### Subjects
- Track all academic subjects across multiple semesters
- Record final grades, credits, and other subject details
- View subject-specific notes and events
### Statistics
- Track academic performance with detailed statistics
- View weighted grade averages
- Monitor completed vs. committed credits
## Technical Details
UniAID is built with modern Android development practices:

- **Architecture**: Clean Architecture with separate domain, data, and presentation layers
- **UI**: Built entirely with Jetpack Compose and Material 3 for a modern, responsive interface
- **Database**: Room persistence library for robust local data storage
- **Preferences**: DataStore for user settings
- **State Management**: MVVM pattern with ViewModels and state flows
- **Dependency Injection**: Hilt for clean dependency management
- **Testing**: Comprehensive unit tests

## Getting Started
To use UniAID effectively:

- Add your subjects with relevant details (name, credits, semester)
- Create notes and associate them with your subjects as needed
- Add lectures and other repeating events, and important dates or deadlines to your calendar
- Track your grades as you progress through your studies
- Use the statistics dashboard to monitor your academic progress

## Credits

This project was made by Kubovics Ákos as his thesis work. Further development is possible.
