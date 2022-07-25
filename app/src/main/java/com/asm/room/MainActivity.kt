package com.asm.room

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asm.room.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)

        binding.btnWriteData.setOnClickListener {
            writeData()
        }

        binding.btnReadData.setOnClickListener {
            readData()
        }
    }

    private fun readData() {
        val rollNo = binding.etFindRollNo.text.toString()

        if (rollNo.isNotEmpty()){
            lateinit var student: Student

            GlobalScope.launch {
                student = appDb.studentDao().findByRoll(rollNo.toInt())
                displayData(student)
            }
        }
    }

    private suspend fun displayData(student: Student) {
        withContext(Dispatchers.Main){
            Log.i(TAG, "${student?.firstName} - ${student?.lastName} - ${student?.rollNo}")
        }
    }

    private fun writeData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()) {
            val student = Student(null, firstName, lastName, rollNo.toInt())
            GlobalScope.launch(Dispatchers.IO) {
                appDb.studentDao().insert(student)
            }

            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etRollNo.text.clear()

            Toast.makeText(this@MainActivity, "Successfully written", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "Please complete the input fields", Toast.LENGTH_SHORT).show()
        }
    }
}