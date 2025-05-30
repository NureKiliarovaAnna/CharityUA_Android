package com.example.charityua_android

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.charityua_android.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FundraiserAdapter
    private var allFundraisers: List<Fundraiser> = emptyList()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var filterSummary: TextView
    private lateinit var resetFiltersButton: Button
    private lateinit var filterAlmostFinished: CheckBox
    private lateinit var categoriesContainer: LinearLayout
    private lateinit var sortOptions: RadioGroup
    private lateinit var sortLabel: TextView
    private lateinit var sortOrderGroup: RadioGroup

    private val availableCategories = listOf("Медицина", "Армія", "Тварини", "Гуманітарна допомога", "Освіта", "Інше")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.mainDrawerLayout
        filterSummary = findViewById(R.id.filter_summary)
        resetFiltersButton = findViewById(R.id.reset_filters_button)
        filterAlmostFinished = findViewById(R.id.filter_almost_finished)
        filterAlmostFinished.buttonTintList =
            ContextCompat.getColorStateList(this, R.color.checkbox_blue)
        categoriesContainer = findViewById(R.id.filter_categories_container)
        sortOptions = findViewById(R.id.sort_options)
        sortLabel = findViewById(R.id.sort_label)

        val sortByDate = findViewById<RadioButton>(R.id.sort_by_date)
        val sortByRemaining = findViewById<RadioButton>(R.id.sort_by_remaining)

        val blueColor = ContextCompat.getColorStateList(this, R.color.checkbox_blue)
        sortByDate.buttonTintList = blueColor
        sortByRemaining.buttonTintList = blueColor

        sortOrderGroup = findViewById(R.id.sort_order_group)

        val asc = findViewById<RadioButton>(R.id.sort_order_asc)
        val desc = findViewById<RadioButton>(R.id.sort_order_desc)
        asc.buttonTintList = blueColor
        desc.buttonTintList = blueColor

        setupCategoryCheckboxes()
        setupDrawerTriggers()
        setupFilterLogic()

        adapter = FundraiserAdapter(listOf()) {
            openFundraiserDetails(it.fundraiser_id)
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        binding.fundraisersRecycler.layoutManager = LinearLayoutManager(this)
        binding.fundraisersRecycler.adapter = adapter

        loadFundraisers()
    }

    override fun onResume() {
        super.onResume()
        loadFundraisers()
    }

    private fun setupCategoryCheckboxes() {
        availableCategories.forEach { name ->
            val checkBox = CheckBox(this)
            checkBox.text = name
            checkBox.setTextColor(getColor(android.R.color.black))
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateFilterSummary()
                applyFilters()
            }
            categoriesContainer.addView(checkBox)
        }
    }

    private fun setupDrawerTriggers() {
        val sortButton = findViewById<ImageButton>(R.id.sort_button)
        val filterBlock = findViewById<LinearLayout>(R.id.filter_block)

        sortButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        filterBlock.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setupFilterLogic() {
        filterAlmostFinished.setOnCheckedChangeListener { _, _ ->
            updateFilterSummary()
            applyFilters()
        }

        sortOptions.setOnCheckedChangeListener { _, _ ->
            updateSortLabel()
            applyFilters()
        }

        sortOrderGroup.setOnCheckedChangeListener { _, _ ->
            updateSortLabel()
            applyFilters()
        }

        resetFiltersButton.setOnClickListener {
            filterAlmostFinished.isChecked = false

            for (i in 0 until categoriesContainer.childCount) {
                val view = categoriesContainer.getChildAt(i)
                if (view is CheckBox) view.isChecked = false
            }

            updateFilterSummary()
            applyFilters()
        }
    }

    private fun applyFilters() {
        val selectedCategories = mutableListOf<String>()
        for (i in 0 until categoriesContainer.childCount) {
            val view = categoriesContainer.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                selectedCategories.add(view.text.toString())
            }
        }

        var filtered = allFundraisers.filter { fundraiser ->
            val isAlmostFinished = if (filterAlmostFinished.isChecked) {
                val percent = fundraiser.current_amount.toDouble() / fundraiser.goal_amount
                percent >= 0.9
            } else true

            val matchesCategory = if (selectedCategories.isNotEmpty()) {
                selectedCategories.contains(fundraiser.category_name)
            } else true

            isAlmostFinished && matchesCategory
        }

        val sortTypeId = sortOptions.checkedRadioButtonId
        val sortOrderId = sortOrderGroup.checkedRadioButtonId

        val ascending = (sortOrderId == R.id.sort_order_asc)

        filtered = when (sortTypeId) {
            R.id.sort_by_date -> {
                if (ascending) filtered.sortedBy { it.created_at }
                else filtered.sortedByDescending { it.created_at }
            }
            R.id.sort_by_remaining -> {
                if (ascending) filtered.sortedBy { it.goal_amount - it.current_amount }
                else filtered.sortedByDescending { it.goal_amount - it.current_amount }
            }
            else -> filtered
        }

        adapter = FundraiserAdapter(filtered) {
            openFundraiserDetails(it.fundraiser_id)
        }
        binding.fundraisersRecycler.adapter = adapter

        updateFilterSummary()
    }

    private fun updateFilterSummary() {
        var count = 0
        if (filterAlmostFinished.isChecked) count++

        for (i in 0 until categoriesContainer.childCount) {
            val view = categoriesContainer.getChildAt(i)
            if (view is CheckBox && view.isChecked) count++
        }

        filterSummary.text = if (count == 0) "не обрано" else "обрано ($count)"
    }

    private fun updateSortLabel() {
        val selectedId = sortOptions.checkedRadioButtonId
        val selectedRadio = findViewById<RadioButton>(selectedId)
        sortLabel.text = selectedRadio.text
    }

    private fun loadFundraisers() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getActiveFundraisers()
                if (response.isSuccessful && response.body() != null) {
                    allFundraisers = response.body()!!.filter { it.status == "active" }
                    applyFilters()
                } else {
                    Toast.makeText(this@MainActivity, "Помилка сервера: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Проблема з мережею", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(this@MainActivity, "HTTP помилка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFundraiserDetails(fundraiserId: Int) {
        val intent = Intent(this, FundraiserDetailActivity::class.java)
        intent.putExtra("fundraiserId", fundraiserId)
        startActivity(intent)
    }
}