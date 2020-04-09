package pl.gov.mc.protego.bluetooth

import pl.gov.mc.protego.bluetooth.beacon.BeaconId
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdAgent
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdLocal
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdRemote
import timber.log.Timber
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

// TODO: [DS 07-Apr-2020] Create proper implementation. Currently it is a dummy.
/**
 * Class responsible for [BeaconIdLocal] and [BeaconIdRemote] handling. Granular responsibilities:
 * 1. Registering and unregistering [listeners]
 * 2. Distributing valid [BeaconIdLocal] to [listeners]
 * 3. Storing received [BeaconIdRemote]
 *
 * This class implements basic measures to be used in a multithreaded context (it should not crash).
 */
class BeaconIdManager : BeaconIdAgent {

    private val beaconIdPart = AtomicInteger(0x0f)
    private val handler = safeCurrentThreadHandler()
    private fun createNewBeaconId(): BeaconIdLocal {
        val newBeaconId = BeaconIdLocal(
            BeaconId(
                byteArrayOf(
                    0x00,
                    0x01,
                    0x02,
                    0x03,
                    0x04,
                    0x05,
                    0x06,
                    0x07,
                    0x08,
                    0x09,
                    0x0a,
                    0x0b,
                    0x0c,
                    0x0d,
                    0x0e,
                    beaconIdPart.getAndIncrement().toByte()
                ), ProteGoManufacturerDataVersion
            ), Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3))
        )
        handler.postDelayed({ currentBeaconId = createNewBeaconId() }, newBeaconId.expirationDate.time - System.currentTimeMillis())
        return newBeaconId
    }

    private var listeners = CopyOnWriteArraySet<BeaconIdAgent.Listener>()

    private var currentBeaconId = createNewBeaconId()
        set (value) {
            listeners.forEach { it.useBeaconId(value) }
            field = value
        }

    override fun getBeaconId(): BeaconIdLocal? = currentBeaconId

    override fun registerListener(listener: BeaconIdAgent.Listener) {
        listeners.add(listener)
        listener.useBeaconId(currentBeaconId)
    }

    override fun unregisterListener(listener: BeaconIdAgent.Listener) {
        listeners.remove(listener)
    }

    override fun synchronizedBeaconId(beaconIdRemote: BeaconIdRemote) {
        Timber.i("Synchronized: $beaconIdRemote")
    }
}