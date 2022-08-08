package com.example.opencv_aruco_test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.aruco.Aruco
import org.opencv.aruco.DetectorParameters
import org.opencv.aruco.Dictionary
import org.opencv.core.CvType
import org.opencv.core.MatOfInt
import org.opencv.imgproc.Imgproc
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.*

var shouldSendMarkers: Boolean = false
var markerData: String = ""
var shouldRunConnection: Boolean = true
val AXIS_VEC = Vec2(1.0, 0.0)

/**
 * A simple [Fragment] subclass.
 * Use the [ArucoManager.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArucoManager : Fragment(), CvCameraViewListener2 {
    private lateinit var camera: CameraBridgeViewBase
    private lateinit var dictionary: Dictionary
    private lateinit var parameters: DetectorParameters
    private lateinit var corners: LinkedList<Mat>
    private lateinit var rgb: Mat
    private lateinit var ids: MatOfInt
    private lateinit var rvecs: Mat
    private lateinit var tvecs: Mat


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aruco_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!")
        else
            Log.d("OpenCV", "OpenCV loaded Successfully! :)")

        var img: Mat? = null
        Log.d("OpenCV", "attempt cam 1")
        camera = view.findViewById(R.id.main_camera)
        Log.d("OpenCV", "attempt cam 2")
        camera.setVisibility(SurfaceView.VISIBLE)
        camera.setCameraPermissionGranted()
        camera.setCvCameraViewListener(this)
//        loadCameraParams()
        camera.setMaxFrameSize(360, 240)
        camera.enableView()
        Log.d("OpenCV", "attempt cam")

        var udpConnect = Thread(ClientSend()).start()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        rgb = Mat()
        corners = LinkedList()
        parameters = DetectorParameters.create()
        dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_100)
        Log.d("OpenCV", "Cam is go")
    }

    override fun onCameraViewStopped() {
        rgb.release()
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        Imgproc.cvtColor(inputFrame.rgba(), rgb, Imgproc.COLOR_RGBA2RGB)
        ids = MatOfInt()
        corners.clear()
        Aruco.detectMarkers(inputFrame.gray(), dictionary, corners, ids, parameters)

        markerData = ""
        if (corners.size > 0) {
            Aruco.drawDetectedMarkers(rgb, corners, ids)
            var idList = ids.toArray()
            var cornersList = corners.toArray()
            for (i in 0 until idList.size) {
                // calc the centerpoint
                val c0 = Vec2(corners[i][0,0][0], corners[i][0,0][1])
                val c1 = Vec2(corners[i][0,1][0], corners[i][0,1][1])
                val c2 = Vec2(corners[i][0,2][0], corners[i][0,2][1])
                val c3 = Vec2(corners[i][0,3][0], corners[i][0,3][1])
                val center = (c0 + c1 + c2 + c3) * 0.25
                val rotation = AXIS_VEC.angleBetween(c1 - c0)

                // for debugging that marker data
//                Log.d("OpenCV", center.x.toString() + " " + center.y.toString() + " " + rotation)
                markerData += idList[i].toString() + " " + center.x.toString() + " " + center.y.toString() + " " + rotation
                if (i + 1 < idList.size) markerData += ","
            }

        }
        // MIGHT WANT TO SEND WHEN THERE ARE NO MARKERS AS WELL
        // But also cap frame rate to not overload the buffer?
        shouldSendMarkers = true
        return rgb
    }
}

class ClientSend : Runnable {
    override fun run() {
        try {
            val udpSocket = DatagramSocket(8484)
            val serverAddr: InetAddress = InetAddress.getByName(GlobalData.ip)

            while (shouldRunConnection) {
                if (shouldSendMarkers) {
                    val buf = markerData.toByteArray()
                    val packet = DatagramPacket(buf, buf.size, serverAddr, 8484)
                    udpSocket.send(packet)
                    shouldSendMarkers = false
                }
            }
            udpSocket.close()
        } catch (e: SocketException) {
            Log.e("Udp:", "Socket Error:", e)
        } catch (e: IOException) {
            Log.e("Udp Send:", "IO Error:", e)
        }

        Log.d("OpenCV", "run over")
    }
}