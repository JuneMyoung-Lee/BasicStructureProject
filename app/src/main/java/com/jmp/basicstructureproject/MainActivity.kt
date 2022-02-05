package com.jmp.basicstructureproject

import android.os.Bundle
import android.widget.Toast

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as AppApplication).appComponent.inject(this)

        setContentView(R.layout.activity_main)

        Toast.makeText(applicationContext, "앱 실행", Toast.LENGTH_SHORT).show()
    }
}