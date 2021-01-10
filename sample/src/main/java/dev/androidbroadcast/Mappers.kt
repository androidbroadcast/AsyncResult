package dev.androidbroadcast

fun ContributorDTO.toContributor(): Contributor {
    return Contributor(login, contributions)
}

fun Contributor.toContributorVO(): ContributorVO {
    return ContributorVO(login, contributions)
}