import android.content.Context
import android.widget.Toast

class utilitarios {

    companion object {
        fun showToast(message: String, context: Context) {
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(context, message, duration)
            toast.show()
        }

    }
}