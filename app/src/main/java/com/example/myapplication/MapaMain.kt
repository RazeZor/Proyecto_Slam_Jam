package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ChildEventListener
import android.widget.PopupMenu
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference

class MapaMain : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val userMarkers = mutableMapOf<String, Marker>() // Para rastrear los markers de cada usuario

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val intente = Intent(this@MapaMain, MainActivity::class.java)
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
        enableLocation()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        observeUsersLocation() // Escucha cambios en la ubicación de los usuarios en tiempo real
        map.setOnMarkerClickListener { marker ->
            // Mostrar un menú contextual cuando se hace clic en un marcador
            VerOpcionesParaElUsuario(marker)
            true // Indica que has manejado el evento
        }
    }

    private fun VerOpcionesParaElUsuario(marker: Marker) {
        val opciones = arrayOf("Invitar a Banda", "Ver Perfil")
        AlertDialog.Builder(this)
            .setTitle("Opciones")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> {
                        InvitarABanda(marker)
                    }
                    1 -> {
                        VerPerfil(marker)
                    }
                }
            }
            .show()
    }



    private fun VerPerfil(marker: Marker) {
        val userId = marker.tag as? String // Obtén el UID desde el tag
        if (userId != null) {
            val intent = Intent(this, VerPerfilDeOtros::class.java)
            intent.putExtra("id", userId)
            startActivity(intent)
        } else {
            Log.d("VerPerfil", "UID no encontrado")
        }
    }



    private fun InvitarABanda(marker: Marker) {

        }





    private fun obtenerMisBandas(callback: (List<Banda>) -> Unit) {

    }


    private fun observeUsersLocation() {
        val database = FirebaseDatabase.getInstance().reference.child("Usuario")

        // Listener para detectar cambios en la ubicación de otros usuarios
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                ActualizarUbicacionDeLosDemas(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                ActualizarUbicacionDeLosDemas(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val userId = snapshot.key
                userMarkers[userId]?.remove()
                userMarkers.remove(userId)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MapaMain, "Error al obtener ubicaciones: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun ActualizarUbicacionDeLosDemas(snapshot: DataSnapshot) {
        val userId = snapshot.key ?: return
        val lat = snapshot.child("ubicacion/latitud").getValue(Double::class.java) ?: return
        val lng = snapshot.child("ubicacion/longitud").getValue(Double::class.java) ?: return
        val nombre = snapshot.child("nombre").getValue(String::class.java) ?: "Usuario $userId"
        val userLocation = LatLng(lat, lng)

        // Redimensionar el logo
        val logoResource = R.drawable.headphone_11712743
        val originalBitmap = BitmapFactory.decodeResource(resources, logoResource)
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 50, 50, false)

        // Verifica si el marcador ya existe para el usuario
        if (userMarkers.containsKey(userId)) {
            userMarkers[userId]?.position = userLocation
        } else {
            // Crea un nuevo marcador
            val markerOptions = MarkerOptions()
                .position(userLocation)
                .title(nombre) // Aquí se muestra el nombre del usuario
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap))

            // Agrega el marcador al mapa y configura el UID en tag
            val marker = map.addMarker(markerOptions)
            marker?.tag = userId // Guarda el UID en tag para poder accederlo después
            userMarkers[userId] = marker!!
        }

        // Libera memoria del bitmap original
        originalBitmap.recycle()
    }


    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

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

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos de Ubicacion", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, "Acepta los permisos en Ajustes para activar la localizacion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Acepta los permisos en Ajustes para activar la localizacion", Toast.LENGTH_SHORT).show()
        }
    }
}