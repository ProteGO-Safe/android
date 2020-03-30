package pl.gov.mc.protego.ui.main

import androidx.lifecycle.ViewModel
import pl.gov.mc.protego.file.FileManager
import pl.gov.mc.protego.gcs.GoogleCloudStorage

class MainActivityViewModel(
    val fileManager: FileManager,
    val googleCloudStorage: GoogleCloudStorage
) : ViewModel() {

    fun sendSampleFileToGcs() {

    }
}