package es.iesnervion.avazquez.askus.ui.home.view

import android.content.Intent
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
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.ui.details.view.DetailsPostActivity
import es.iesnervion.avazquez.askus.ui.fragments.AddPostFragment
import es.iesnervion.avazquez.askus.ui.fragments.HomeFragment
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity()
    , NavigationView.OnNavigationItemSelectedListener
    , HomeActivityCallback {
    lateinit var viewModel: MainViewModel
    lateinit var tagList: List<TagDTO>
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var selectedItemMenuTitle: String
    lateinit var selectedTag: TagDTO
    var tagsObserver: Observer<List<TagDTO>>

    init {
        tagsObserver = Observer { list ->
            tagList = list
            if (list.isNotEmpty() && navigation.menu.size() == 3) {
                val menu: Menu = navigation.menu
                val sortedList = list.sortedBy { it.nombre }
                for (x in sortedList.iterator()) {
                    menu.add(0, x.id, 0, x.nombre).isCheckable = true
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
        initMenu()
        if (savedInstanceState == null) {
            val menuItem: MenuItem = navigation.menu.getItem(0)
            onNavigationItemSelected(menuItem)
            menuItem.isChecked = true
            selectedItemMenuTitle = navigation.menu.getItem(0).title as String
            viewModel.saveStateMenu = menuItem.itemId
        } else {
            val menuItem: MenuItem =
                navigation.menu.findItem(viewModel.saveStateMenu) ?: navigation.menu.getItem(0)
            onNavigationItemSelected(menuItem)
            menuItem.isChecked = true
        }
    }

    private fun initMenu() {
        val menu: Menu = navigation.menu
        tagList = viewModel.allTags().value ?: listOf()
        val sortedList = viewModel.allTags().value?.sortedBy { it.nombre }
        if (!sortedList.isNullOrEmpty()) {
            for (x in sortedList.iterator()) {
                menu.add(0, x.id, 0, x.nombre).isCheckable = true
            }
        }
    }

    private fun initObservers() {
        viewModel.allTags().observe(this, tagsObserver)
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
                selectedTag = tagList.first { it.nombre == item.title }
                toolBar.title = selectedTag.nombre
                loadFragmentLoader(HomeFragment.newInstance(selectedTag.id))
            }
        }
        selectedItemMenuTitle = toolBar.title as String
        viewModel.saveStateMenu = item.itemId
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

    override fun onAddPostClicked(idTagUserWasSeeing: Int) {
        //No uso el método loadFragmentLoader porque aquí sí quiero añadir add to back stack
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, AddPostFragment.newInstance(idTagUserWasSeeing))
        transaction.addToBackStack("addPostFragment")
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        toolBar.title = getString(R.string.send_post)
    }

    override fun onPostAdded(idTagUserWasSeeing: Int) {
        //        if(idTagUserWasSeeing > 0){
        //            selectedTag =
        //                tagList.first{ it.id == idTagUserWasSeeing }
        //            toolBar.title = selectedTag.nombre
        //        }else{
        //            toolBar.title = resources.getText(R.string.menu_home)
        //        }
        toolBar.title = selectedItemMenuTitle
        loadFragmentLoader(HomeFragment.newInstance(idTagUserWasSeeing))
    }

    override fun onPostClicked(post: PostCompletoParaMostrarDTO) {
        val intent = Intent(this, DetailsPostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        toolBar.title = selectedItemMenuTitle
    }
}
