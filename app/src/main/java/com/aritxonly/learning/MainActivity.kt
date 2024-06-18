package com.aritxonly.learning

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.icu.util.Output
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColor
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.BaseOnChangeListener
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.Buffer

class MainActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 初始化底栏与底栏监听事件
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.basicCounter -> {
                    // 加载HomeFragment
                    loadFragment(BasicCounter.newInstance("", ""))
                    return@setOnItemSelectedListener true
                }
                R.id.settings -> {
                    // 加载SettingsFragment
                    loadFragment(Settings.newInstance("", ""))
                    return@setOnItemSelectedListener true
                }
                // 其他菜单项...
                else -> return@setOnItemSelectedListener false
            }
        }

        // 导航栏沉浸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.navigationBarColor = ResourcesCompat.getColor(resources,
//                com.google.android.material.R.color.design_default_color_surface, null) // 设置你的颜色
        }

        // 导入设置
        val settings: String = load()
        settingsLoader(settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun loadFragment(fragment: Fragment) {
        settingsSaver()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_fragment, fragment)
        transaction.commit()
    }

    private var mediaPlayer: MediaPlayer? = null
    public fun generateVoice() {
        if(mediaPlayer != null && (mediaPlayer!!.isPlaying)) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.perc)
        mediaPlayer?.start()
        // 内存释放
        if(!(mediaPlayer!!.isPlaying)) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    public fun vibrate() {
        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(80)
        }
    }

    private fun save(inputText: String) {
        try {
            val output = openFileOutput("Settings", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.write(inputText)
            }
        } catch(e: IOException) { e.printStackTrace() }
    }

    private fun load(): String {
        val content = StringBuilder()
        try {
            val input = openFileInput("Settings")
            val reader = BufferedReader(InputStreamReader(input))
            input.use {
                reader.forEachLine { content.append(it) }
            }
        } catch(e: IOException) { e.printStackTrace() }
        return content.toString()
    }

    private fun settingsLoader(content: String) {
        val settingsList = content.split(';')
        for(each in settingsList) {
            val setting = each.split('=')
            if(setting[0] == "allowNegative")
                negEnable = setting[1].toInt() != 0
            if(setting[0] == "allowVoice")
                voiceEnable = setting[1].toInt() != 0
            else
                continue
        }
    }

    private fun settingsSaver() {
        var content: String = ""
        content += "allowNegative="
        content += if(negEnable) "1" else "0"
        content += ";"
        content += "allowVoice="
        content += if(voiceEnable) "1" else "0"
        save(content)
    }

    companion object {
        private var negEnable: Boolean = false
        private var voiceEnable: Boolean = false

        public fun setNegEnable(x: Boolean) { negEnable = x }
        public fun checkNegEnable(): Boolean = negEnable

        public fun setVoiceEnable(x: Boolean) { voiceEnable = x }
        public fun checkVoiceEnable(): Boolean = voiceEnable
    }
}