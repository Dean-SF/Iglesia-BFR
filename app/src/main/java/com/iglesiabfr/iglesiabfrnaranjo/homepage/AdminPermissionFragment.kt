package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.UserList
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.items.UserItem
import java.util.LinkedList

class AdminPermissionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val users = LinkedList<UserItem>()
    private lateinit var adapter: UserList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_permissions, container, false)
        recyclerView = view.findViewById(R.id.userList)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        loadUsers()
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = createUserList()
//        recyclerView.adapter = adapter
//
//        recyclerView.addOnScrollListener(
//            object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                }
//            }
//        )
    }

//    private fun loadUsers() {
//        val startIndex = users.size
//
//        val allUsers = DatabaseConnector.db.query<UserData>("isAdmin == $0", false).find()
//
//        if (allUsers.isNotEmpty()) {
//            val userList = allUsers.map {
//                UserItem(it._id, it.name)
//            }
//
//            users.addAll(userList)
//
//            recyclerView.post {
//                adapter.notifyItemRangeInserted(startIndex, userList.size)
//            }
//        }
//    }
//
//
//    private fun createUserList() : UserList {
//        val userList = UserList(users)
//        userList.onItemClick = { selectedItem ->
//            val selectedUserName = selectedItem.name
//            Toast.makeText(requireContext(), "Selected user: $selectedUserName", Toast.LENGTH_SHORT).show()
//        }
//        return userList
//    }

}