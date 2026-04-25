package ma.fstt.smartbuget.data

import ma.fstt.smartbuget.data.dao.CategoryDao
import ma.fstt.smartbuget.data.dao.ExpenseDao
import ma.fstt.smartbuget.data.entity.Expense
import java.util.Calendar

object TestDataSeeder {

    suspend fun seedIfEmpty(categoryDao: CategoryDao, expenseDao: ExpenseDao) {

        // Récupérer les catégories directement (sans LiveData)
        val categories = categoryDao.getAllCategoriesOnce()
        if (categories.isEmpty()) return

        val catMap = categories.associate { it.name to it.id }

        val alimentation = catMap["Alimentation"] ?: return
        val transport    = catMap["Transport"]    ?: return
        val logement     = catMap["Logement"]     ?: return
        val sante        = catMap["Santé"]        ?: return
        val loisirs      = catMap["Loisirs"]      ?: return
        val etudes       = catMap["Études"]       ?: return
        val autre        = catMap["Autre"]        ?: return

        // Mars 2026
        val depensesMars = listOf(
            Expense(amount = 500.0,  date = dateOf(2026,2,1),  categoryId = logement,     note = "Loyer Mars",         currency = "MAD"),
            Expense(amount = 45.0,   date = dateOf(2026,2,2),  categoryId = alimentation, note = "Courses supermarché",currency = "MAD"),
            Expense(amount = 12.0,   date = dateOf(2026,2,3),  categoryId = transport,    note = "Bus",                currency = "MAD"),
            Expense(amount = 80.0,   date = dateOf(2026,2,5),  categoryId = alimentation, note = "Restaurant",         currency = "MAD"),
            Expense(amount = 200.0,  date = dateOf(2026,2,6),  categoryId = etudes,       note = "Livres cours",       currency = "MAD"),
            Expense(amount = 35.0,   date = dateOf(2026,2,8),  categoryId = transport,    note = "Taxi",               currency = "MAD"),
            Expense(amount = 150.0,  date = dateOf(2026,2,10), categoryId = loisirs,      note = "Cinéma + sortie",    currency = "MAD"),
            Expense(amount = 60.0,   date = dateOf(2026,2,12), categoryId = sante,        note = "Pharmacie",          currency = "MAD"),
            Expense(amount = 90.0,   date = dateOf(2026,2,13), categoryId = alimentation, note = "Courses semaine",    currency = "MAD"),
            Expense(amount = 25.0,   date = dateOf(2026,2,15), categoryId = transport,    note = "Carburant",          currency = "MAD"),
            Expense(amount = 300.0,  date = dateOf(2026,2,17), categoryId = etudes,       note = "Inscription examen", currency = "MAD"),
            Expense(amount = 55.0,   date = dateOf(2026,2,19), categoryId = alimentation, note = "Café + snacks",      currency = "MAD"),
            Expense(amount = 100.0,  date = dateOf(2026,2,21), categoryId = loisirs,      note = "Sport abonnement",   currency = "MAD"),
            Expense(amount = 40.0,   date = dateOf(2026,2,24), categoryId = autre,        note = "Divers",             currency = "MAD"),
            Expense(amount = 75.0,   date = dateOf(2026,2,28), categoryId = sante,        note = "Consultation",       currency = "MAD")
        )

        // Avril 2026
        val depensesAvril = listOf(
            Expense(amount = 500.0,  date = dateOf(2026,3,1),  categoryId = logement,     note = "Loyer Avril",        currency = "MAD"),
            Expense(amount = 50.0,   date = dateOf(2026,3,2),  categoryId = alimentation, note = "Courses",            currency = "MAD"),
            Expense(amount = 15.0,   date = dateOf(2026,3,3),  categoryId = transport,    note = "Bus semaine",        currency = "MAD"),
            Expense(amount = 120.0,  date = dateOf(2026,3,5),  categoryId = loisirs,      note = "Concert",            currency = "MAD"),
            Expense(amount = 85.0,   date = dateOf(2026,3,7),  categoryId = alimentation, note = "Restaurant famille", currency = "MAD"),
            Expense(amount = 200.0,  date = dateOf(2026,3,8),  categoryId = etudes,       note = "Matériel cours",     currency = "MAD"),
            Expense(amount = 30.0,   date = dateOf(2026,3,10), categoryId = transport,    note = "Taxi aéroport",      currency = "MAD"),
            Expense(amount = 45.0,   date = dateOf(2026,3,12), categoryId = sante,        note = "Médicaments",        currency = "MAD"),
            Expense(amount = 95.0,   date = dateOf(2026,3,14), categoryId = alimentation, note = "Supermarché",        currency = "MAD"),
            Expense(amount = 60.0,   date = dateOf(2026,3,16), categoryId = loisirs,      note = "Jeux vidéo",         currency = "MAD"),
            Expense(amount = 20.0,   date = dateOf(2026,3,18), categoryId = transport,    note = "Parking",            currency = "MAD"),
            Expense(amount = 150.0,  date = dateOf(2026,3,20), categoryId = etudes,       note = "Cours particulier",  currency = "MAD"),
            Expense(amount = 35.0,   date = dateOf(2026,3,22), categoryId = autre,        note = "Cadeau ami",         currency = "MAD"),
            Expense(amount = 70.0,   date = dateOf(2026,3,25), categoryId = alimentation, note = "Courses fin mois",   currency = "MAD"),
            Expense(amount = 90.0,   date = dateOf(2026,3,28), categoryId = sante,        note = "Dentiste",           currency = "MAD")
        )

        (depensesMars + depensesAvril).forEach {
            expenseDao.insertExpense(it)
        }
    }

    private fun dateOf(year: Int, month: Int, day: Int): Long {
        return Calendar.getInstance().apply {
            set(year, month, day, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}