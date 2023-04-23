package event.telegram.validation

import org.apache.commons.validator.routines.UrlValidator
import org.apache.commons.validator.routines.UrlValidator.*

object IsLinkValid {
    operator fun invoke(link: String): Boolean {
        val urlValidator = UrlValidator(ALLOW_2_SLASHES + NO_FRAGMENTS + ALLOW_LOCAL_URLS + ALLOW_ALL_SCHEMES)
        return urlValidator.isValid(link)
    }
}
