package com.edgar.mvidemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private val viewModel: MainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(
            RetrofitUtils.apiService
        )
    }

    private val imageAdapter: ImageAdapter by lazy {
        ImageAdapter { imageInfoBean ->
            //todo
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.binding = binding

        init()
    }

    private fun init() {
        binding?.rvImages?.adapter = imageAdapter
        binding?.btnLoadData?.setOnClickListener { loadData() }

        observeState()
        observeEffect()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState
                //当生命周期处于 STARTED 之后执行代码，STOP 之后取消
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                //过滤重复
                .distinctUntilChanged()
                .collect {
                    when (it) {
                        is MainState.Idle -> {}
                        is MainState.Loading -> {
                            binding?.pbLoading?.show()
                            binding?.btnLoadData?.gone()
                        }

                        is MainState.GetImageSuccess -> {
                            binding?.pbLoading?.gone()
                            binding?.btnLoadData?.gone()
                            binding?.rvImages?.show()

                            imageAdapter.submitList(it.images.resBean.imageInfoBean)
                        }

                        is MainState.Error -> {
                            binding?.pbLoading?.gone()
                            binding?.btnLoadData?.show()
                            binding?.btnLoadData?.setText(R.string.retry)
                            showToast(it.error)
                        }
                    }
                }
        }
    }

    private fun observeEffect() {
        lifecycleScope.launch {
            viewModel.effect
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is MainEffect.ToastEffect -> {
                            showToast(it.msg)
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

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
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