package ma.fstt.smartbuget.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT // interdit supprimer catégorie si dépenses existent
        )
    ],
    indices = [Index(value = ["categoryId"])]
)


data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,                  // ex: 45.50
    val currency: String = "MAD",        // devise par défaut
    val date: Long,                      // timestamp (System.currentTimeMillis())
    val categoryId: Int,                 // lié à Category
    val note: String? = null,            // optionnel
    val paymentMethod: String? = null,   // "Espèce", "Carte", "Virement"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)