package com.example.proyectomapas

import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.main) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val searchBar = findViewById<EditText>(R.id.search_bar)

        // Detectar cuando el usuario presiona "Enter" o similar
        searchBar.setOnEditorActionListener { _, _, _ ->
            val location = searchBar.text.toString()
            if (location.isNotEmpty()) {
                searchLocation(location)
            }
            true
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        // Configuración inicial del mapa (opcional)
        val initialLocation = LatLng(42.23282, -8.72264) // Vigo
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13f))
        googleMap.addMarker(MarkerOptions().position(initialLocation).title("Marcador en Vigo"))
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(location, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)

                // Mover la cámara al destino
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                // Añadir un marcador en el destino
                googleMap.addMarker(MarkerOptions().position(latLng).title(location))
            } else {
                Toast.makeText(this, "No se encontró la ubicación", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al buscar ubicación", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}

