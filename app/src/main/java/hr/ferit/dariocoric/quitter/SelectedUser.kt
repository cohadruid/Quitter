package hr.ferit.dariocoric.quitter

object SelectedUser {
    private lateinit var user: User

    fun getUser() = user
    fun setUser(user: User)  {
        this.user = user
    }
}