package es.iesnervion.avazquez.askus.ui.home.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import es.iesnervion.avazquez.askus.ui.auth.view.AuthActivity
import es.iesnervion.avazquez.askus.ui.details.view.DetailsPostActivity
import es.iesnervion.avazquez.askus.ui.fragments.AddPostFragment
import es.iesnervion.avazquez.askus.ui.fragments.HomeFragment
import es.iesnervion.avazquez.askus.ui.fragments.SettingsFragment
import es.iesnervion.avazquez.askus.ui.fragments.moderation.view.ModerationFragment
import es.iesnervion.avazquez.askus.ui.fragments.profileFragment.view.ProfileFragment
import es.iesnervion.avazquez.askus.ui.fragments.tabs.all.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.EXTRA_PARAM_POST
import es.iesnervion.avazquez.askus.utils.AppConstants.HOME_PAGE
import es.iesnervion.avazquez.askus.utils.AppConstants.LOG_OUT
import es.iesnervion.avazquez.askus.utils.AppConstants.MENU_NAV_DRAWER_SIZE
import es.iesnervion.avazquez.askus.utils.AppConstants.MODERATION
import es.iesnervion.avazquez.askus.utils.AppConstants.NEW_POST
import es.iesnervion.avazquez.askus.utils.AppConstants.PROFILE_ANOTHER_USER
import es.iesnervion.avazquez.askus.utils.AppConstants.PROFILE_ANOTHER_USER_FROM_DETAILS
import es.iesnervion.avazquez.askus.utils.AppConstants.PROFILE_CURRENT_USER
import es.iesnervion.avazquez.askus.utils.AppConstants.SETTINGS
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HomeActivityCallback {
    lateinit var viewModel: MainViewModel
    lateinit var tagList: List<TagDTO>
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var selectedItemMenuTitle: String
    lateinit var selectedTag: TagDTO
    lateinit var sharedPreference: SharedPreferences
    lateinit var intentType: String
    var currentUserId = 0
    var tagsObserver: Observer<List<TagDTO>>

    init {
        tagsObserver = Observer { list ->
            tagList = list
            if (list.isNotEmpty() && navigation.menu.size() == MENU_NAV_DRAWER_SIZE) {
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
        intentType = intent.getStringExtra("type") ?: "auth"
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        //        currentUserId = intent.getIntExtra("user_id", 0)
        currentUserId = sharedPreference.getInt("user_id", 0)
        viewModel.loadTags()
        initObservers()
        setSupportActionBar(toolBar)
        actionBarDrawerToggle =
                ActionBarDrawerToggle(this, dlDrawerLayout, toolBar, R.string.drawer_opened,
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
        when (intentType) {
            "loadProfileFragment" -> {
                val idUserClicked = intent.getIntExtra("idUserToLoad", 0)
                val nicknameUserClicked = intent.getStringExtra("nicknameUserToLoad") ?: ""
                onUserClicked(idUser = idUserClicked, nickname = nicknameUserClicked,
                    fromDetails = true)
            }
        }
    }

    private fun initMenu() {
        val menu: Menu = navigation.menu
        tagList = viewModel.allTags().value ?: listOf()
        val sortedList = viewModel.allTags().value?.sortedBy { it.nombre }
        //TODO mejorar esto con funcionalidades de kotlin
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
            R.id.nav_home       -> {
                toolBar.title = resources.getText(R.string.menu_home)
                loadFragmentLoader(HomeFragment.newInstance(0), HOME_PAGE)
                //Este no puede tener add to back stack
            }
            R.id.nav_account    -> {
                toolBar.title = resources.getText(R.string.menu_account)
                loadFragmentLoaderBackStack((ProfileFragment.newInstance(currentUserId)),
                    PROFILE_CURRENT_USER)
            }
            R.id.nav_settings   -> {
                toolBar.title = resources.getText(R.string.menu_settings)
                loadFragmentLoaderBackStack((SettingsFragment.newInstance()), SETTINGS)
            }
            R.id.nav_moderation -> {
                toolBar.title = resources.getText(R.string.menu_moderation)
                //                Toast.makeText(this, "Moderation section - work in progress", Toast.LENGTH_SHORT)
                //                    .show()
                loadFragmentLoaderBackStack((ModerationFragment.newInstance()), MODERATION)
            }
            else                -> {
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
    private fun loadFragmentLoader(fragment: Fragment, tag: String = "") {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment, tag)
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun loadFragmentLoaderBackStack(fragment: Fragment, tag: String = "") {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment, tag)
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onAddPostClicked(idTagUserWasSeeing: Int) {
        //No uso el método loadFragmentLoader porque aquí sí quiero añadir add to back stack
        loadFragmentLoaderBackStack(AddPostFragment.newInstance(idTagUserWasSeeing), NEW_POST)
        toolBar.title = getString(R.string.send_post)
    }

    override fun onPostAdded(idTagUserWasSeeing: Int) {
        toolBar.title = selectedItemMenuTitle
        loadFragmentLoader(HomeFragment.newInstance(idTagUserWasSeeing))
    }

    override fun onPostClicked(post: PostCompletoParaMostrarDTO) {
        val intent = Intent(this, DetailsPostActivity::class.java)
        intent.putExtra(EXTRA_PARAM_POST, post)
        startActivity(intent)
    }

    //    override fun onPostClicked(post: PostCompletoParaMostrarDTO,
    //    v: View,
    //    title: Int,
    //    txt: Int,
    //    author: Int,
    //    tags: Int) {
    //    val intent = Intent(this, DetailsPostActivity::class.java)
    //    intent.putExtra(EXTRA_PARAM_POST, post)
    //    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
    //        Pair<View, String>(v.findViewById(title), VIEW_NAME_TITLE_POST),
    //        Pair<View, String>(v.findViewById(txt), VIEW_NAME_BODY_POST),
    //        Pair<View, String>(v.findViewById(tags), VIEW_NAME_TAGS_POST),
    //        Pair<View, String>(v.findViewById(author), VIEW_NAME_AUTHOR_POST))
    //    // Now we can start the Activity, providing the activity options as a bundle
    //    ActivityCompat.startActivity(this, intent, activityOptions.toBundle())
    //    }
    override fun onUserClicked(idUser: Int, nickname: String, fromDetails: Boolean) {
        if (fromDetails) {
            loadFragmentLoader(ProfileFragment.newInstance(idUser),
                PROFILE_ANOTHER_USER_FROM_DETAILS)
        } else {
            loadFragmentLoader(ProfileFragment.newInstance(idUser), PROFILE_ANOTHER_USER)
        }
        toolBar.title = nickname
    }

    override fun logOut() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(LOG_OUT, true)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.content_frame)
        when {
            (currentFragment?.tag == PROFILE_CURRENT_USER || currentFragment?.tag == PROFILE_ANOTHER_USER || currentFragment?.tag == SETTINGS) -> {
                viewModel.saveStateMenu = 0
                val menuItem: MenuItem = navigation.menu.findItem(viewModel.saveStateMenu)
                        ?: navigation.menu.getItem(0)
                onNavigationItemSelected(menuItem)
                menuItem.isChecked = true
            }
            currentFragment?.tag == PROFILE_ANOTHER_USER_FROM_DETAILS                                                                          -> {
                super.onBackPressed()
            }
            currentFragment?.tag == HOME_PAGE                                                                                                  -> {
                AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(resources.getString(R.string.exit))
                    .setMessage(resources.getString(R.string.user_want_to_exit))
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ -> finish() }
                    .setNegativeButton(resources.getString(R.string.no), null).show()
            }
            else                                                                                                                               -> {
                super.onBackPressed()
                toolBar.title = selectedItemMenuTitle
            }
        }
    }
}
