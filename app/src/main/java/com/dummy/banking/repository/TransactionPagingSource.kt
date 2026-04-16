package com.dummy.banking.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dummy.banking.model.Transaction

class TransactionPagingSource(
    private val repository: TransactionRepository
) : PagingSource<Int, Transaction>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        val position = params.key ?: 0
        return try {
            val transactions = repository.getTransactionsPaginated(position, params.loadSize)
            LoadResult.Page(
                data = transactions,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (transactions.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
