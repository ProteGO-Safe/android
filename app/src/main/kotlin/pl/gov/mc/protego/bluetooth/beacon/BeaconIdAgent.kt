package pl.gov.mc.protego.bluetooth.beacon


/**
 * Class implementing this protocol is responsible for giving out Beacon IDs and
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
     * @param rssi the RSSI value for the BeaconId if available
     */
    fun synchronizedBeaconId(beaconIdRemote: BeaconIdRemote, rssi: Int?)
}