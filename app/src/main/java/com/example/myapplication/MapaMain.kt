package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class MapaMain : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

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
                    // Acción al crear banda
                    val intent = Intent(this@MapaMain, CrearBandaActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Crear Banda seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_ver_bandas -> {
                    // Acción para ver bandas
                    Toast.makeText(this, "Ver Bandas seleccionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_cerrar_sesion -> {
                    FirebaseAuth.getInstance().signOut()
                    val intente = Intent(this@MapaMain,MainActivity::class.java)
                    Toast.makeText(this, "Cerrando Sesion", Toast.LENGTH_SHORT).show()
                    startActivity(intente)
                    true
                }
                else -> super.onOptionsItemSelected(menuItem)
            }
        }

        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    private fun createMarker() {
        val coordinates = LatLng(-36.827132, -73.050156)
        val marker = MarkerOptions().position(coordinates).title("Tu ubicación")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            5000,
            null
        )
    }

    // Método para abrir el menú
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }




}
