package com.duakelinci.core.helper

import java.io.IOException

class NoConnectivityException(message: String = "Tidak Ada Koneksi Internet") : IOException(message)
class LocationPermissionNotGrantedException(message: String = "Akses Lokasi Tidak Diizinkan") : Throwable(message)
class DateNotFoundException : Throwable("Date Not Found")
class NoDataFoundException(message: String? = "Tidak Ada Data") : Throwable(message)
class ApiResponseException(message: String) : Throwable(message)
class UserEmptyException : Throwable("User is Empty")