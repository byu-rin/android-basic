package com.byurin.datepicker

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.byurin.datepicker.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textDate = binding.textDate
        val buttonDate = binding.buttonDate

        val calendarBox = Calendar.getInstance() // 현재 시간을 기준으로 달력 객체 생성
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth -> // DatePickerDialog 의 날짜 설정되었을 때의 동작 정의 리스너
            calendarBox.set(Calendar.YEAR, year) // 선택한 연도를 달력 객체에 설정
            calendarBox.set(Calendar.MONTH, month) // 선택한 월을 달력 객체에 설정
            calendarBox.set(Calendar.DAY_OF_MONTH, dayOfMonth) // 선택한 일을 달력 객체에 설정

            updateText(calendarBox) // 달력 객체에 설정된 날짜 이용하여 텍스트 업데이트
        }
        buttonDate.setOnClickListener {
            // 버튼 클릭 시 DatePickerDialog 띄우기
            DatePickerDialog(this, dateBox, calendarBox.get(Calendar.YEAR), calendarBox.get(Calendar.MONTH), calendarBox.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    // 달력 객체에 설정된 날짜를 텍스트 형식으로 변환하여 텍스트뷰에 업데이트
    private fun updateText(calendar: Calendar){
        val dateFormat = "dd-MM-yyyy" // 날짜 출력 형식 지정
        val simple = SimpleDateFormat(dateFormat, Locale.KOREA) // SimpleDateFormat 사용하여 날짜 형식 지정
        binding.textDate.text = simple.format(calendar.time) // 지정한 날짜 형식으로 날짜를 문자열로 변환하여 텍스트뷰에 설정
    }
}

