# Comment Style Guidelines

## HELP FOR UNDERSTAND THIS FILE

Where you see `[...]` you should replace it with the appropriate information. For example,
`[entity description]` should be replaced with a description of the entity.

Where you see param1, param2, etc., you should replace it with the appropriate parameter name. For
example,
`@param name` should be replaced with `@param id`. And after it with the description of the
parameter.

Where you see `ExceptionType` you should replace it with the appropriate exception type. For
example,
`IllegalArgumentException` or `IllegalStateException`.

## General Comments

```kotlin
/**
 * Brief description of the class, interface, or function.
 *
 * Detailed description of the class, interface, or function.
 * This can span multiple lines.
 */
```

## Repository Files

```kotlin
/**
 * Implementation of [Interface] interface.
 *
 * Handles data operations through the underlying [DataSource].
 *
 * @property param1 Description of param1.
 */
```

## Module Files

```kotlin
/**
 * Dagger Hilt module for providing dependencies.
 *
 * Installed in [Scope], provides [Dependency] and related dependencies.
 *
 * Responsibilities:
 * - Provides instances of use cases
 * - Ensures singleton instances where needed
 * - Injects dependencies into components
 */
```

## Model/DAO Files

```kotlin
/**
 * Represents [entity description].
 *
 * @property param1 Description of param1.
 * @property param2 Description of param2.
 * @property param3 Description of param3.
 */

/**
 * Data Access Object for [Entity].
 *
 * Provides CRUD operations for the [tableName] table.
 */
```

## Use Case Files

```kotlin
/**
 * Use case for [action description].
 *
 * Encapsulates the logic for [specific task], handling validation
 * and interacting with the repository.
 *
 * @property param1 Description of param1.
 */
```

## Function/Method Files

```kotlin
/**
 * [Function name] [brief description].
 *
 * @param param1 Description of param1.
 * @param param2 Description of param2.
 * @return Description of return value.
 * @throws ExceptionType When/why exception occurs.
 */
```

## UI-Component Files

```kotlin
/**
 * Composable that displays [component description].
 *
 * @param param1 Description of param1.
 * @param param2 Description of param2.
 */
```

## Event Files

```kotlin
/**
 * Sealed class representing events for [feature].
 *
 * Defines possible user interactions or system events
 * that can occur within the feature.
 */
```

## State Files

```kotlin
/**
 * Represents UI state for [screen/feature].
 *
 * @property param1 Description of param1.
 * @property param2 Description of param2.
 * @property param3 Description of param3.
 */
```

## Screen Files

```kotlin
/**
 * Screen for [feature description].
 *
 * @param param1 Description of param1.
 * @param param2 Description of param2.
 * @param param3 Description of param3.
 */
```

## ViewModel Files

```kotlin
/**
 * ViewModel for [screen/feature].
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property param1 Description of param1.
 * @property param2 Description of param2.
 */
```