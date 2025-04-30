package com.example.drivr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val LOCATION_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            return
        }

        googleMap.isMyLocationEnabled = true
        loadUserLocation()
    }

    private fun loadUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

                saveLocationToFirestore(currentLatLng)
                loadNearbyUsers(currentLatLng)
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLocationToFirestore(latLng: LatLng) {
        val userId = auth.currentUser?.uid ?: return
        val locationMap = mapOf(
            "lat" to latLng.latitude,
            "lng" to latLng.longitude
        )

        db.collection("Users").document(userId)
            .update("location", locationMap)
    }

    private fun loadNearbyUsers(currentLatLng: LatLng) {
        db.collection("Users").get().addOnSuccessListener { result ->
            for (document in result) {
                val userId = auth.currentUser?.uid
                if (document.id == userId) continue  // skip self

                val data = document.data
                val username = document.getString("username") ?: "User"
                val location = data["location"] as? Map<*, *>
                val lat = (location?.get("lat") as? Number)?.toDouble()
                val lng = (location?.get("lng") as? Number)?.toDouble()

                if (lat != null && lng != null &&
                    isWithinRadius(currentLatLng.latitude, currentLatLng.longitude, lat, lng, 10.0)) {

                    val profilePicName = document.getString("profilePicture") ?: "pfp1"
                    val resourceId = resources.getIdentifier(profilePicName, "drawable", packageName)

                    if (resourceId != 0) {
                        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
                        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
                        val userLatLng = LatLng(lat, lng)

                        googleMap.addMarker(
                            MarkerOptions()
                                .position(userLatLng)
                                .title(username)
                                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                        )
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isWithinRadius(
        userLat: Double,
        userLng: Double,
        otherLat: Double,
        otherLng: Double,
        radiusInKm: Double
    ): Boolean {
        val userLocation = Location("").apply {
            latitude = userLat
            longitude = userLng
        }

        val otherLocation = Location("").apply {
            latitude = otherLat
            longitude = otherLng
        }

        val distanceInMeters = userLocation.distanceTo(otherLocation)
        return distanceInMeters <= radiusInKm * 1000
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(googleMap)
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }
}
