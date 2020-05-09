package es.iesnervion.avazquez.askus.ui.home.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.ui.fragments.AddPostFragment
import es.iesnervion.avazquez.askus.ui.fragments.HomeFragment
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity()
    , NavigationView.OnNavigationItemSelectedListener
    , HomeActivityCallback {
    lateinit var viewModel : MainViewModel
    lateinit var tagList : List<TagDTO>
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    var tagsObserver: Observer<List<TagDTO>>
    init {
        tagsObserver = Observer {
            tagList = it
            if(it.isNotEmpty()){
                val menu: Menu = navigation.menu
                val sortedList = it.sortedBy { it.nombre }
                for (x in sortedList.iterator()) {
                    menu.add(x.nombre).isCheckable = true
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.loadTags()
        initObservers()
        setSupportActionBar(toolBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,
            dlDrawerLayout,
            toolBar,
            R.string.drawer_opened,
            R.string.drawer_closed)
        dlDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()   //without this it doesn't show the hamburguer menu icon
        navigation.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            val menuItem: MenuItem = navigation.menu.getItem(0)
            onNavigationItemSelected(menuItem)
            menuItem.isChecked = true
        }
    }
    private fun initObservers() {
        viewModel.allTags().observe(this,tagsObserver)
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
                toolBar.title = resources.getText(R.string.menu_home)
                loadFragmentLoader(HomeFragment.newInstance(0))
            }
            R.id.nav_account -> {
                toolBar.title = resources.getText(R.string.menu_account)
                Toast.makeText(this, getString(R.string.menu_account), Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                toolBar.title = resources.getText(R.string.menu_settings)
                Toast.makeText(this, getString(R.string.menu_settings), Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                val tagSelected = tagList.first{ it.nombre == item.title }
                toolBar.title = tagSelected.nombre
                loadFragmentLoader(HomeFragment.newInstance(tagSelected.id))
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

    override fun onAddPostClicked() {
        //No uso el método loadFragmentLoader porque aquí sí quiero añadir add to back stack
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, AddPostFragment.newInstance())
        transaction.addToBackStack(null);
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }
}
