import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.query.RealmResults

class UserAdapter(
    private val users: RealmResults<UserData>,
    private val listener: OnItemClickListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var listUpdateListener: OnListUpdateListener? = null

    interface OnItemClickListener {
        fun onItemClick(user: UserData)
    }

    interface OnListUpdateListener {
        fun onListUpdated()
    }

    fun setOnListUpdateListener(listener: OnListUpdateListener) {
        this.listUpdateListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user, listener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.user_name)

        fun bind(user: UserData, listener: OnItemClickListener) {
            userName.text = user.name

            itemView.setOnClickListener {
                listener.onItemClick(user)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRecyclerView() {
        notifyDataSetChanged()
    }


}
