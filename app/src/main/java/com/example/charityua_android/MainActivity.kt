package com.example.charityua_android

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.charityua_android.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.core.content.ContextCompat

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

    // Категорії — за бажанням можна завантажити з API
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

        // Динамічно додаємо чекбокси для категорій
        setupCategoryCheckboxes()

        adapter = FundraiserAdapter(listOf()) {
            Toast.makeText(this, "Вибрано: ${it.title}", Toast.LENGTH_SHORT).show()
        }

        binding.fundraisersRecycler.layoutManager = LinearLayoutManager(this)
        binding.fundraisersRecycler.adapter = adapter

        loadFundraisers()
        setupDrawerTriggers()
        setupFilterLogic()
    }

    private fun setupCategoryCheckboxes() {
        availableCategories.forEach { name ->
            val checkBox = CheckBox(this)
            checkBox.text = name
            checkBox.setTextColor(getColor(android.R.color.black))
            checkBox.setOnCheckedChangeListener { _, _ -> updateFilterSummary() }
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
        val currentFundraisers = allFundraisers
        if (currentFundraisers == null) return

        val selectedCategories = mutableListOf<String>()
        for (i in 0 until categoriesContainer.childCount) {
            val view = categoriesContainer.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                selectedCategories.add(view.text.toString())
            }
        }

        val filtered = currentFundraisers.filter { fundraiser ->
            val isAlmostFinished = if (filterAlmostFinished.isChecked) {
                val percent = fundraiser.current_amount.toDouble() / fundraiser.goal_amount
                percent >= 0.9
            } else true

            val matchesCategory = if (selectedCategories.isNotEmpty()) {
                selectedCategories.contains(fundraiser.category_name)
            } else true

            isAlmostFinished && matchesCategory
        }

        adapter = FundraiserAdapter(filtered) {
            Toast.makeText(this, "Деталі: ${it.title}", Toast.LENGTH_SHORT).show()
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
                    adapter = FundraiserAdapter(allFundraisers) {
                        Toast.makeText(this@MainActivity, "Деталі: ${it.title}", Toast.LENGTH_SHORT).show()
                    }
                    binding.fundraisersRecycler.adapter = adapter
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
}