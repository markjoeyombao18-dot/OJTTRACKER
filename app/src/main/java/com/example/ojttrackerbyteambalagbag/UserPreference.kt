package com.example.ojttrackerbyteambalagbag

import android.content.Context

class UserPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(
        "OJT_TRACKER_PREFS",
        Context.MODE_PRIVATE
    )

    fun saveAccount(
        fullName: String,
        email: String,
        password: String,
        school: String,
        course: String,
        yearSection: String,
        company: String,
        requiredHours: String,
        profileImage: String
    ) {
        preferences.edit()
            .putString("full_name", fullName)
            .putString("email", email)
            .putString("password", password)
            .putString("school", school)
            .putString("course", course)
            .putString("year_section", yearSection)
            .putString("company", company)
            .putString("required_hours", requiredHours)
            .putString("profile_image", profileImage)
            .putBoolean("account_created", true)
            .apply()
    }

    fun login(email: String, password: String): Boolean {

        val savedEmail = preferences.getString("email", "")
        val savedPassword = preferences.getString("password", "")

        return email == savedEmail && password == savedPassword
    }

    fun setLoggedIn(loggedIn: Boolean) {

        preferences.edit()
            .putBoolean("logged_in", loggedIn)
            .apply()
    }

    fun isLoggedIn(): Boolean {

        return preferences.getBoolean(
            "logged_in",
            false
        )
    }

    fun hasAccount(): Boolean {

        return preferences.getBoolean(
            "account_created",
            false
        )
    }

    fun getFullName(): String {

        return preferences.getString(
            "full_name",
            ""
        ) ?: ""
    }

    fun getEmail(): String {

        return preferences.getString(
            "email",
            ""
        ) ?: ""
    }

    fun getSchool(): String {

        return preferences.getString(
            "school",
            ""
        ) ?: ""
    }

    fun getCourse(): String {

        return preferences.getString(
            "course",
            ""
        ) ?: ""
    }

    fun getYearSection(): String {

        return preferences.getString(
            "year_section",
            ""
        ) ?: ""
    }

    fun getCompany(): String {

        return preferences.getString(
            "company",
            ""
        ) ?: ""
    }

    fun getRequiredHours(): String {

        return preferences.getString(
            "required_hours",
            "500"
        ) ?: "500"
    }

    fun getProfileImage(): String {

        return preferences.getString(
            "profile_image",
            ""
        ) ?: ""
    }

    fun logout() {

        preferences.edit()
            .putBoolean("logged_in", false)
            .apply()
    }
    fun updateProfile(
        fullName: String,
        email: String,
        school: String,
        course: String,
        yearSection: String,
        company: String,
        requiredHours: String,
        profileImage: String
    ) {
        preferences.edit()
            .putString("full_name", fullName)
            .putString("email", email)
            .putString("school", school)
            .putString("course", course)
            .putString("year_section", yearSection)
            .putString("company", company)
            .putString("required_hours", requiredHours)
            .putString("profile_image", profileImage)
            .apply()
    }
}