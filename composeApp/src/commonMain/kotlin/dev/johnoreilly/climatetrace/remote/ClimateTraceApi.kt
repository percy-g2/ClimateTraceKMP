package dev.johnoreilly.climatetrace.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AssetsResult(val assets: List<Asset>)

@Serializable
data class Asset(
    @SerialName("Id")
    val id: String,
    @SerialName("Name")
    val name: String,
    @SerialName("AssetType")
    val assetType: String,
    @SerialName("Sector")
    val sector: String,
    @SerialName("Thumbnail")
    val thumbnail: String,
)


@Serializable
data class Country(
    val alpha3: String,
    val alpha2: String,
    val name: String,
    val continent: String,
)

@Serializable
data class CountryEmissionsInfo(
    val country: String,
    val rank: Int,
    val emissions: EmissionInfo,
    val worldEmissions: EmissionInfo
)

@Serializable
data class CountryAssetEmissionsInfo(
    @SerialName("Country")
    val country: String,
    @SerialName("Emissions")
    val emissions: Float,
    @SerialName("Sector")
    val sector: String
)


@Serializable
data class EmissionInfo(
    val co2: Float,
    val co2e_100yr: Float,
    val co2e_20yr: Float
)



class ClimateTraceApi(
    private val baseUrl: String = "https://api.climatetrace.org/v4",
)  {
    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false})
        }
    }

    suspend fun fetchContinents() = client.get("$baseUrl/definitions/continents").body<List<String>>()
    suspend fun fetchCountries() = client.get("$baseUrl/definitions/countries").body<List<Country>>()
    suspend fun fetchSectors() = client.get("$baseUrl/definitions/sectors").body<List<String>>()
    suspend fun fetchSubSectors() = client.get("$baseUrl/definitions/subsectors").body<List<String>>()
    suspend fun fetchGases() = client.get("$baseUrl/definitions/gases").body<List<String>>()

    // TODO need to implement paging on top of this
    suspend fun fetchAssets() = client.get("$baseUrl/assets").body<AssetsResult>()

    suspend fun fetchCountryAssets(countryCode: String) = client.get("$baseUrl/assets?countries=$countryCode").body<AssetsResult>()
    suspend fun fetchCountryEmissionsInfo(countryCode: String) = client.get("$baseUrl/country/emissions?since=2022&to=2022&countries=$countryCode").body<List<CountryEmissionsInfo>>()

    suspend fun fetchCountryAssetEmissionsInfo(countryCode: String) = client.get("$baseUrl/assets/emissions?countries=$countryCode").body<Map<String, List<CountryAssetEmissionsInfo>>>()
}