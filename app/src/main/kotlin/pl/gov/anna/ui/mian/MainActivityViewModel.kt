package pl.gov.anna.ui.mian

import androidx.lifecycle.ViewModel
import pl.gov.anna.file.FileManager
import pl.gov.anna.gcs.GoogleCloudStorage

class MainActivityViewModel(
    val fileManager: FileManager,
    val googleCloudStorage: GoogleCloudStorage
) : ViewModel() {

    fun sendSampleFileToGcs() {

    }
}