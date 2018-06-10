package me.ryansimon.yelpfusion.feature.business

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_yelp_business.view.*
import me.ryansimon.yelpfusion.R

/**
 * @author Ryan Simon
 */
class BusinessesAdapter(val businesses: MutableList<Business>)
    : RecyclerView.Adapter<BusinessesAdapter.BusinessViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        return BusinessViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_yelp_business, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val business = businesses[position]
        holder.businessNameTextView.text = business.name
        // TODO: add image loading here
    }

    override fun getItemCount(): Int = businesses.size

    class BusinessViewHolder(itemView: View,
                             val businessNameTextView: TextView = itemView.business_name_tv,
                             val businessImageView: ImageView = itemView.business_img)
        : RecyclerView.ViewHolder(itemView)
}