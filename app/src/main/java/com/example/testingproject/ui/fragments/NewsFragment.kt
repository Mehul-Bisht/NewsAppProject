package com.example.testingproject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testingproject.BottomDialog
import com.example.testingproject.Utils
import com.example.testingproject.databinding.AllnewsLayoutBinding
import com.example.testingproject.mvvm.MainViewModel
import com.example.testingproject.mvvm.SharedViewModel
import com.example.testingproject.newslistener.NewsOnListener
import com.example.testingproject.newsmodels.ArticleX
import com.example.testingproject.recyclers.NewsAdapter
import com.example.testingproject.ui.FavNewsActivity
import com.example.testingproject.ui.NewsDetailsActivity
import com.example.testingproject.ui.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
@ExperimentalPagingApi
class NewsFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var newsAdapter: NewsAdapter
    private  val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding : AllnewsLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AllnewsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.newsRecycler.setHasFixedSize(true)

        sharedViewModel = ViewModelProviders.of(this)[SharedViewModel::class.java]

        newsAdapter = NewsAdapter(object : NewsOnListener {
            override fun onNewsClicked(allNewsModel: ArticleX) {
                Intent(requireContext(), NewsDetailsActivity::class.java).apply {
                    putExtra("author", allNewsModel.author)
                    putExtra("title", allNewsModel.title)
                    putExtra("description", allNewsModel?.description)
                    putExtra("imgUrl", allNewsModel.urlToImage)
                    putExtra("date", allNewsModel.publishedAt)
                    binding.fabMenu.collapse()
                    startActivity(this)
                }
            }
        })

        if(Utils.checkConnectivity(requireContext())) {
            fetchData("corona".toLowerCase(Locale.ROOT))
        } else {
            lifecycleScope.launchWhenStarted {
                mainViewModel.getOfflineNews().collect {
                    newsAdapter.submitData(it)
                    binding.newsRecycler.adapter = newsAdapter
                }
            }
        }
        binding.deleteMenu.setOnClickListener {
            BottomDialog().apply {
                show(parentFragmentManager, "Suggestions Deletion")
                binding.fabMenu.collapse()
            }
        }
        binding.settingsMenu.setOnClickListener {
            Intent(requireContext(), SettingsActivity::class.java).apply {
                startActivity(this)
            }
            binding.fabMenu.collapse()
        }
        binding.favNews.setOnClickListener {
            Intent(requireContext(), FavNewsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(this)
            }
        }

        sharedViewModel.getQuery().observe(viewLifecycleOwner, Observer {
            fetchData(it.toLowerCase(Locale.ROOT))
        })
    }
    private fun fetchData(query: String) {
        lifecycleScope.launch {
              mainViewModel.getNews(query,Utils.API_KEY).collect {
                newsAdapter.submitData(it)

            }
        }
        binding.newsRecycler.adapter = newsAdapter
    }

}















        /*
          //val menuItem = menu.findItem(R.id.newsSearchView).actionView as SearchView
        menuItem.findViewById<ImageView>(androidx.appcompat.R.id.search_button).apply {
            setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_search_black_24dp))
        }
        menuItem.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat
            .R.id.search_src_text).apply {
                setHintTextColor(Color.WHITE)
                setTextColor(Color.WHITE)
        }
        menuItem.queryHint = "search"
        menuItem.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(
            requireContext(),
            R.layout.suggestions_layout,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        menuItem.suggestionsAdapter = cursorAdapter
        menuItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchData(query!!)
                CoroutineScope(Dispatchers.IO).launch {
                    mainViewModel.insertSuggestions(SuggestionsModel(query))
                }

                val cursor = MatrixCursor(
                    arrayOf(
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1
                    )
                )
                CoroutineScope(Dispatchers.IO).launch {
                    mainViewModel.getSuggestions().observe(
                        viewLifecycleOwner,
                        androidx.lifecycle.Observer { list ->
                            query.let {
                                list.forEachIndexed { index, suggestionsModel ->
                                    if (suggestionsModel.suggestion?.contains(it!!, true)!!) {
                                        cursor.addRow(arrayOf(index, suggestionsModel.suggestion))
                                    }
                                }
                            }
                        })
                }
                cursorAdapter.changeCursor(cursor)
                return true
            }
        })
        menuItem.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = menuItem.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                menuItem.setQuery(selection, false)
                fetchData(selection)
                return false
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }
         */