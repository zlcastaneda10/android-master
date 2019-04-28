package com.team8.locky

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.team8.locky.Models.GSON.Reserva

class ReservaAdapter(val mCtx: Context,val layoutResId: Int,val resevasList: MutableList<Reserva>)
    :ArrayAdapter<Reserva>(mCtx,layoutResId,resevasList){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId,null)

        val textViewName = view.findViewById<TextView>(R.id.textViewListaReservaId)
        val button = view.findViewById<Button>(R.id.buttonListaReserveId)
        val reserva = resevasList[position]
        textViewName.text = reserva.nombreLugar
        button.text = "DETALLES"
        button.tag = reserva.id

        return view
    }
}