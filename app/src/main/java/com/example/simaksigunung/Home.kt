package com.example.simaksigunung

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.simaksigunung.booking.SyaratKetentuan
import com.example.simaksigunung.databinding.FragmentHomeBinding
import com.example.simaksigunung.carousel.CarouselAdapter
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val carouselImages = listOf(
        R.drawable.main_gunung,
        R.drawable.main_gunung_1,
        R.drawable.main_gunung_2
    )

    private var currentPage = 0
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (binding.imageCarousel.adapter != null) {
                val itemCount = binding.imageCarousel.adapter!!.itemCount
                currentPage = (currentPage + 1) % itemCount // Loop back to the first item
                binding.imageCarousel.setCurrentItem(currentPage, true) // Smooth scroll to the next item
                handler.postDelayed(this, 5000) // Repeat every 2 seconds
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupCarousel()
        updateUserName()
        updateDate()

        // Handle button clicks
        binding.btnPesanRute.setOnClickListener {
            val intent = Intent(activity, SyaratKetentuan::class.java)
            startActivity(intent)
        }

        binding.detailRute.setOnClickListener {
            val intent = Intent(activity, DetailRute::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    fun ViewPager2.setSmoothScrollDuration(duration: Long) {
        try {
            val recyclerView = getChildAt(0) as RecyclerView
            val layoutManager = recyclerView.layoutManager

            if (layoutManager is RecyclerView.SmoothScroller.ScrollVectorProvider) {
                val smoothScroller = object : LinearSmoothScroller(context) {
                    override fun calculateSpeedPerPixel(displayMetrics: android.util.DisplayMetrics): Float {
                        return duration / displayMetrics.densityDpi.toFloat()
                    }
                }

                val method = RecyclerView::class.java.getDeclaredMethod(
                    "smoothScrollBy",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    RecyclerView.SmoothScroller::class.java
                )
                method.isAccessible = true
                method.invoke(recyclerView, 0, 0, smoothScroller)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setupCarousel() {
        val adapter = CarouselAdapter(carouselImages)
        binding.imageCarousel.adapter = adapter

        // Set custom page transformer for smooth animation
        binding.imageCarousel.setPageTransformer { page, position ->
            page.apply {
                translationX = -position * resources.getDimensionPixelOffset(R.dimen.carousel_page_margin)
                scaleY = 1 - (0.15f * kotlin.math.abs(position)) // Shrink pages slightly
                alpha = 0.5f + (1 - kotlin.math.abs(position)) // Fade effect
            }
        }

        // Adjust scroll speed
        binding.imageCarousel.setSmoothScrollDuration(300L) // 300ms scroll duration

        // Auto-scroll setup
        handler.postDelayed(autoScrollRunnable, 5000)

        // Add spacing between pages
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.carousel_page_margin)
        val offsetPx = resources.getDimensionPixelOffset(R.dimen.carousel_offset)

        binding.imageCarousel.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3 // Keep adjacent items visible
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        // Add spacing
        binding.imageCarousel.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                outRect.right = pageMarginPx
                outRect.left = if (position == 0) offsetPx else 0
            }
        })

        // Initialize dot indicators
        setupDotIndicator()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Stop the auto-scroll when the view is destroyed
        handler.removeCallbacks(autoScrollRunnable)
    }

    private fun updateUserName() {
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val name = sharedPreferences?.getString("name", "Guest")
        binding.nama.text = name
    }

    private fun updateDate() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formattedDate = dateFormat.format(currentDate)
        binding.tanggal.text = formattedDate
    }

    private fun setupDotIndicator() {
        val dotCount = carouselImages.size
        val dots = Array(dotCount) { ImageView(requireContext()) }

        // Clear any existing dots
        binding.dotIndicator.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(requireContext())
            dots[i].setImageResource(R.drawable.dot_unselected) // Default unselected dot
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0) // Spacing between dots
            dots[i].layoutParams = params
            binding.dotIndicator.addView(dots[i])
        }

        // Set the first dot as selected
        if (dots.isNotEmpty()) {
            dots[0].setImageResource(R.drawable.dot_selected)
        }

        // Add a listener to update dots when the page changes
        binding.imageCarousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in dots.indices) {
                    dots[i].setImageResource(
                        if (i == position) R.drawable.dot_selected else R.drawable.dot_unselected
                    )
                }
            }
        })
    }
}
