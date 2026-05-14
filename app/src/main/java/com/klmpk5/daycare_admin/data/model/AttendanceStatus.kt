package com.klmpk5.daycare_admin.data.model

enum class AttendanceStatus (
    val label: String,
    val value: String
    ) {
    PRESENT("Hadir","PRESENT"),
    ABSENT("Alpha","ABSENT"),
    SICK("Sick","SICK"),
    PERMISSION("Izin","PERMISSION")
    }
