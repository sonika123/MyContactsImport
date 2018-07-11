package com.example.sonika.mycontactsimport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.sonika.mycontactsimport.R.id.*
import java.lang.reflect.Array
import android.app.Activity
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.ContextCompat.getSystemService




class SpinnerAdapter() : BaseAdapter() {
    override fun getCount(): Int {
        return countryNames!!.size
    }

    var flags:  kotlin.Array<Int>? = null
    var countryNames : kotlin.Array<String>? = null
    //var inflater: LayoutInflater? = null
    var context: Context? = null

    constructor(context: Context, flags: kotlin.Array<Int>, countryNames: kotlin.Array<String>) : this() {
        this.context = context
        this.flags = flags
        this.countryNames = countryNames
        //inflater = LayoutInflater.from(context)
    }

    /*private view holder class*/
    private inner class ViewHolder {
        var spinner_imgflag : ImageView? = null
        var spinner_countryname : TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder? = null

        if (convertView == null) {
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            convertView = inflater.inflate(R.layout.custom_spinner_items, null)
            holder = ViewHolder()
            holder.spinner_countryname = convertView.findViewById<View>(R.id.s_textView) as TextView
            holder.spinner_imgflag = convertView.findViewById<View>(R.id.s_imageView) as ImageView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }


        holder.spinner_countryname!!.text = countryNames!![position]
        holder.spinner_imgflag!!.setImageResource(flags!![position])

        return convertView!!
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }



}