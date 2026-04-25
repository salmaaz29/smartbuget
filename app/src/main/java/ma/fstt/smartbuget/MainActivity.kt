package ma.fstt.smartbuget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.data.TestDataSeeder
import ma.fstt.smartbuget.data.database.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Insérer les données de test au premier lancement
        val prefs = getSharedPreferences("smartbudget_prefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("data_seeded", false)

        if (!isFirstLaunch) {
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(applicationContext)
                // Attendre que les catégories soient créées
                kotlinx.coroutines.delay(1000)
                TestDataSeeder.seedIfEmpty(
                    db.categoryDao(),
                    db.expenseDao()
                )
                // Marquer comme fait
                prefs.edit().putBoolean("data_seeded", true).apply()
            }
        }
    }
}