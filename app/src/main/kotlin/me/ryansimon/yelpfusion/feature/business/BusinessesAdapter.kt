package me.ryansimon.yelpfusion.feature.business

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
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
        val context = holder.businessImageView.context

        holder.businessNameTextView.text = business.name

        holder.businessImageView.contentDescription =
                context.getString(R.string.business_img_accessibility_msg, business.name)
        Glide.with(context)
                .load(business.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.businessImageView)
    }

    override fun getItemCount(): Int = businesses.size

    class BusinessViewHolder(itemView: View,
                             val businessNameTextView: TextView = itemView.business_name_tv,
                             val businessImageView: ImageView = itemView.business_img)
        : RecyclerView.ViewHolder(itemView)
}