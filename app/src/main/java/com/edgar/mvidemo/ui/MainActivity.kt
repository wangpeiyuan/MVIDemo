package com.edgar.mvidemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edgar.mvidemo.R
import com.edgar.mvidemo.data.model.ImageInfoBean
import com.edgar.mvidemo.databinding.ActivityMainBinding
import com.edgar.mvidemo.databinding.ItemImageBinding
import com.edgar.mvidemo.network.RetrofitUtils
import com.edgar.mvidemo.utils.gone
import com.edgar.mvidemo.utils.show
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private val viewModel: MainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(
            RetrofitUtils.apiService
        )
    }

    private val imageAdapter: ImageAdapter by lazy {
        ImageAdapter() { imageInfoBean ->

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.binding = binding

        init()
        loadData()
    }

    private fun init() {
        binding?.rvImages?.adapter = imageAdapter
        binding?.btnRetry?.setOnClickListener { loadData() }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is MainState.Idle -> {}
                    is MainState.Loading -> {
                        binding?.pbLoading?.show()
                        binding?.btnRetry?.gone()
                    }

                    is MainState.GetImageSuccess -> {
                        binding?.pbLoading?.gone()
                        binding?.btnRetry?.gone()
                        binding?.rvImages?.show()

                        imageAdapter.submitList(it.images.resBean.imageInfoBean)
                    }

                    is MainState.Error -> {
                        binding?.pbLoading?.gone()
                        binding?.btnRetry?.show()
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.mainIntentChannel.send(MainIntent.GetImages)
        }
    }
}

class ImageAdapter(
    private val onClick: (ImageInfoBean) -> Unit
) : ListAdapter<ImageInfoBean, ImageHolder>(DiffCallback) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<ImageInfoBean>() {
            override fun areItemsTheSame(oldItem: ImageInfoBean, newItem: ImageInfoBean) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ImageInfoBean, newItem: ImageInfoBean) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context))
        return ImageHolder(binding) { onClick.invoke(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ImageHolder(
    private val itemBinding: ItemImageBinding,
    private val onClick: (position: Int) -> Unit
) : RecyclerView.ViewHolder(itemBinding.root) {
    init {
        itemBinding.root.setOnClickListener {
            onClick(adapterPosition)
        }
    }

    fun bind(imageInfoBean: ImageInfoBean) {
        Glide.with(itemBinding.ivImageItem)
            .load(imageInfoBean.img)
            .centerCrop()
            .into(itemBinding.ivImageItem)
    }
}