package queries

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("selectCountry")
class SelectCountryQuery(val countryFlagSmile: String) : Query

@Serializable
@SerialName("selectCityInCIS")
class SelectCityInCIS(val city: String) : Query

@Serializable
@SerialName("selectDistrict")
class SelectDistrict(val district: String) : Query

@Serializable
@SerialName("selectRegion")
class SelectRegion(val region: String) : Query

@Serializable
@SerialName("selectCity")
class SelectCity(val city: String) : Query
