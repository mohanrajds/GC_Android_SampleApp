package com.zoho.gc.sampleapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zoho.gc.gc_base.ZDChatCallback
import com.zoho.gc.gc_base.ZDTheme
import com.zoho.gc.gc_base.ZDThemeType
import com.zoho.gc.gc_base.ZohoGCUtil
import com.zoho.gc.livechat.ZohoGC


class SettingsActivity : AppCompatActivity() {
    var showGC: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        showGC = findViewById(R.id.show_gc)
        showGC?.setOnClickListener {
            val preference = PreferenceManager.getDefaultSharedPreferences(this)
            val domain = preference.getString("domain", null)
            val orgId = preference.getString("orgId", null)
            val widgetId = preference.getString("widgetId", null)

            if (!domain.isNullOrEmpty() && !orgId.isNullOrEmpty() && !widgetId.isNullOrEmpty()) {
                launchGC(orgId, widgetId, domain)
            } else {
                Toast.makeText(it.context, "Some Info Missing", Toast.LENGTH_SHORT).show()
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val button: Preference? = findPreference("domain")
            val setSessionVariable: Preference? = findPreference("set_session_variable")
            val updateSessionVariable: Preference? = findPreference("update_session_variable")
            val clearDataBase: Preference? = findPreference("clear_db")
            val logger: SwitchPreferenceCompat? = findPreference("log")
            val theme: ListPreference? = findPreference("theme")
            logger?.isChecked?.let {
                ZohoGC.getInstance(requireContext()).enableLog(it)
            }
            theme?.value?.let { type ->
                setTheme(type)
            }

            setSessionVariable?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                setSessionVariable()
                true
            }

            updateSessionVariable?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    updateSessionVariable()
                    true
                }
            theme?.setOnPreferenceChangeListener { preference, newValue ->
                setTheme(newValue.toString())
                true
            }

            clearDataBase?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                clearDB()
                true
            }
            logger?.setOnPreferenceChangeListener { preference, newValue ->
                enableLogger(newValue as Boolean)
                true
            }

        }

        private fun setTheme(type: String) {
            when (type) {
                "light" -> {
                    val light = ZDTheme.Builder(false).build()
                    ZohoGCUtil.setThemeBuilder(light)
                    ZohoGCUtil.setThemeType(ZDThemeType.LIGHT)
                }
                "dark" -> {
                    val dark = ZDTheme.Builder(true).build()
                    ZohoGCUtil.setThemeBuilder(dark)
                    ZohoGCUtil.setThemeType(ZDThemeType.DARK)
                }
                "system" -> {
                    val light = ZDTheme.Builder(false).build()
                    val dark = ZDTheme.Builder(true).build()
                    ZohoGCUtil.setThemeBuilder(light)
                    ZohoGCUtil.setThemeBuilder(dark)
                    ZohoGCUtil.setThemeType(ZDThemeType.SYSTEM)
                }
                else -> {}
            }
        }

        private fun clearDB() {
            ZohoGC.getInstance(requireContext()).clearData(object :
                ZDChatCallback.ZDClearDataCallback {
                override fun onFailed(exception: Exception) {}
                override fun onSuccess() {}
            })
        }

        private fun enableLogger(isEnabled: Boolean) {
            ZohoGC.getInstance(requireContext()).enableLog(isEnabled)
        }

        private fun setSessionVariable() {
            val sessionVariableMap = HashMap<String, Any>()
            sessionVariableMap.apply {
                this["name"] = "exampleName"
                this["value"] = "exampleValue"
            }
            val sessionVariableList = ArrayList<HashMap<String, Any>>()
            sessionVariableList.add(sessionVariableMap)
            ZohoGC.getInstance(requireContext()).setSessionVariables(sessionVariableList)
        }

        private fun updateSessionVariable() {
            val sessionVariableMap = HashMap<String, Any>()
            sessionVariableMap.apply {
                this["name"] = "exampleName"
                this["value"] = "exampleValue"
            }
            val sessionVariableList = ArrayList<HashMap<String, Any>>()
            sessionVariableList.add(sessionVariableMap)
            ZohoGC.getInstance(requireContext()).updateSessionVariables(sessionVariableList)
        }

    }

    private fun launchGC(orgId: String, widgetId: String, domain: String) {
        ZohoGC.getInstance(this).show(
            this, orgId, widgetId, domain
        )
    }

}