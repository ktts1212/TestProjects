package com.example.androiddemo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.androiddemo.R
import com.example.androiddemo.childFragments.FindFragment
import com.example.androiddemo.childFragments.FollowFragment
import com.example.androiddemo.childFragments.LocalFragment
import com.example.androiddemo.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var tableLayout: TableLayout
    private lateinit var viewpaper: ViewPager2
    private val tabTitles= mutableListOf<String>("关注","发现","郑州")
    private val fragments= listOf(lazy { FollowFragment()},lazy {FindFragment()},lazy {LocalFragment()})
    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter=viewPaperAdapter()
        binding.viewPaper.adapter=adapter
        TabLayoutMediator(binding.tabHeader,binding.viewPaper){
                tab,position->
            tab.text=tabTitles[position]
        }.attach()
    }
    inner class viewPaperAdapter: FragmentStateAdapter(this){
        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position].value as Fragment
        }
    }
}