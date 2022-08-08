package com.example.opencv_aruco_test

import android.R.attr.port
import androidx.navigation.findNavController
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


import com.example.opencv_aruco_test.databinding.FragmentIpInputBinding
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException


class IpInput : Fragment() {
    private var _binding: FragmentIpInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIpInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.connect.setOnClickListener {
            Log.d("OpenCV", "attempt connect")
            var ip = binding.editTextPhone.text.toString()
//            GlobalData.ip = ip
            GlobalData.ip = "192.168.0.194"
            Log.d("OpenCV", GlobalData.ip)

            view.findNavController().navigate(R.id.action_ip_to_aruco)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}