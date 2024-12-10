package com.example.proyectomapas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {
    class BasicMapDemoActivity : AppCompatActivity(), OnMapReadyCallback {

        val SYDNEY = LatLng(-33.862, 151.21)
        val ZOOM_LEVEL = 13f

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            val mapFragment : SupportMapFragment? =
                supportFragmentManager.findFragmentById(R.id.main) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just move the camera to Sydney and add a marker in Sydney.
         */
        override fun onMapReady(googleMap: GoogleMap) {
            with(googleMap) {
                moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, ZOOM_LEVEL))
                addMarker(MarkerOptions().position(SYDNEY))
            }
        }
    }

}

