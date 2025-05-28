package com.example.charityua_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComplaintBottomSheet(
    private val fundraiserId: Int,
    private val onSuccess: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var reasonGroup: RadioGroup
    private lateinit var commentInput: EditText
    private lateinit var sendButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.layout_complaint_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reasonGroup = view.findViewById(R.id.reason_radio_group)
        commentInput = view.findViewById(R.id.comment_input)
        sendButton = view.findViewById(R.id.send_button)
        cancelButton = view.findViewById(R.id.cancel_button)

        cancelButton.setOnClickListener {
            dismiss()
        }

        sendButton.setOnClickListener {
            val selectedId = reasonGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Оберіть причину", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reason = view.findViewById<RadioButton>(selectedId).text.toString()
            val comment = commentInput.text.toString().trim()

            val request = ComplaintRequest(
                fundraiser_id = fundraiserId,
                reason = reason,
                comment = if (comment.isEmpty()) null else comment
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.instance.submitComplaint(request)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Скаргу надіслано", Toast.LENGTH_SHORT).show()
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
}