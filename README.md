# Sprintify

## Descriere generală 
- [demo video]([https://www.youtube.com/watch?v=QC5UOMz4tto](https://www.youtube.com/watch?v=h1O4onwRKA4))
- Sprintify este o aplicație mobilă inovatoare destinată comunității de alergători, oferind servicii avansate de geolocalizare direct de pe telefonul utilizatorului.
- Prin utilizarea tehnologiei GPS, Sprintify permite alergătorilor de toate nivelurile să urmărească în timp real distanța parcursă, ritmul și timpul de alergare. Mai mult decât atât, aplicația se distinge prin posibilitatea de personalizare a circuitelor, permițând utilizatorilor să creeze sau să selecteze trasee adaptate preferințelor. 
- Sprintify promovează interacțiunea socială prin competiții, contribuind la creșterea motivării și la întărirea comunității de alergători. În plus, securitatea utilizatorilor este o prioritate, oferind trasee sigure pentru alergările nocturne. Cu o interfață de utilizare modernă, bazată pe Jetpack Compose, și integrând tehnologii precum Firebase și Google Maps API, Sprintify este concepută pentru a fi o platformă scalabilă, gata să satisfacă nevoile în continuă expansiune ale alergătorilor.


## Obiective Generale

- **Urmărirea Activității:** Permiterea alergătorilor de toate nivelurile să urmărească distanța, ritmul și timpul parcurs.
- **Personalizarea Circuitelor:** Utilizatorii pot alege sau crea trasee personalizate în funcție de preferințele lor.
- **Interacțiune Socială:** Încurajarea comunității prin competiții, grupuri de alergare și evenimente.
- **Securitate:** Oferirea de trasee sigure pentru alergătorii nocturni.

## Funcționalități Principale

1. **Urmărire în Timp Real:** Utilizarea GPS pentru a oferi feedback în timp real privind performanța alergării.
2. **Crearea și Personalizarea Circuitelor:** Opțiuni de filtrare după distanță, durată, dificultate și tipul terenului.
3. **Pauză și Repornire:** Flexibilitate pentru gestionarea pauzelor în timpul alergării.
4. **Competiții și Socializare:** Componentă socială pentru participarea la competiții și evenimente.
5. **Trasee de Alergare Personalizabile:** Posibilitatea de a adăuga trasee proprii, cu opțiunea de a le face publice sau private.
6. **Evaluarea și Salvarea Traseelor:** Funcționalități pentru vizualizarea, evaluarea și salvarea traseelor preferate.
7. **Administrare:** Interfață de administrare pentru gestionarea utilizatorilor și traseelor.

## Tehnologii și Arhitectură

- **Limbaj de Programare:** Kotlin.
- **UI Toolkit:** Jetpack Compose, pentru o interfață utilizator modernă și reactivă.
- **Backend:** Firebase sau alte soluții pentru stocarea datelor și autentificare.
- **API-uri Externe:** Google Maps API pentru afișarea și crearea traseelor pe hartă.
- **Securitate:** Implementarea celor mai bune practici pentru protecția datelor utilizatorilor.

## Dezvoltare și Scalabilitate

Proiectul "Sprintify" va fi dezvoltat în iterații, începând cu funcționalitățile de bază și adăugând treptat caracteristici avansate. Conceput să fie scalabil, permite extinderea ușoară a funcționalităților pentru a satisface nevoile unei comunități în creștere.


## Livrabil intermediar

### Completed User Stories

1. 👑 **As a casual runner, I want to track my distance, pace, and time during my runs.**
2. 🐸 **As a casual runner, I want to choose my circuits for running.**
3. 😎 **As a casual runner, I want to be able to choose my workout based on distance, time, and difficulty.**
4. **As a casual runner, I want to start my own run and choose the distance.**
5. 🕰️ **As a casual runner, I want to be able to pause my runs in case I get tired, or something happens.**
6. ⚜️ **As a user, I want a special section for my saved routes (favorites), whether they are standard or custom routes.**
7. 👥 **As a social runner, I want to join running groups or events through the app so I can meet others with similar running interests.**
8. 🐕 **As a runner, I want the option to choose pet-friendly routes.**
9. ☎️ **As a user, I want to see the rating of custom user routes when viewing them.**

### Partially Completed User Stories

- **User Stories 7, 10, and 11** were partially implemented. We encountered some difficulties and decided to concentrate on the basic functionalities, postponing UI and UX improvements.
- **User Stories 6 and 8** are frontend-related and can be easily added; however, we decided they were not essential at this stage.
- **User Story 9** was not completed due to a lack of user role implementation.

### Future Development Plan

We plan to complete all the remaining user stories in phases:
1. **Phase 1**: Finalize partially implemented features.
2. **Phase 2**: Implement easily achievable frontend improvements.
3. **Phase 3**: Add remaining advanced features when necessary.

## Project Contributors

This project was initially developed by **Serban Andrei**, **Maftei Stefan**, **Botezatu Cosmin-Adrian**, and **Papuc Stefan Eduard**. Recently, **Emanoil-Bogdan Protopescu** joined the team.

Each team member contributed to core development, with some specialized responsibilities:
- **Maftei Stefan** focused on Frontend development.
- **Papuc Stefan** worked on navigation and map integration.
- **Botezatu Cosmin** conducted testing and built community features.
- **Serban Andrei** designed the app architecture and handled Backend development.

With the new addition, we plan to distribute responsibilities further to maintain effective collaboration.

## Technical Overview

- **IDE**: Android Studio
- **Programming Language**: Kotlin
- **Libraries & Frameworks**:
  - Jetpack Compose for UI development
  - Jetpack Libraries for lifecycle management and UI rendering
- **APIs**:
  - Google Maps API for location and map services
  - Firebase for backend services

### Development Challenges

Using Android industry standards such as Kotlin, Jetpack Compose, and Jetpack Libraries simplified data management, UI rendering, and background tasks, helping us create a robust, maintainable app. However, as our first major project, we only refactored superficially, which introduced bugs and complexities later in the process.

### Repository

Visit our GitHub repository for more information: [Sprintify on GitHub](https://github.com/SerbanA01/Sprintify)

