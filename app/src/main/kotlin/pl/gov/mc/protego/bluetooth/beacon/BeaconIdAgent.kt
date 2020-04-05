package pl.gov.mc.protego.bluetooth.beacon


/**
 * Class implementing this protocol is responsible for giving out valid Beacon IDs and
 * receiving them and storing locally.
 */
interface BeaconIdAgent {

    /**
     * This function should return valid Beacon ID with its expiration date
     * which will be exchanged between devices. Can return `nil` if there
     * are are no valid Beacon IDs available.
     */
    fun getBeaconId(): BeaconIdLocal?

    /**
     * This function is called when new Beacon ID is synchronized.
     * @param beaconIdRemote the BeaconIdRemote that was found
     */
    fun synchronizedBeaconId(beaconIdRemote: BeaconIdRemote)

    /**
     * This method registers [BeaconIdAgent.Listener]
     * @param listener the [Listener]
     */
    fun registerListener(listener: Listener)

    /**
     * This method unregisters [BeaconIdAgent.Listener]
     * @param listener the [Listener]
     */
    fun unregisterListener(listener: Listener)

    /**
     * Class implementing this protocol will be able to register for notifications
     * about current Beacon IDs to use.
     */
    interface Listener {
        /**
         * This method will be called whenever a new Beacon ID is to be
         * used by [BeaconIdAgent.Listener].
         * @param beaconIdLocal the Beacon ID to use. null if no valid Beacon ID available.
         */
        fun useBeaconId(beaconIdLocal: BeaconIdLocal?)
    }
}