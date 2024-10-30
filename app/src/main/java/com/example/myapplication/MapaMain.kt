package com.example.myapplication


import android.content.Intent

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import com.google.android.gms.maps.model.Marker // Agrega este import si no está
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class MapaMain : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val markersMap = mutableMapOf<String, Marker>() // Mapa para almacenar marcadores de usuarios

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapa_main)

        // Inicializar el DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Configurar la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar el ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejar selecciones del menú de navegación
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ver_perfil -> {
                    val intent = Intent(this@MapaMain, PerfilActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Ver perfil", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_crear_banda -> {
                    val intent = Intent(this@MapaMain, CrearBandaActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Crear Banda seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_ver_bandas -> {
                    val intent = Intent(this@MapaMain, VerBandaActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Ver Bandas seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_cerrar_sesion -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@MapaMain, MainActivity::class.java)
                    Toast.makeText(this, "Cerrando Sesion", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(menuItem)
            }
        }
        setupRealtimeLocationListener()
        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
    }

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun setupRealtimeLocationListener() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("Usuario")

        usersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                updateUserLocation(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                updateUserLocation(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val userId = snapshot.key ?: return
                markersMap[userId]?.remove()
                markersMap.remove(userId)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapaMain, "Error al cargar ubicaciones", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }

    private fun updateUserLocation(snapshot: DataSnapshot) {
        val userId = snapshot.key ?: return
        val nombre = snapshot.child("nombre").getValue(String::class.java) ?: return
        val locationData = snapshot.child("ubicacion")
        val lat = locationData.child("latitud").getValue(Double::class.java)
        val lng = locationData.child("longitud").getValue(Double::class.java)

        if (lat != null && lng != null) {
            val location = LatLng(lat, lng)
            val marker = markersMap[userId]

            if (marker == null) {
                markersMap[userId] = map.addMarker(
                    MarkerOptions().position(location).title(nombre)
                )!!
            } else {
                marker.position = location
                marker.title = nombre
            }
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos de Ubicacion", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Acepta los permisos en Ajustes para activar la localizacion", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean = false

    override fun onMyLocationClick(location: Location) {
        GuardarUbicacionEnBd(location)
        Toast.makeText(this, "Este eres tu", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Ubicación guardada: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
    }

    private fun GuardarUbicacionEnBd(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userLocationMap = mapOf(
                "latitud" to lat,
                "longitud" to lng
            )

            database.reference.child("Usuario").child(userId).child("ubicacion").updateChildren(userLocationMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ubicación actualizada en la base de datos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}