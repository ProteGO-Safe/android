package pl.gov.mc.protegosafe.domain.usecase

class DiagnosisKeysFileNameToTimestampUseCase {
    companion object {
        /*
         * Regular expression for diagnosis keys file names that have the following format:
         * "/1602849600-1602864000-00001.zip", where "1589918499" is a timestamp with file creation time in
         * Unix epoch in seconds and "00001" is key version.
         */
        private val DIAGNOSIS_KEYS_FILE_NAME_REGEX = "/?([0-9]{10})-([0-9]{5}).zip".toRegex()
    }

    /**
     * Provide Unix epoch in seconds from Diagnosis Keys file name.
     */
    fun execute(fileName: String): Long? {
        return DIAGNOSIS_KEYS_FILE_NAME_REGEX.find(fileName)?.let { match ->
            try {
                match.groupValues.getOrNull(1)?.toLong()
            } catch (e: NumberFormatException) {
                return@let null
            }
        }
    }
}
