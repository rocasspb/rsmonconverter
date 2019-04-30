package com.rocasspb.rsmonconverter

data class DataLine(var throttlePercent : Int = 0, var brakePressure : Double = .0,
                    var steeringAngle : Int = 0, var gear: Byte = 0,
                    var speed : Double = 0.0, var rpm: Int = 0,
                    var tempOil: Int = 0, var tempCoolant: Int = 0,
                    var tempGearbox: Int = 0, var tempClutch: Int = 0,
                    var tempIntake: Int = 0, var tempExternal: Int = 0,
                    var gpsLat : Double = 0.0, var gpsLon : Double = 0.0,
                    var wheelRR: Int = 0, var wheelRL: Int = 0,
                    var wheelFR: Int = 0, var wheelFL: Int = 0,
                    var accelLat: Double = 0.0, var accelLon: Double = 0.0,
                    var boost: Int = 0, var power: Int = 0, var torque: Int = 0,
                    val relativeTime: Double = 0.0, val gpsUpdate : Boolean = false)
        


