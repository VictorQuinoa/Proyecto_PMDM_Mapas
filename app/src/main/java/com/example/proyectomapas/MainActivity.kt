package com.example.proyectomapas

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
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
    private lateinit var formContainer: LinearLayout
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonAddMarker: Button

    private val markers = mutableListOf<MarkerData>()
    private var tempMarkerPosition: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar las vistas
        formContainer = findViewById(R.id.formContainer)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonAddMarker = findViewById(R.id.buttonAddMarker)

        // Configurar el fragmento del mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar el botón para agregar un marcador
        buttonAddMarker.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            if (title.isNotBlank() && description.isNotBlank()) {
                tempMarkerPosition?.let { position ->
                    addMarkerToMap(position, title, description)
                    saveMarkers()
                }
                hideForm()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        loadMarkers()

        // Listener para agregar marcador al tocar el mapa
        map.setOnMapClickListener { latLng ->
            tempMarkerPosition = latLng
            showForm()
        }

        // Listener para mostrar información al tocar un marcador
        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow() // Mostrar información del marcador
            true // Retornar true para evitar comportamiento por defecto
        }

        // Listener para eliminar marcador con un click prolongado
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {}
        })
        map.setOnInfoWindowLongClickListener { marker ->
            showDeleteConfirmationDialog(marker) // Mostrar confirmación al mantener pulsado
        }
    }

    private fun addMarkerToMap(position: LatLng, title: String, description: String) {
        val markerOptions = MarkerOptions()
            .position(position)
            .title(title)
            .snippet(description)
        map.addMarker(markerOptions)

        // Guardar datos en la lista de marcadores
        markers.add(MarkerData(position.latitude, position.longitude, title, description))
    }

    private fun saveMarkers() {
        val sharedPreferences = getSharedPreferences("markers_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(markers)
        editor.putString("markers", json)
        editor.apply()
    }

    private fun loadMarkers() {
        val sharedPreferences = getSharedPreferences("markers_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("markers", null)
        if (json != null) {
            val type = object : TypeToken<List<MarkerData>>() {}.type
            val loadedMarkers: List<MarkerData> = gson.fromJson(json, type)
            markers.clear()
            markers.addAll(loadedMarkers)

            // Agregar los marcadores al mapa
            for (markerData in markers) {
                val position = LatLng(markerData.latitude, markerData.longitude)
                addMarkerToMap(position, markerData.title, markerData.description)
            }
        }
    }

    private fun showForm() {
        formContainer.visibility = View.VISIBLE
    }

    private fun hideForm() {
        formContainer.visibility = View.GONE
        editTextTitle.text.clear()
        editTextDescription.text.clear()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDeleteConfirmationDialog(marker: Marker) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este marcador?")
            .setPositiveButton("Sí") { _, _ ->
                removeMarker(marker)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun removeMarker(marker: Marker) {
        // Eliminar marcador del mapa
        marker.remove()

        // Eliminar marcador de la lista persistente
        markers.removeIf { it.latitude == marker.position.latitude && it.longitude == marker.position.longitude }
        saveMarkers()

        Toast.makeText(this, "Marcador eliminado", Toast.LENGTH_SHORT).show()
    }

    data class MarkerData(
        val latitude: Double,
        val longitude: Double,
        val title: String,
        val description: String
    )
}
