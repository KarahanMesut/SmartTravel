package com.mesutkarahan.smarttravel.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

import com.mesutkarahan.smarttravel.databinding.FragmentRegisterBinding
import com.mesutkarahan.smarttravel.model.User
import com.mesutkarahan.smarttravel.roomdb.UserDatabase
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener { registerUser(it) }


        binding.btnGoToLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            view?.let { Navigation.findNavController(it).navigate(action) }
        }

    }

    private fun registerUser(view: View) {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterFragment", "Registration task is successful")
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        val db = FirebaseFirestore.getInstance()
                        val userMap = hashMapOf(
                            "email" to email,
                            "userId" to userId
                        )
                        Log.d("RegisterFragment", "User ID: $userId")

                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                Log.d("RegisterFragment", "Navigating to HomeFragment")

                                // Room Database'e kaydet
                                val userDao = UserDatabase.getDatabase(requireContext()).userDao()
                                lifecycleScope.launch {
                                    userDao.insert(User(userId = userId, email = email))
                                    val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                                    Navigation.findNavController(requireView()).navigate(action)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("RegisterFragment", "Firestore Error: ${e.message}")
                            }
                    } else {
                        Log.e("RegisterFragment", "User ID is null after successful registration")
                    }
                } else {
                    Log.e("RegisterFragment", "Registration Failed: ${task.exception?.message}")
                    Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}