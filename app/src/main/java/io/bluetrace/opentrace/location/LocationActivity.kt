package io.bluetrace.opentrace.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import io.bluetrace.opentrace.R
import kotlinx.android.synthetic.main.activity_location.*
import java.text.DateFormat
import java.util.*

class LocationActivity : AppCompatActivity() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val MY_PERMISSION_FINE_LOCATION = 101
    private var locationRequest: LocationRequest? = null
    private var updateOn = true
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        locationRequest = LocationRequest()
        locationRequest!!.interval = 8000// use 10 or 15 sec
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        tbGps_Balanced.setOnClickListener {
            if (tbGps_Balanced.isChecked) {
                tvType.text = "GPS mode"
                locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            } else {
                tvType.text = "WiFi"
                locationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }
        }
        tbLocationOnOff.setOnClickListener {
            if (tbLocationOnOff.isChecked) {
                tvUpdates.text = "Off"
                updateOn = false
                stopLocationUpdates()
            } else {
                tvUpdates.text = "On"
                updateOn = true
                startLocationUpdates()
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )  {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    //update UI
                    tvLatitude.text = location.latitude.toString()
                    tvLongtitude.text = location.longitude.toString()
                    tvUpdates.text = java.text.DateFormat.getDateTimeInstance().format(java.util.Date())
                }
            }
        } else {
            //request permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_FINE_LOCATION
                )
            }
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                for (location in p0!!.locations) {
                    //update UI
                    if (location != null) {
                        //update UI
                        tvLatitude.text = location.latitude.toString()
                        tvLongtitude.text = location.longitude.toString()
                        tvUpdates.text = java.text.DateFormat.getDateTimeInstance().format(java.util.Date())
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Required location grant",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateOn) startLocationUpdates()
    }

    override fun onStart() {
        super.onStart()
        if (updateOn) startLocationUpdates()
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else {
            //request permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_FINE_LOCATION
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
    private fun stopLocationUpdates() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }
}