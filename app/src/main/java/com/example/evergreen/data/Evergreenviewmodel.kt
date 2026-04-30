package com.example.evergreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.evergreen.models.CarbonEntry
import com.example.evergreen.models.HabitModel
import com.example.evergreen.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EverGreenViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // ── User ──────────────────────────────────────────────────────────────────
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    // ── Carbon entries ────────────────────────────────────────────────────────
    private val _carbonEntries = MutableStateFlow<List<CarbonEntry>>(emptyList())
    val carbonEntries: StateFlow<List<CarbonEntry>> = _carbonEntries.asStateFlow()

    // ── Habits ────────────────────────────────────────────────────────────────
    private val _habits = MutableStateFlow<List<HabitModel>>(emptyList())
    val habits: StateFlow<List<HabitModel>> = _habits.asStateFlow()

    // ── Derived dashboard stats ───────────────────────────────────────────────
    val todayEmission: Double
        get() = _carbonEntries.value.lastOrNull()?.totalEmission ?: 0.0

    val weeklyEmissions: List<Double>
        get() = _carbonEntries.value.takeLast(7).map { it.totalEmission }

    val streak: Int
        get() {
            val dates = _carbonEntries.value.map { 
                java.time.LocalDate.ofInstant(
                    java.time.Instant.ofEpochMilli(it.timestamp), 
                    java.time.ZoneId.systemDefault()
                ) 
            }.distinct().sortedDescending()
            
            if (dates.isEmpty()) return 0
            
            var currentStreak = 0
            var checkDate = java.time.LocalDate.now()
            
            // If no entry today, check if there was one yesterday to continue streak
            if (dates.first() != checkDate && dates.first() != checkDate.minusDays(1)) {
                return 0
            }
            
            if (dates.first() == checkDate.minusDays(1)) {
                checkDate = checkDate.minusDays(1)
            }

            for (date in dates) {
                if (date == checkDate) {
                    currentStreak++
                    checkDate = checkDate.minusDays(1)
                } else {
                    break
                }
            }
            return currentStreak
        }

    fun getSustainabilityScore(avgEmission: Double = 120.0): Int {
        val userAvg = if (_carbonEntries.value.isEmpty()) 0.0
        else _carbonEntries.value.takeLast(7)
            .map { it.totalEmission }.average()
        return (100 - (userAvg / avgEmission * 100)).toInt().coerceIn(0, 100)
    }

    fun getLevel(points: Int): String = when {
        points >= 1000 -> "Planet Guardian 🌎"
        points >= 500  -> "Green Champion 🌳"
        points >= 250  -> "Eco Warrior 🛡️"
        points >= 100  -> "Eco Saver 🌿"
        else           -> "Seedling 🌱"
    }

    fun getLevelProgress(points: Int): Float = when {
        points >= 1000 -> 1f
        // Progress from 500 to 1000 (Range of 500)
        points >= 500  -> (points - 500) / 500f
        // Progress from 250 to 500 (Range of 250)
        points >= 250  -> (points - 250) / 250f
        // Progress from 100 to 250 (Range of 150)
        points >= 100  -> (points - 100) / 150f
        // Progress from 0 to 100 (Range of 100)
        else           -> points / 100f
    }

    fun getNextLevelLabel(points: Int): String = when {
        points >= 1000 -> "Max level reached!"
        points >= 500  -> "Planet Guardian 🌎"
        points >= 250  -> "Green Champion 🌳"
        points >= 100  -> "Eco Warrior 🛡️"
        else           -> "Eco Saver 🌿"
    }

    fun getPointsToNextLevel(points: Int): Int = when {
        points >= 500 -> 0
        points >= 100 -> 500 - points
        else          -> 100 - points
    }

    // ── Eco-Impact Conversions ──────────────────────────────────────────────
    fun getTreesEquivalent(kg: Double): Double = kg / 20.0
    fun getCarKmEquivalent(kg: Double): Double = kg * 4.0

    fun getSavedCo2Today(): Double {
        val dailyAvg = 15.0 // Average daily emission baseline
        return (dailyAvg - todayEmission).coerceAtLeast(0.0)
    }

    // ── Carbon calculation ────────────────────────────────────────────────────
    fun calculateTransport(distanceKm: Double):  Double = distanceKm * 0.21
    fun calculateElectricity(units: Double):     Double = units * 0.5
    fun calculateFood(meatMeals: Int):           Double = meatMeals * 3.3
    fun calculateTotal(t: Double, e: Double, f: Double): Double = t + e + f

    // ── Recommendations ───────────────────────────────────────────────────────
    fun getRecommendations(entry: CarbonEntry): List<String> {
        val tips = mutableListOf<String>()
        if (entry.transportEmission > 50)
            tips.add("Try public transport twice a week — save up to 30 kg CO₂/month")
        if (entry.electricityEmission > 40)
            tips.add("Switch to LED bulbs and unplug devices when idle")
        if (entry.foodEmission > 30)
            tips.add("Replace one meat meal per day with plant-based food")
        if (tips.isEmpty())
            tips.add("Great job! You are below average emissions. Keep it up!")
        return tips
    }

    // ── Gamification ─────────────────────────────────────────────────────────
    fun awardPoints(habitType: String): Int = when (habitType) {
        "Used public transport" -> 20
        "Ate vegetarian meal"   -> 15
        "Reduced electricity"   -> 10
        else                    -> 5
    }

    // ── Firebase: load user ───────────────────────────────────────────────────
    fun loadUser() {
        val uid = auth.currentUser?.uid ?: return
        db.child("Users").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _user.value = snapshot.getValue(UserModel::class.java)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ── Firebase: load carbon entries ─────────────────────────────────────────
    fun loadCarbonEntries() {
        val uid = auth.currentUser?.uid ?: return
        db.child("CarbonEntries").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(CarbonEntry::class.java)
                    }
                    _carbonEntries.value = list
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ── Firebase: save carbon entry ───────────────────────────────────────────
    fun saveCarbonEntry(entry: CarbonEntry, onDone: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val key = db.child("CarbonEntries").child(uid).push().key ?: return
        entry.id     = key
        entry.userId = uid
        db.child("CarbonEntries").child(uid).child(key)
            .setValue(entry)
            .addOnCompleteListener { onDone() }
    }

    // ── Firebase: load habits ─────────────────────────────────────────────────
    fun loadHabits() {
        val uid = auth.currentUser?.uid ?: return
        db.child("Habits").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(HabitModel::class.java)
                    }
                    _habits.value = list
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ── Firebase: save habit + update points ──────────────────────────────────
    fun saveHabit(habit: HabitModel, onDone: () -> Unit) {
        val uid    = auth.currentUser?.uid ?: return
        val key    = db.child("Habits").child(uid).push().key ?: return
        habit.id     = key
        habit.userId = uid
        db.child("Habits").child(uid).child(key)
            .setValue(habit)
            .addOnCompleteListener {
                updateUserPoints(uid, habit.points)
                onDone()
            }
    }

    // ── Firebase: update points ───────────────────────────────────────────────
    private fun updateUserPoints(uid: String, addPoints: Int) {
        val currentPoints = _user.value?.totalPoints ?: 0
        val newPoints     = currentPoints + addPoints
        db.child("Users").child(uid).child("totalPoints").setValue(newPoints)
        db.child("Users").child(uid).child("level").setValue(getLevel(newPoints))
    }

    // ── Firebase: sign out ────────────────────────────────────────────────────
    fun signOut() = auth.signOut()
}

fun saveCarbonEntry(
    transportEmission: Double,
    electricityEmission: Double,
    foodEmission: Double,
    totalEmission: Double,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val entry = CarbonEntry(
        userId              = userId,
        transportEmission   = transportEmission,
        electricityEmission = electricityEmission,
        foodEmission        = foodEmission,
        totalEmission       = totalEmission,
        date                = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
    val ref = FirebaseDatabase.getInstance().getReference("CarbonEntries").push()
    entry.id = ref.key
    ref.setValue(entry)
        .addOnSuccessListener {
            updateUserPoints(userId, 10)
            onSuccess()
        }
        .addOnFailureListener { onError(it.message ?: "Failed") }
}

