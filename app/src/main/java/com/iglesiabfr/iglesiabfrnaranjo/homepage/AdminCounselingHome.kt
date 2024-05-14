import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.homepage.AdminCounselingRecord
import com.iglesiabfr.iglesiabfrnaranjo.homepage.AdminCounselingSchedule

class AdminCounselingHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_counseling, container, false)
        replaceFragment(AdminCounselingSchedule())

        val navBar : NavigationBarView = view.findViewById(R.id.bottomNav)

        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_schedule -> {
                    replaceFragment(AdminCounselingSchedule())
                    true
                }
                R.id.item_record -> {
                    replaceFragment(AdminCounselingRecord())
                    true
                }

                else -> false
            }
        }
        return view
    }

    private fun replaceFragment(fragment:Fragment) {
        val fragTrans = childFragmentManager.beginTransaction()
        fragTrans.replace(R.id.cousenlingFrame,fragment)
        fragTrans.commit()
    }
}