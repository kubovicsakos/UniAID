# Naming Conventions

- **File Names**
    - Use PascalCase for file names (e.g. `MainActivity.kt`, `SubjectDetailsViewModel.kt`).

- **Package Names**
    - Use lowercase letters (e.g. `com.example.app`, `com.example.data`).
    - Try to avoid multi word package names but if necessary, use camelCase 
      (e.g. `com.example.app.newFeature`).

- **Classes, Interfaces and Objects**
    - Use PascalCase (e.g. `MainActivity`, `SubjectDetailsViewModel`).

- **Test Classes**
    - Mirror the name of the class under test, adding `Test` suffix (e.g. `SubjectUseCasesTest`).

- **Functions and Variables**
    - Use CamelCase (e.g. `showSnackbar`, `currentSubjectId`).
    - Avoid abbreviations (e.g. use `subjectId` instead of `subId`).

- **Private Variables**
    - Use `_` prefix (e.g. `_subjectId`) for private variables.

- **Constants**
    - Use CAPS letters with underscores (e.g. `MAX_SIZE`).

- **Preference keys**
    - Use snake_case (e.g. `user_id`, `chosen_color`).