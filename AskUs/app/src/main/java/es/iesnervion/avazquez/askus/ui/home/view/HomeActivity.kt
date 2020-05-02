package es.iesnervion.avazquez.askus.ui.home.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.ui.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity()
, NavigationView.OnNavigationItemSelectedListener{
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,
            dlDrawerLayout,
            toolBar,
            R.string.drawer_opened,
            R.string.drawer_closed)
        dlDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()   //without this it doesn't show the hamburguer menu icon
        navigation.setNavigationItemSelectedListener(this)
        val menuItem: MenuItem = navigation.menu.getItem(0)
        onNavigationItemSelected(menuItem)
        menuItem.isChecked = true
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        loadFragment(item)
        dlDrawerLayout.closeDrawers()
        return true
    }
    /**
     * This will change the fragment according to the selected menu item
     */
    private fun loadFragment(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> {
                loadFragmentLoader(HomeFragment.newInstance())
            }
            R.id.nav_account -> {
                Toast.makeText(this, getString(R.string.menu_account), Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                Toast.makeText(this, getString(R.string.menu_settings), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * This will load the fragment
     */
    private fun loadFragmentLoader(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment)
        //transaction.addToBackStack(null);
        transaction.commit()
    }
}
