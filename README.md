# 💰 SmartBudget — Application Android de Gestion de Budget

![Android](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Room](https://img.shields.io/badge/Database-Room-blue)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange)

## 📱 Présentation

**SmartBudget** est une application Android *offline-first* de gestion de budget personnel.
Elle permet aux étudiants de **suivre leurs dépenses**, **visualiser où part leur argent**
par catégorie et par mois, et **gérer leurs habitudes financières** facilement.

---

## 🎯 Fonctionnalités

### ✅ Fonctionnalités principales
- **Gestion des dépenses (CRUD)** : Ajouter, modifier, supprimer une dépense
- **Catégorisation** : Alimentation, Transport, Logement, Santé, Loisirs, Études, Autre
- **Navigation par mois** : Voir les dépenses mois par mois (◀ ▶)
- **Filtrage** : Filtrer les dépenses par catégorie
- **Total du mois** : Affichage du total en temps réel
- **Statistiques** : Répartition par catégorie avec barres de progression et pourcentages
- **Paramètres** : Gérer les catégories (activer/désactiver/supprimer/ajouter)
- **Offline-first** : Toutes les opérations fonctionnent sans internet

---

## 🏗️ Architecture

L'application suit le pattern **MVVM (Model-View-ViewModel)** recommandé par Google.
UI (Fragments)
↕
ViewModel
↕
Repository
↕
Room DAO
↕
SQLite (local)
---

## 🛠️ Technologies utilisées

| Technologie | Utilisation |
|---|---|
| **Kotlin** | Langage principal |
| **Room** | Base de données locale (SQLite) |
| **LiveData** | Observation réactive des données |
| **ViewModel** | Gestion de l'état UI |
| **Navigation Component** | Navigation entre fragments |
| **Material Design** | Interface utilisateur |
| **Coroutines** | Opérations asynchrones |
| **RecyclerView** | Listes de dépenses |

---

## 🗄️ Modèle de données

### Expense (Dépense)
| Champ | Type | Description |
|---|---|---|
| id | Int | Identifiant unique |
| amount | Double | Montant (ex: 45.50) |
| currency | String | Devise (MAD par défaut) |
| date | Long | Date (timestamp) |
| categoryId | Int | Référence catégorie |
| note | String? | Note libre (optionnel) |
| paymentMethod | String? | Espèce / Carte / Virement |
| createdAt | Long | Date de création |
| updatedAt | Long | Date de modification |

### Category (Catégorie)
| Champ | Type | Description |
|---|---|---|
| id | Int | Identifiant unique |
| name | String | Nom unique |
| icon | String | Emoji icône |
| color | String | Couleur UI |
| isActivate | Boolean | Actif ou archivé |

---

## 📁 Structure du projet
```bash
ma.fstt.smartbuget/
├── data/
│   ├── dao/              → Requêtes Room (CategoryDao, ExpenseDao)
│   ├── database/         → AppDatabase (Singleton Room)
│   ├── entity/           → Modèles (Category, Expense)
│   ├── repository/       → Repositories (CategoryRepository, ExpenseRepository)
│   └── TestDataSeeder.kt → Données de test (30 dépenses)
├── ui/
│   ├── expenses/         → Écran liste + formulaire dépenses
│   ├── stats/            → Écran statistiques
│   └── settings/         → Écran paramètres
├── viewmodel/            → ViewModels (Expense, Category, Stats)
└── MainActivity.kt       → Activité principale + navigation
```
---

## 📸 Captures d'écran

| Dépenses | Statistiques | Paramètres |
|---|---|---|
| ![Dépenses](#) | ![Stats](#) | ![Paramètres](#) |

---

## 🚀 Installation

1. Clone le repository :
```bash
git clone https://github.com/salmaaz29/smartbuget.git
```

2. Ouvre le projet dans **Android Studio**

3. Synchronise Gradle : **Sync Now**

4. Lance sur un émulateur ou téléphone Android (API 24+) :
**Run ▶**

---

## 📋 Données de test

L'application insère automatiquement **30 dépenses de test** au premier lancement :
- **15 dépenses** sur Mars 2026
- **15 dépenses** sur Avril 2026
- Réparties sur **7 catégories** différentes

---

## 📄 Licence

Projet académique — Mini-projet Android — FSTT 2026
