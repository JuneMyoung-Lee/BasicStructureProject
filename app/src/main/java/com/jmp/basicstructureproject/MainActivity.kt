package com.jmp.basicstructureproject

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.jmp.basicstructureproject.databinding.ActivityMainBinding
import com.jmp.basicstructureproject.presentation.MainFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as AppApplication).appComponent.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle: Bundle? = null

        if (savedInstanceState == null) {
            if(!MainFragment.newInstance(bundle = bundle).isAdded){
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.mainFragmentHolder,
                        MainFragment.newInstance(bundle = bundle)
                    )
                    .commitNow()

                window.run {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    statusBarColor = 0x00000000  // transparent
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }
}