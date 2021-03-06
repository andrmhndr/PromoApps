package com.example.promoapps.viewmodel.user

import androidx.lifecycle.ViewModel
import com.example.promoapps.adapter.Helper
import com.example.promoapps.model.PromoModel
import com.example.promoapps.model.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class UserViewModel: ViewModel() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var userModel: UserModel
    var promoList: ArrayList<PromoModel> = arrayListOf()

    suspend fun getUserData(currentUser: FirebaseUser?){
        if (currentUser != null){
            db.collection(Helper.ROLE).document(currentUser.uid).get().addOnSuccessListener { document->
                if (document != null){
                    userModel = UserModel(document.getString("name"), document.getString("email"), currentUser.uid, document.getString("role"))
                }
            }.await()
        }
    }

    suspend fun getPromosData(){
        promoList.clear()
        db.collection(Helper.PROMOS).orderBy(Helper.TIMESTAMP, Query.Direction.DESCENDING).get().addOnSuccessListener { document->
            document?.forEach { data->
                val promoModel = PromoModel(data.id, data.getString("title"), data.getDate("timestamp"), data.getString("description"))
                promoList.add(promoModel)
            }
        }.await()
    }
}