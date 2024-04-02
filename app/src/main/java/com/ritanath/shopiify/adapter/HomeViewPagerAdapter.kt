package com.ritanath.shopiify.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(
    private val list:List<Fragment>
    ,
    private val fragmentManager:FragmentManager,
    private val lifecycle:Lifecycle
) :FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}