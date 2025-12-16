# 🛍️ E-Commerce Android Application

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-green.svg)
![Hilt](https://img.shields.io/badge/DI-Hilt-orange.svg)
![Retrofit](https://img.shields.io/badge/Network-Retrofit-blue.svg)

## 📱 Overview

This is a modern, native Android E-Commerce application built with **Kotlin** and **Jetpack Compose**. The app follows the **MVVM architecture** and uses **Hilt** for dependency injection, ensuring a scalable, testable, and maintainable codebase.

It simulates a complete shopping experience including product browsing, cart management, user profiles, and order processing.

## ✨ Key Features

Based on the project structure, the app includes:

* **User Authentication:** Secure login and profile management.
* **Product Catalog:** Browse products with categories and details.
* **Shopping Cart:** Add/remove items (`SepetUrun`), update quantities.
* **Favorites:** Mark products as favorites for later.
* **Order Management:** View past orders (`Siparis`) and history.
* **Address Management:** Add or edit shipping addresses (`Adres`).
* **Search & Filtering:** Dynamic product search.

## 🏗️ Tech Stack & Architecture

The project leverages **Modern Android Development (MAD)** skills:

| Category | Technology |
| --- | --- |
| **Language** | Kotlin |
| **UI Toolkit** | Jetpack Compose |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Dependency Injection** | Hilt (Dagger) |
| **Networking** | Retrofit 2 & Gson |
| **Image Loading** | Coil |
| **Navigation** | Jetpack Navigation Compose |
| **Asynchronous** | Coroutines & Flow |

### 📂 Project Structure

The codebase is organized to support Clean Architecture principles:

* `data/`: Contains **Entity** classes (`Urun`, `SepetUrun`) and **Data Sources**.
* `repo/`: Repository layer (`UrunlerRepository`, `SepetRepository`) acting as a single source of truth.
* `di/`: Hilt modules for Dependency Injection.
* `ui/`: Composable screens and ViewModels.
* `retrofit/`: API interfaces and HTTP client configuration.

## 🚀 Installation

1.  **Clone the repo**
    ```bash
    git clone [https://github.com/zeysdzl/eticaretuygulamasi.git](https://github.com/zeysdzl/eticaretuygulamasi.git)
    ```
2.  **Open in Android Studio**
    * File -> Open -> Select the cloned folder.
    * Let Gradle sync the dependencies.
3.  **Run the App**
    * Select an Emulator or connect a physical device.
    * Press **Run** (Shift+F10).

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1.  Fork the project.
2.  Create your feature branch (`git checkout -b feature/NewFeature`).
3.  Commit your changes.
4.  Push to the branch and open a Pull Request.

## 📧 Contact

**Zeynep** - [LinkedIn Profile](https://www.linkedin.com/in/zeynepduzel/)

Project Link: [https://github.com/zeysdzl/eticaretuygulamasi](https://github.com/zeysdzl/eticaretuygulamasi)
