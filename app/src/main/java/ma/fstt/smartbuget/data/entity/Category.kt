package ma.fstt.smartbuget.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories",
        indices = [Index(value = ["name"], unique = true)]) // nom unique

data class Category (
    @PrimaryKey(autoGenerate = true)
                     val id: Int = 0,

                     val name: String,
                     val icon: String,
                     val color: String,
                     val isActivate: Boolean = true
)

