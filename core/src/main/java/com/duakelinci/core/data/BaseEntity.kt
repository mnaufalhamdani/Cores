package com.duakelinci.core.data

import androidx.room.ColumnInfo
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.duakelinci.core.util.parseUnixTime

enum class StatusKirim(val status:Int){
    NOT_SENT(0), SENT(1)
}

abstract class BaseEntity {
    @ColumnInfo(name = "status")
    var status: Int = 1
    @ColumnInfo(name = "status_kirim")
    var status_kirim: StatusKirim = StatusKirim.NOT_SENT
    @ColumnInfo(name = "created_at")
    var created_at: String = ""
    @ColumnInfo(name = "updated_at")
    var updated_at: String = ""

    abstract class BaseDaoTimeStamp<T : BaseEntity> {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        abstract suspend fun insert(model: T)

        suspend fun insertWithTimeStamp(model: T): T {
            model.created_at = System.currentTimeMillis().parseUnixTime("yyyy-MM-dd HH:mm:ss")
            model.updated_at = System.currentTimeMillis().parseUnixTime("yyyy-MM-dd HH:mm:ss")
            this.insert(model)
//            model.id = this.insert(model).toInt()
            return model
        }

        suspend fun insertWithTimeStamp(modelsParam: List<T>): List<T> {
            val models = mutableListOf<T>()
            modelsParam.map {
                models.add(this.insertWithTimeStamp(it))
            }
            return models
        }

        suspend fun insertWithTimeStamp(vararg modelsParam: T):List<T>{
            val models = mutableListOf<T>()
            modelsParam.map {
                models.add(this.insertWithTimeStamp(it))
            }
            return models
        }

        @Update
        abstract suspend fun update(model: T)

        suspend fun updateWithTimeStamp(modelsParam: List<T>): List<T> {
            val models = mutableListOf<T>()
            modelsParam.map {
                models.add(this.updateWithTimeStamp(it))
            }
            return models
        }

        suspend fun updateWithTimeStamp(vararg modelsParam: T):List<T>{
            val models = mutableListOf<T>()
            modelsParam.map {
                models.add(this.updateWithTimeStamp(it))
            }
            return models
        }

        suspend fun updateWithTimeStamp(model: T): T {
            model.updated_at = System.currentTimeMillis().parseUnixTime("yyyy-MM-dd HH:mm:ss")
            this.update(model)
            return model
        }

        @Delete
        abstract suspend fun delete(model:T)

        suspend fun deleteOnStatus(model: T) {
            model.status = 0
            this.updateWithTimeStamp(model)
        }
    }
}