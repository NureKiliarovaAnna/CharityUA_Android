package com.example.charityua_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.charityua_android.databinding.FragmentDonateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DonateBottomSheet(
    private val fundraiserId: Int,
    private val maxAmount: Double,
    private val onSuccess: () -> Unit = {}
) : BottomSheetDialogFragment() {

    private var _binding: FragmentDonateBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonateBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопки швидкого введення
        binding.button100.setOnClickListener { setAmount(100) }
        binding.button200.setOnClickListener { setAmount(200) }
        binding.button500.setOnClickListener { setAmount(500) }

        binding.cancelButton.setOnClickListener { dismiss() }

        binding.confirmButton.setOnClickListener {
            val cardNumber = binding.cardNumberInput.text.toString().trim()
            val expDate = binding.cardExpiryInput.text.toString().trim()
            val cvv = binding.cardCvvInput.text.toString().trim()
            val amountStr = binding.amountInput.text.toString().trim()

            val amount = amountStr.toIntOrNull()

            if (cardNumber.length != 16 || expDate.length != 5 || !expDate.contains("/") ||
                cvv.length != 3 || amount == null || amount <= 0 || amount > maxAmount.toInt()
            ) {
                Toast.makeText(requireContext(), "Неправильні дані", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔄 Збереження імітації донату
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.instance.postDonation(
                        DonationRequest(fundraiser_id = fundraiserId, amount = amount)
                    )
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Дякуємо за донат!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Помилка: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Помилка підключення", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setAmount(amount: Int) {
        if (amount <= maxAmount.toInt()) {
            binding.amountInput.setText(amount.toString())
        } else {
            Toast.makeText(requireContext(), "Сума перевищує максимум", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}