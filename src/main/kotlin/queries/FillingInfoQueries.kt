package queries

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Country")
class SelectCountryQuery(val country: String) : Query

@Serializable
@SerialName("CIS")
class SelectCityInCIS(val city: String) : Query

@Serializable
@SerialName("Area")
class SelectDistrict(val district: String) : Query

@Serializable
@SerialName("region")
class SelectRegion(val region: String) : Query

@Serializable
@SerialName("City")
class SelectCity(val city: String) : Query
