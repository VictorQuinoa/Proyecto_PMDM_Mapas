package com.example.proyectomapas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private val markers = mutableListOf<MarkerData>()
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonAddMarker: Button
    private lateinit var formContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar las vistas
        formContainer = findViewById(R.id.formContainer)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonAddMarker = findViewById(R.id.buttonAddMarker)

        createMapFragment()

        buttonAddMarker.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            if (title.isNotBlank() && description.isNotBlank()) {
                addMarker(marker!!.position, title, description)
                editTextTitle.text.clear()
                editTextDescription.text.clear()
                formContainer.visibility = View.GONE
            }
        }
    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapClickListener()
        loadMarkers() // Cargar marcadores
    }

    private fun setMapClickListener() {
        map.setOnMapClickListener { latLng ->
            marker?.remove()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Nuevo marcador")
                    .snippet("Descripción del lugar tocado")
            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            formContainer.visibility = View.VISIBLE
        }
    }

    private fun addMarker(latLng: LatLng, title: String, description: String) {
        marker?.remove()
        marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(description)
        )
        markers.add(MarkerData(latLng.latitude, latLng.longitude, title, description))
        saveMarkers()
    }

    private fun saveMarkers() {
        val sharedPreferences = getSharedPreferences("MapApp", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(markers)
        editor.putString("markers", json)
        editor.apply()
    }

    private fun loadMarkers() {
        val sharedPreferences = getSharedPreferences("MapApp", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("markers", null)
        if (json != null) {
            val type = object : TypeToken<List<MarkerData>>() {}.type
            val savedMarkers: List<MarkerData> = gson.fromJson(json, type)
            markers.clear()
            markers.addAll(savedMarkers)
            for (markerData in markers) {
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(markerData.latitude, markerData.longitude))
                        .title(markerData.title)
                        .snippet(markerData.description)
                )
            }
        }
    }

    data class MarkerData(
        val latitude: Double,
        val longitude: Double,
        val title: String,
        val description: String
    )
}
