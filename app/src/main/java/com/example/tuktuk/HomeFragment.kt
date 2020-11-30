package com.example.tuktuk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tuktuk.Adapter.MediaAdapter
import com.example.tuktuk.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater,
            R.layout.fragment_home,container,false)

        binding.toProfile.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val exampleList = generateDummyList(20)
        recycler_view.adapter = MediaAdapter(exampleList)
        recycler_view.layoutManager = LinearLayoutManager(this.requireContext())
        recycler_view.setHasFixedSize(true)

    }

    private fun generateDummyList(size: Int): List<MediaObject> {
        val list = ArrayList<MediaObject>()
        for (i in 0 until size) {
            val item = MediaObject( "Item $i", "Line 2")
            list += item
        }
        return list
    }

}