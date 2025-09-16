package net.mnio.alpenstrandlaufer

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.mnio.alpenstrandlaufer.data.AirplaneSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AirplaneSessionAdapter(
    private var sessions: List<AirplaneSession>,
    private val onDoubleTap: (AirplaneSession) -> Unit
) : RecyclerView.Adapter<AirplaneSessionAdapter.SessionViewHolder>() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    @SuppressLint("ClickableViewAccessibility")
    inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val root: MaterialCardView = view.findViewById(R.id.session_item_card_root)
        val title: TextView = view.findViewById(R.id.session_item_card_title)
        val body: TextView = view.findViewById(R.id.session_item_card_body)

        val circle: View = itemView.findViewById(R.id.session_item_color_circle)

        private val gestureDetector =
            GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (adapterPosition >= sessions.size) {
                        return false;
                    }
                    val session = sessions[adapterPosition]
                    onDoubleTap(session)
                    return true
                }
            })

        init {
            root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_airplane_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val circleView = holder.circle // reference to your circle View in ViewHolder
        val circleDrawable = GradientDrawable()
        circleDrawable.shape = GradientDrawable.OVAL
        circleView.background = circleDrawable

        if (position >= sessions.size) {
            circleDrawable.setColor(Color.TRANSPARENT)
            val context = holder.itemView.context
            holder.title.text = context.getString(R.string.duration_hh_mm_ss)
            holder.body.text = context.getString(R.string.double_tab_to_delete_an_entry)
            holder.root.alpha = 0.5f
            holder.root.isClickable = false
            holder.root.isFocusable = false
            return;
        }
        val session = sessions[position]
        circleDrawable.setColor(calculateColorForSession(session))
        holder.title.text = buildString {
            append("Duration: ")
            append(session.getDurationFormatted())
        }
        holder.body.text =
            buildString {
                append("Start:\t\t")
                append(sdf.format(Date(session.startTime)))
                append("\nEnd:\t\t\t")
                append(sdf.format(Date(session.endTime)))
            }
        holder.root.alpha = 1f
        holder.root.isClickable = true
        holder.root.isFocusable = true
    }

    private fun calculateColorForSession(session: AirplaneSession): Int {
        if (session.durationHours > 8) {
            return 0xFF388E3C.toInt() // Color.GREEN
        } else if (session.durationHours > 7) {
            return 0xFFFBC02D.toInt() // Color.YELLOW
        } else if (session.durationHours > 6) {
            return 0xFFD32F2F.toInt() // Color.RED
        }
        return Color.TRANSPARENT
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSessions: List<AirplaneSession>) {
        sessions = newSessions
        notifyDataSetChanged() // redraws the whole list
    }

    override fun getItemCount(): Int = sessions.size + 1
}
