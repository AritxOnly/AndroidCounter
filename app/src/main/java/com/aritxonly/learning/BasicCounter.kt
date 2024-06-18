package com.aritxonly.learning

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.aritxonly.learning.MainActivity.Companion.checkNegEnable
import com.aritxonly.learning.MainActivity.Companion.checkVoiceEnable
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BasicCounter.newInstance] factory method to
 * create an instance of this fragment.
 */
class BasicCounter : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {// 按钮监听事件

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_counter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        btnPlus?.setOnClickListener {
            Log.d("Basic Counter", "btnPlus clicked")
            countIncrease(view)
        }

        val btnMinus = view.findViewById<Button>(R.id.btnMinus)
        btnMinus?.setOnClickListener {
            Log.d("Basic Counter", "btnMinus clicked")
            countDecrease(view)
        }

        val btnNew = view.findViewById<FloatingActionButton>(R.id.newCount)
        btnNew?.setOnClickListener {
            Log.d("Basic Counter", "btnNew clicked")
            newCount(view)
        }

        Toast.makeText(mActivity, "计数已清空", Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mActivity = context as MainActivity
        } else {
            throw IllegalStateException("Activity must be an instance of MainActivity")
        }
    }

    fun newCount(view: View) {
        val countText: TextView = view.findViewById(R.id.text)

        val alert: AlertDialog.Builder = AlertDialog.Builder(mActivity as Context)

        alert.setTitle("新计数")
            .setMessage("确定要开启一次新的计数吗？")
            .setPositiveButton("确定") { dialog, which ->
                countText.text = "开始新的计数"
            }.setNegativeButton("取消") { dialog, which ->

            }.create().show()

    }

    private fun counter(add: Int) {
        val countText: TextView? = view?.findViewById(R.id.text)

        val curText: String = countText?.text.toString()
        var curCount: Int = 0

        if(curText != "开始新的计数")
            curCount = curText.toInt()

        if(!checkNegEnable()) {
            if (curCount + add >= 0) {
                curCount += add
                countText?.text = curCount.toString()
            } else {
                val alert: AlertDialog.Builder = AlertDialog.Builder(mActivity as Context)
                alert.setTitle("提示")
                    .setMessage("无法计数负数")
                    .setNeutralButton("确定") { dialog, which ->
                        if(curCount != 0)
                            Toast.makeText(mActivity, "已自动重置为0", Toast.LENGTH_LONG).show()
                        curCount = 0
                        countText?.text = curCount.toString()
                    }.create().show()
            }
        } else {
            curCount += add
            countText?.text = curCount.toString()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BasicCounter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasicCounter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun countDecrease(view: View) {
        Log.d("Basic Counter", "count decreased")
        counter(add=-1)
        if(checkVoiceEnable()) {
            mActivity?.vibrate()
            mActivity?.generateVoice()
        }
    }
    private fun countIncrease(view: View) {
        Log.d("Basic Counter", "count increased")
        counter(add=1)
        if(checkVoiceEnable()) {
            mActivity?.vibrate()
            mActivity?.generateVoice()
        }
    }
}