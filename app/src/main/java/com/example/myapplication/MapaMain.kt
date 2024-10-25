package com.example.myapplication


import android.content.Intent

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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
import com.google.firebase.ktx.Firebase

class MapaMain : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener{

    private lateinit var map: GoogleMap
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView




    companion object{
        const val REQUEST_CODE_LOCATION = 0 
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

    private fun createFragment(){ /* cargar mapa */

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) { /* se llama cuando el mapa es creado */
        map = googleMap //mapa se cree
        //createMarker() /* crea un marker en el mapa */
        enableLocation() //activa la localizacion
        map.setOnMyLocationButtonClickListener(this) //LLama al boton de ubicarse
        map.setOnMyLocationClickListener(this) //Llama al boton de tu ubicacion

        // Llama a la función para mover la cámara a la ubicación actual
        moveToCurrentLocation()
    }
// Comentado el marker para que no moleste
//    private fun createMarker() {
//        val coordinates = LatLng(-36.827132, -73.050156)
//        val marker = MarkerOptions().position(coordinates).title("Tu ubicación")
//        map.addMarker(marker)
//        map.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
//            5000,
//            null
//        )
//    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")//le quito la alerta de permisos
    private fun moveToCurrentLocation() {
        if (isLocationPermissionGranted()) { // Verifica si el permiso está concedido
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val coordinates = LatLng(it.latitude, it.longitude)
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                        1000, // Duración de 1 segundo para la animación
                        null
                    )
                } ?: Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Si no tiene el permiso, lo solicita
            requestLocationPermission()
        }
    }


    // Método para abrir el menú
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    /*regresa true o false segun este el permiso de localizacion activado */
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Confirma si el mapa esta funcionando segun el permiso
    private fun enableLocation(){
        if (!::map.isInitialized) return //si el mapa no esta inicializado, chao
        if (isLocationPermissionGranted()){ //si los permisos estan activos, activa la localizacion en tiempo real, si no...
            //si, corre el requestLocationPermission, osea que tiene permiso.
            //NO TOCAR
            map.isMyLocationEnabled = true  //NO TOCAR, el "error" es solo el programa diciendo que usa el permiso.
            //NO TOCAR
        }else{
            //no, corre de nuevo el permiso
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission(){// revisa los permisos
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos de Ubicacion", Toast.LENGTH_SHORT).show()//si rechazo los permisos, activalo tu
        }else{//pedimos permisos de nuevo
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    //
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Verifica si el resultado es para el código de solicitud de ubicación
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido: habilita la ubicación en el mapa y mueve la cámara
                if (::map.isInitialized) {
                    map.isMyLocationEnabled = true  // Habilita la ubicación en el mapa
                    moveToCurrentLocation()         // Mueve la cámara a la ubicación actual
                }
            } else {
                // Permiso denegado: muestra un mensaje al usuario
                Toast.makeText(
                    this,
                    "Acepta los permisos en Ajustes para activar la localización",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //Por algunos bugs, al desactivar los permisos mientras la app esta en uso u otros
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return //si el mapa no esta inicializado, chao
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled == false
            Toast.makeText(this, "Acepta los permisos en Ajustes para activar la localizacion", Toast.LENGTH_SHORT).show()
        }
    }

    //Controles boton de ubicacion
    override fun onMyLocationButtonClick(): Boolean {
        //Mensaje de prueba
        //Toast.makeText(this, "Boton Pulsado", Toast.LENGTH_SHORT).show()
        return false //en false, te lleva a tu ubicacion, el true se desactiva el boton
    }

    // Este método se llama cada vez que el usuario pulse en su ubicación
    override fun onMyLocationClick(location: Location) {
        // Mover y hacer zoom instantáneamente a la ubicación actual
        val coordinates = LatLng(location.latitude, location.longitude)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            10000, // Duración de 1 segundo para hacer la transición
            null
        )

        // Mensaje opcional
        Toast.makeText(this, "Este eres tú", Toast.LENGTH_SHORT).show()
    }

}


