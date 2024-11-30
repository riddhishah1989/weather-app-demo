# Weather Forecast App
This is a demo weather forecast app that showcases best practices in modern Android development. The app is designed to fetch and display real-time weather data, including current weather conditions and a 5-day forecast for any searched city. It is built using MVVM architecture, with a focus on clean and modular code.

# Features

🌤️ Current Weather: Displays real-time weather conditions for the user's location or a searched city.

📅 5-Day Forecast: Provides a detailed 5-day weather forecast for selected locations.

📦 Offline Support: Utilizes Room Database for caching weather data to ensure usability without an active internet connection.

🎨 Modern UI: Clean and simple user interface, leveraging Jetpack Compose and Material Design components.

📍 Location-Based Weather: Fetches weather data based on the user's current location.

🔄 Periodic Updates: Weather data is refreshed automatically after 30 minutes to ensure it remains up-to-date.

🛠️ Error Handling: Displays appropriate error messages for scenarios like no internet connection or invalid search queries.

# Technologies Used
This app is developed using the latest tools and libraries in Android development:

Kotlin: For modern and concise programming.

MVVM Architecture: Ensures a clean separation of concerns and testability.

Jetpack Compose: Implements parts of the UI for declarative and reactive design.

Room Database: Caches weather data for offline access.

Retrofit: Handles API calls with efficient JSON parsing.

Kotlin Coroutines: Manages background tasks and API calls seamlessly.

LiveData: Observes and updates the UI with real-time data changes.

Navigation Component: Simplifies navigation between screens.

DataBinding: Binds views for type-safe and null-safe access.

# API
This app uses the OpenWeatherMap API to fetch weather data.

# Folder Structure
![image](https://github.com/user-attachments/assets/30765fa1-45f6-446e-8de6-2a934b04165a)

