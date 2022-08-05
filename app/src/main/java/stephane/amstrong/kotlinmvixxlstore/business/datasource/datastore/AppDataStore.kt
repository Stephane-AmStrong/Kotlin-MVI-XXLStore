package stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore


interface AppDataStore {

    suspend fun setValue(
        key: String,
        value: String
    )

    suspend fun readValue(
        key: String,
    ): String?

}