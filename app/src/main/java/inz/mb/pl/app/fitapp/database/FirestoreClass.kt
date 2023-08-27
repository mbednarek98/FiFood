package inz.mb.pl.app.fitapp.database

import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import inz.mb.pl.app.fitapp.*
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO
import inz.mb.pl.app.fitapp.enums.DBTypeEnum
import inz.mb.pl.app.fitapp.interfaces.QueryInterface
import inz.mb.pl.app.fitapp.models.User
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentThree
import java.time.LocalDate
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.ui.search_food.SearchFoodFragment


class FirestoreClass : QueryInterface {
    companion object {
        const val USERS: String = "Users"
        const val DISHES : String = "Dish"
    }
    private val fireStore = FirebaseFirestore.getInstance()

    fun searchDishes(fragment : SearchFoodFragment, query : String){
        fireStore.collection(DISHES)
            .orderBy("name_lowercase").whereGreaterThanOrEqualTo("name_lowercase",query.lowercase())
            .whereLessThanOrEqualTo("name_lowercase",query.lowercase()+ "\uf8ff")
            .limit(50)
            .get()
            .addOnSuccessListener {
                fragment.setUpListView(it.toObjects(SavedDishDTO::class.java) as ArrayList<SavedDishDTO>)
            }
    }

    fun registerUser(activity: BaseActivity, userInfo: User){
        val userID = getCurrentUserId()
        if(userID != null) {
            fireStore.collection(USERS)
                .document(userID)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userFirstSuccess()
                }
        }
    }

    fun loginUser(activity: BaseActivity){
        val userID = getCurrentUserId()
        if(userID != null) {
            fireStore.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener {
                    val user: User = it.toObject(User::class.java)!!
                    when (user.age) {
                        0 -> activity.userFirstSuccess()
                        else -> {
                            val date = LocalDate.now()
                            activity.userSuccess(user,
                                (selectData(type = DBTypeEnum.WEIGHT) as WeightChangeDTO?),
                                (selectData(date.dayOfMonth,
                                    date.monthValue,
                                    date.year,
                                    DBTypeEnum.WATER) as WaterChangeDTO?)
                            )
                        }
                    }
                }
        }
    }


    fun splashScreenAuth(activity: SplashActivity){
        val userID = getCurrentUserId()
        if(userID != null) {
            fireStore.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener {
                    val user: User? = it.toObject(User::class.java)
                    when (user?.age) {
                        0 -> {
                            activity.changeActivityDownUp(
                                activity,
                                Intent(activity, GetStartedActivity::class.java)
                            )
                        }
                        else -> {
                            val date = LocalDate.now()
                            activity.changeActivityDownUp(
                                activity,
                                Intent(activity, MainActivity::class.java),
                                user,
                                (selectData(type = DBTypeEnum.WEIGHT) as WeightChangeDTO?),
                                (selectData(date.dayOfMonth,
                                    date.monthValue,
                                    date.year,
                                    DBTypeEnum.WATER) as WaterChangeDTO?)

                            )
                        }
                    }
                }
        }
    }

    fun checkIfUserExist(activity: BaseActivity, userInfo: User){
        val userID = getCurrentUserId()
        if(userID != null) {
            fireStore.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener {
                    when {
                        it.toObject(User::class.java) == null -> registerUser(activity, userInfo)
                        else -> loginUser(activity)
                    }
                }
        }
    }


    fun getCurrentUser(fragment: GetStartedFragmentThree){
        val userID = getCurrentUserId()
        if(userID != null) {
            fireStore.collection(USERS)
                .document(userID)
                .get()
                .addOnSuccessListener {
                    fragment.assignUser(it.toObject(User::class.java)!!)
                }
        }
    }


    fun updateUser(user : User?,activity: SettingsActivity?){
        val userID = getCurrentUserId()
        if(userID != null) {
            val updateUser = hashMapOf<String, Any?>(
                "age" to user?.age,
                "gender" to user?.gender,
                "height" to user?.height,
                "activity" to user?.activity,
                "progress" to user?.progress,
                "dishesCount" to user?.dishesCount,
                "dishesType" to user?.dishesType
            )
            Log.e("xD", "$updateUser ====================")
            fireStore.collection(USERS)
                .document(userID)
                .update(updateUser)
                .addOnFailureListener { Log.e("?????????????", "$updateUser ====================") }
                .addOnSuccessListener {
                    Log.e("xDDD", "$updateUser ====================")
                    activity?.finishSettingsActivity()
                }
        }
    }


    private fun getCurrentUserId() : String? = FirebaseAuth.getInstance().currentUser?.uid

    fun setUserDetails(fragment: GetStartedFragmentThree, userInfo: User){
        val userID = getCurrentUserId()
        if(userID != null) {
            fireStore.collection(USERS)
                .document(userID)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    fragment.changeToMainActivity(userInfo)
                }
        }
    }


}