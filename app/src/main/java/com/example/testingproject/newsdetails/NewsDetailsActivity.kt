package com.example.testingproject.newsdetails

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.testingproject.showToast
import com.example.testingproject.Utils
import com.example.testingproject.viewmodel.MainViewModel
import com.example.testingproject.databinding.ActivityNewsDetailsBinding
import com.example.testingproject.models.FavNewsModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news__details_.*

@AndroidEntryPoint
class NewsDetailsActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel? by viewModels()
    lateinit var binding: ActivityNewsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

            val intent = intent
            intent?.let {
                binding.apply {
                    setAuthor.text = intent.getStringExtra("author")
                    favdescription.text = intent.getStringExtra("description")
                    favtitle.text = intent.getStringExtra("title")
                    toolbar.title = intent.getStringExtra("date")
                    Picasso.get().load(intent.getStringExtra("imgUrl")).fit().into(favImage)
                }
            }


        if (Utils.checkConnectivity(this)) {
            fab.setOnClickListener {
                val favNewsModel = FavNewsModel(
                    intent.getStringExtra("author"),
                    intent.getStringExtra("title"),
                    intent.getStringExtra("imgurl"),
                    intent.getStringExtra("date"),
                    intent.getStringExtra("description")
                )
                mainViewModel!!.insertNews(favNewsModel)
                this.showToast("Article Successfully Added")
            }
        }
    }
}

