package com.example.testingproject.recyclers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testingproject.newsdetails.NewsDetailsActivity
import com.example.testingproject.R
import com.example.testingproject.databinding.NewsRowLayoutBinding
import com.example.testingproject.newslistener.NewsOnListener
import com.example.testingproject.newsmodels.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsAdapter (var newsOnListener: NewsOnListener) : PagedListAdapter<NewsModel,NewsAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(var newsRowLayoutBinding: NewsRowLayoutBinding) : RecyclerView.ViewHolder(newsRowLayoutBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val newsRowLayoutBinding  = DataBindingUtil.inflate<NewsRowLayoutBinding>(LayoutInflater.from(parent.context),
        R.layout.news_row_layout,parent,false)
        return ViewHolder(newsRowLayoutBinding)
    }
     companion object {
         var diffUtil =   object : DiffUtil.ItemCallback<NewsModel>() {
             override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                 return oldItem.articles[0].author == newItem.articles[0].author
             }

             override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
                 return oldItem.articles[0].title == newItem.articles[0].title
                         && oldItem.articles[0].description == newItem.articles[0].description
                         && oldItem.articles[0].publishedAt == newItem.articles[0].publishedAt
             }
         }
    }
     override fun getItem(position: Int): NewsModel? {
        return super.getItem(position)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
//        val date = simpleDateFormat.parse(model!!.publishedAt!!)!!
//        val formattedDate = simpleDateFormat.format(date)
//        model.publishedAt = formattedDate
//
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            holder.newsRowLayoutBinding.newsProgressBar.visibility = View.INVISIBLE
            holder.newsRowLayoutBinding.position = position
            holder.newsRowLayoutBinding.newsy = model
            holder.newsRowLayoutBinding.listener = newsOnListener
        }
    }
}