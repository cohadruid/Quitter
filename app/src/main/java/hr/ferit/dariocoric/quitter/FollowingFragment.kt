package hr.ferit.dariocoric.quitter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_following.*
import kotlinx.android.synthetic.main.user_row_following.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FollowingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowingFragment : Fragment() {
    val uid: String = FirebaseAuth.getInstance().uid.toString()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    // private var adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        //adapter = GroupAdapter<ViewHolder>()
        /*adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())*/


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // recyclerview_following.setAdapter(adapter)

        getUsers()
    }

    private fun getUsers() {
        val ref =
            FirebaseDatabase.getInstance().getReference("/users").orderByChild("quitTimeStamp")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            val adapter = GroupAdapter<ViewHolder>()

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    Log.d("FollowingFragment", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) adapter.add(UserItem(user))

                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    SelectedUser.setUser(userItem.user)
                    activity?.showFragment(R.id.fl_fragment_social, ChatFragment.newInstance())
                }
                recyclerview_following.setAdapter(adapter)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.user_row_username.text = user.user
        viewHolder.itemView.user_row_quittime.text = user.datetime
    }

    override fun getLayout(): Int {
        return R.layout.user_row_following
    }
}
