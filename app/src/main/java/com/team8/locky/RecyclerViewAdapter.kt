package com.team8.locky


import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.team8.locky.Models.GSON.Reserva


class MyRecyclerViewAdapter// data is passed into the constructor
internal constructor(context: Context, private val mData: List<Reserva>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.reservas_record, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = mData[position] as Reserva
        holder.myTextViewNombreLugar.text = reserva.nombreLugar
        holder.myTextViewLockerId.text = "ID Locker: " + reserva.lockerId.toString()
        holder.myTextViewTiempInicFin.text = "Tiempo de Reserva: "+ reserva.tiempoInicio + " - " + reserva.tiempoFin
        holder.myTextViewTiempRet.text = "Retorno: "+ reserva.tiempoRetorno
        holder.myTextViewPrecio.text = "Precio: "+ reserva.precioTotal.toString()
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
          {
        internal var myTextViewNombreLugar: TextView
        internal var myTextViewLockerId: TextView
        internal var myTextViewTiempInicFin: TextView
        internal var myTextViewTiempRet: TextView
        internal var myTextViewPrecio: TextView


        init {
            myTextViewLockerId = itemView.findViewById(R.id.textViewReservaRecordLockerId)
            myTextViewNombreLugar = itemView.findViewById(R.id.textViewReservaNombreLugarId)
            myTextViewTiempInicFin = itemView.findViewById(R.id.textViewReservaRecordTiempoInicTiempoFinId)
            myTextViewTiempRet = itemView.findViewById(R.id.textViewReservaRecordTiemporRetornoId)
            myTextViewPrecio = itemView.findViewById(R.id.textViewReservaRecorPrecioTotalId)
        }
    }


    // allows clicks events to be caught
    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}