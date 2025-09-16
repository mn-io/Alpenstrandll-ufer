package net.mnio.alpenstrandlaufer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mnio.alpenstrandlaufer.data.AirplaneSession
import net.mnio.alpenstrandlaufer.data.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private val airplaneButtonClickListener: (View) -> Unit = {
        val hasPermission = checkCallingOrSelfPermission(
            android.Manifest.permission.WRITE_SECURE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Toast.makeText(this, "No permission to change airplane mode", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
        } else {
            try {
                Settings.Global.putInt(
                    contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON,
                    1 // or 0
                )
                sendBroadcast(Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED))
            } catch (se: SecurityException) {
                Log.e("AirplaneMode", "No permission: ${se.message}")
                Toast.makeText(
                    this,
                    "No permission to change airplane mode (error)",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
            }
        }
    }

    private val onDoubleTap: (AirplaneSession) -> Unit = { session ->
        // Double-tap callback: delete from DB and reload
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(this@MainActivity).sessionDao()
            dao.delete(session)
            loadSessions()
        }
    }

    private val reloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadSessions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRootView()
        initView()
        loadSessions()
        startService()
    }

    private fun initRootView() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        val fab1: FloatingActionButton = findViewById(R.id.fab1)
        fab1.setOnClickListener(airplaneButtonClickListener)

        recyclerView = findViewById(R.id.recyclerSessions)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = AirplaneSessionAdapter(emptyList(), onDoubleTap)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            loadSessions()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadSessions() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(this@MainActivity).sessionDao()
            val sessions = dao.getAll()

            withContext(Dispatchers.Main) {
                (recyclerView.adapter as? AirplaneSessionAdapter)?.updateData(sessions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadSessions()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(reloadReceiver, IntentFilter(ACTION_RELOAD_SESSION_INTENT_NAME))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(reloadReceiver)
    }
    private fun startService() {
        val intent = Intent(this, AirplaneModeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
