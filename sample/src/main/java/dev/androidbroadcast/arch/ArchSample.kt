@file:Suppress("unused", "UNUSED_PARAMETER")

package dev.androidbroadcast.arch

import by.kirich1409.result.Result
import by.kirich1409.result.map
import dev.androidbroadcast.Contributor
import dev.androidbroadcast.ContributorDTO
import dev.androidbroadcast.ContributorVO
import dev.androidbroadcast.GitHub
import dev.androidbroadcast.toContributor
import dev.androidbroadcast.toContributorVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class View(private val viewModel: ViewModel) {

    private val viewScope = CoroutineScope(SupervisorJob())

    fun onViewCreated() {
        viewScope.launch {
            viewModel.contributors.collect { result: Result<List<ContributorVO>> ->
                when(result) {
                    is Result.Success -> showData(result.value)
                    is Result.Failure<*> -> showError(result.error)
                }
            }
        }
    }

    private fun showError(error: Throwable?) {
        TODO("Not yet implemented")
    }

    private fun showData(contributors: List<ContributorVO>) {
        TODO("Not yet implemented")
    }
}

class ViewModel(private val repository: Repository) {

    val contributors: Flow<Result<List<ContributorVO>>> =
        flow {
            repository.loadContributors("kirich1409", "ViewBindingPropertyDelegate")
                .map { contributors: List<Contributor> -> contributors.map(Contributor::toContributorVO) }
        }
}

class Repository(private val gitHub: GitHub) {

    suspend fun loadContributors(owner: String, repo: String): Result<List<Contributor>> {
        return gitHub.contributors(owner, repo).map { contributors ->
            contributors.map(ContributorDTO::toContributor)
        }
    }
}