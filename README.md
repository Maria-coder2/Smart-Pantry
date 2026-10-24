Smart Pantry



**1. Application Description**
Smart Pantry Manager is a Java-based Android application designed to help users reduce food waste by tracking the ingredients they have at home and suggesting recipes they can prepare using only those ingredients.
The application checks that every required ingredient is available in a sufficient quantity before suggesting a recipe.


**2. Technologies Used**
\- Java
\- Android Studio
\- XML layouts
\- SQLite
\- Git and GitHub

**3. Database Choice**
SQLite was selected because it is built into Android, stores information locally on the device, and does not require a separate database server or internet connection.
The database stores pantry ingredients, recipes, recipe requirements, and application settings.



**4. Main Features**
\- Add, view, edit and delete pantry ingredients.
\- Store ingredient quantities, units and optional expiry dates.
\- Store a collection of 20 recipes.
\- Suggest only recipes whose ingredient and quantity requirements are satisfied.
\- Display recipe ingredients and preparation instructions.
\- Save the expiry-alert preference.
\- Retain pantry records when the application is closed and reopened.

**5. Database Structure**
\- `pantry\_items`: stores pantry ingredients.
\- `recipes`: stores recipe names and preparation methods.
\- `recipe\_ingredients`: stores the ingredients and quantities required by each recipe.
\- `settings`: stores application preferences.

**6. Setup and Run Instructions**
1\. Open the project folder in Android Studio.
2\. Allow Gradle to synchronize the project dependencies.
3\. Create or start an Android emulator, or connect a compatible Android device.
4\. Run the application using the Run button.
5\. The SQLite database and initial recipe collection are created automatically on the first launch.

**7. Testing**
The application should be tested for pantry CRUD operations, database persistence, input validation, empty recipe results, ingredient normalization, compatible-unit conversion, and strict recipe matching.

**8. Version Control**
GitHub is used to track incremental development, record changes, and maintain the project's development history.





