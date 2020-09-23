package hr.ferit.dariocoric.quitter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.from_row_chat.view.*
import kotlinx.android.synthetic.main.from_row_chat.view.textview_from_message
import kotlinx.android.synthetic.main.to_row_chat.view.*
import kotlinx.android.synthetic.main.user_row_following.view.*
import kotlinx.android.synthetic.main.user_row_following.view.user_row_username
import java.sql.Timestamp

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        textview_username_chat.text = SelectedUser.getUser().user

        recyclerview_chat.adapter = adapter
        listenForMessages()
        btn_send_message.setOnClickListener {
            Log.d("ChatFrag", "button pressed")
            sendMessage()
        }
    }

    class ChatToItem(val text: String): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textview_to_message.text = text
        }

        override fun getLayout(): Int {
             return R.layout.to_row_chat
        }
    }

    class ChatFromItem(val text: String): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textview_from_message.text = text
        }

        override fun getLayout(): Int {
            return R.layout.from_row_chat
        }
    }

    class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long) {
        constructor(): this("", "", "", "", 0)
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = SelectedUser.getUser().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null) {
                    if(chatMessage.fromId == fromId) {
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendMessage() {
        val text = edittext_message.text.toString()
        edittext_message.text.clear()

        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = SelectedUser.getUser().uid

        val fromRef = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(fromRef.key!!, text, fromId.toString(), toId, System.currentTimeMillis()/1000)
        fromRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatFrag", chatMessage.text)
            }
        toRef.setValue(chatMessage)
    }

    companion object {
        fun newInstance(): Fragment {
            return ChatFragment()
        }
    }
}