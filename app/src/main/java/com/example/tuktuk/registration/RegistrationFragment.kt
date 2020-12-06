package com.example.tuktuk.registration

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.tuktuk.R
import com.example.tuktuk.database.LocalCache

import com.example.tuktuk.databinding.FragmentRegistrationBinding
import com.example.tuktuk.util.Injection
import kotlinx.coroutines.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var cache: LocalCache
    var textview_date: TextView? = null
    @RequiresApi(Build.VERSION_CODES.N)
    var cal = Calendar.getInstance()!!
    //helper global variable
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var registerContext: Context
    }

  @SuppressLint("SetTextI18n", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      binding = DataBindingUtil.inflate(
          inflater, R.layout.fragment_registration, container, false
      )
      binding.lifecycleOwner = this
      registrationViewModel = ViewModelProvider(this, Injection.provideRegistrationViewModelFactory(context!!))
          .get(RegistrationViewModel::class.java)
      binding.registrationViewModel = registrationViewModel

      cache = Injection.provideCache(context!!)
      val animDrawable = binding.registrationLayout.background as AnimationDrawable
      animDrawable.setEnterFadeDuration(10)
      animDrawable.setExitFadeDuration(5000)
      animDrawable.start()

      textview_date = binding.ageInput

      val dateSetListener = object : DatePickerDialog.OnDateSetListener {
          @RequiresApi(Build.VERSION_CODES.N)
          override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                 dayOfMonth: Int) {
              cal.set(Calendar.YEAR, year)
              cal.set(Calendar.MONTH, monthOfYear)
              cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
              updateDateInView()
          }
      }

      textview_date!!.setOnClickListener(object : View.OnClickListener {
          @RequiresApi(Build.VERSION_CODES.N)
          override fun onClick(view: View) {
              DatePickerDialog(view.context,
                  dateSetListener,
                  cal.get(Calendar.YEAR),
                  cal.get(Calendar.MONTH),
                  cal.get(Calendar.DAY_OF_MONTH)).show()
          }

      })

      binding.toLoginButton.setOnClickListener { view : View ->
          view.findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
      }

      binding.registerButton.isEnabled = false
      return binding.root
  }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationViewModel.isValid.observe(viewLifecycleOwner, Observer<Boolean> { isValidated ->
            binding.registerButton.isEnabled = isValidated
        })

        binding.registerButton.setOnClickListener {
            register(registrationViewModel.name.toString(), registrationViewModel.email.toString(), registrationViewModel.password.toString())
        }
    }

    private fun register(name: String, email: String, password: String){
        GlobalScope.launch {
            val responseExists: Deferred<Int> = async (Dispatchers.IO) {registrationViewModel.userExists("exists", name)}
            when (responseExists.await()) {
                200 -> {
                    val responseRegister: Deferred<Int> = async(Dispatchers.IO) {
                        Log.i("INFO", "######")
                        Log.i("INFO", "REGISTER FORM")
                        Log.i("INFO", registrationViewModel.name.value!!)
                        Log.i("INFO", registrationViewModel.email.value!!)
                        Log.i("INFO", registrationViewModel.password.value!!)
                        registrationViewModel.api(
                            "register",
                            registrationViewModel.name.value!!,
                            registrationViewModel.email.value!!,
                            registrationViewModel.password.value!!
                        )
                    }
                    val codeRegister = responseRegister.await()
                    if (codeRegister == 200) {
                        Log.i("INFO", "######")
                        view?.findNavController()?.navigate(R.id.loginFragment)
                    } else {
                        Log.i("INFO", "code err register")
                    }
                }
                409 -> {
                    Log.i("INFO", "Pouzivatel existuje.")
                }
                500 -> {
                    Log.i("INFO", "Nastala neocakavana chyba.")
                }
                else -> {
                    Log.i("INFO", "Nastala naozaj neocakavana chyba.")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }
}