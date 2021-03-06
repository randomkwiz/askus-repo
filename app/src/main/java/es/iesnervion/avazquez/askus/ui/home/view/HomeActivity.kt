package es.iesnervion.avazquez.askus.ui.home.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.shashank.sony.fancydialoglib.Animation
import com.shashank.sony.fancydialoglib.FancyAlertDialog
import com.shashank.sony.fancydialoglib.Icon
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.DTOs.UserDTO
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
import setVisibilityToGone
import setVisibilityToVisible

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HomeActivityCallback {
    lateinit var viewModel: MainViewModel
    lateinit var tagList: List<TagDTO>
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var selectedItemMenuTitle: String
    lateinit var selectedTag: TagDTO
    lateinit var sharedPreference: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    lateinit var intentType: String
    var isDarkModeOn = false
    var token = ""
    var currentUserId = 0
    var isModerador = false
    var idTagToShowWhenAppIsOpened = 0
    var isFirstTime = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        isFirstTime = savedInstanceState == null
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        intentType = intent.getStringExtra("type") ?: "auth"
        sharedPreference = getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        //        currentUserId = intent.getIntExtra("user_id", 0)
        edit = sharedPreference.edit()
        currentUserId = sharedPreference.getInt("user_id", 0)
        token = sharedPreference.getString("token", "").toString()
        viewModel.loadMyUser(id = currentUserId, token = token)
        isDarkModeOn = sharedPreference.getBoolean("isDarkModeEnabled", false)
        idTagToShowWhenAppIsOpened = sharedPreference.getInt("idTagToShowWhenAppIsOpened", 0)
        //Para que al iniciar se ponga correctamente
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        viewModel.loadTags()
        initObservers()
        setSupportActionBar(toolBar)
        actionBarDrawerToggle =
                ActionBarDrawerToggle(this, dlDrawerLayout, toolBar, R.string.drawer_opened,
                    R.string.drawer_closed)
        dlDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()   //without this it doesn't show the hamburguer menu icon
        navigation.setNavigationItemSelectedListener(this)
        when (intentType) {
            "loadProfileFragment" -> {
                onOtherProfileClicked()
            }
        }
    }

    private fun onOtherProfileClicked() {
        val idUserClicked = intent.getIntExtra("idUserToLoad", 0)
        val nicknameUserClicked = intent.getStringExtra("nicknameUserToLoad") ?: ""
        onUserClicked(idUser = idUserClicked, nickname = nicknameUserClicked, fromDetails = true)
    }

    private fun setSelectedItemMenu() {
        if (isFirstTime) {
            val menuItem: MenuItem =
                    navigation.menu.findItem(idTagToShowWhenAppIsOpened) ?: navigation.menu.getItem(
                        0)
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
        home__lottie_loading.setVisibilityToGone()
        content_frame.setVisibilityToVisible()
    }

    private fun onFullUserLoaded(myUser: UserDTO) {
        if (myUser.id == currentUserId) {
            //guardo datos en sharedpref (is moderador)
            edit.putBoolean("isModerador", myUser.isModerador ?: false)
            edit.commit()
            isModerador = myUser.isModerador ?: false
        }
    }

    private fun onTagsLoaded(list: List<TagDTO>) {
        tagList = list
        if (list.isNotEmpty() && navigation.menu.size() == MENU_NAV_DRAWER_SIZE) {
            val menu: Menu = navigation.menu
            val sortedList = list.sortedBy { it.nombre }
            for (x in sortedList.iterator()) {
                menu.add(0, x.id, 0, x.nombre).isCheckable = true
            }
        }
        if (!intentType.equals("loadProfileFragment")) {
            setSelectedItemMenu()
        }
    }

    private fun initObservers() {
        viewModel.allTags().observe(this, Observer(::onTagsLoaded))
        viewModel.getFullUser().observe(this, Observer(::onFullUserLoaded))
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
            }
            R.id.nav_account    -> {
                toolBar.title = resources.getText(R.string.menu_account)
                loadFragmentLoader((ProfileFragment.newInstance(currentUserId)),
                    PROFILE_CURRENT_USER)
            }
            R.id.nav_settings   -> {
                toolBar.title = resources.getText(R.string.menu_settings)
                loadFragmentLoader((SettingsFragment.newInstance()), SETTINGS)
            }
            R.id.nav_moderation -> {
                if (isModerador) {
                    toolBar.title = resources.getText(R.string.menu_moderation)
                    loadFragmentLoader((ModerationFragment.newInstance()), MODERATION)
                } else {
                    showWantToBeAModeratorDialog()
                }
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

    private fun showWantToBeAModeratorDialog() {
        val color = resources.getString(0 + R.color.colorPrimary)
        FancyAlertDialog.Builder(this).setTitle(getString(R.string.menu_moderation))
            .setBackgroundColor(Color.parseColor(color)) //Don't pass R.color.colorvalue
            .setMessage(resources.getString(R.string.terms_of_use_moderation_sec))
            .setNegativeBtnText(resources.getString(R.string.reject))
            .setPositiveBtnBackground(Color.parseColor(color)) //Don't pass R.color.colorvalue
            .setPositiveBtnText(resources.getString(R.string.accept))
            .setNegativeBtnBackground(Color.parseColor("#bdb5b3")) //Don't pass R.color.colorvalue
            .setAnimation(Animation.SLIDE).isCancellable(true)
            .setIcon(R.drawable.ic_check_black_24dp, Icon.Visible).OnPositiveClicked {
                viewModel.makeUserAModerator(token = token, idUser = currentUserId)
                Toast.makeText(applicationContext, getString(R.string.agree_moderator),
                    Toast.LENGTH_LONG).show()
                viewModel.loadMyUser(id = currentUserId, token = token)
            }.OnNegativeClicked {}.build()
    }

    /**
     * This will load the fragment
     */
    private fun loadFragmentLoader(fragment: Fragment, tag: String = "") {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment, tag)
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    override fun onAddPostClicked(idTagUserWasSeeing: Int) {
        loadFragmentLoader(AddPostFragment.newInstance(idTagUserWasSeeing), NEW_POST)
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
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.content_frame)
        when {
            (currentFragment?.tag == NEW_POST)                        -> {
                toolBar.title = selectedItemMenuTitle
                navigationMenu()
            }
            currentFragment?.tag == PROFILE_ANOTHER_USER_FROM_DETAILS -> {
                super.onBackPressed()
            }
            currentFragment?.tag == HOME_PAGE                         -> {
                AlertDialog.Builder(this).setIcon(R.drawable.ic_exit_to_app_black_24dp)
                    .setTitle(resources.getString(R.string.exit))
                    .setMessage(resources.getString(R.string.user_want_to_exit))
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ -> finish() }
                    .setNegativeButton(resources.getString(R.string.no), null).show()
            }
            else                                                      -> {
                viewModel.saveStateMenu = 0
                navigationMenu()
            }
        }
    }

    private fun navigationMenu() {
        val menuItem: MenuItem =
                navigation.menu.findItem(viewModel.saveStateMenu) ?: navigation.menu.getItem(0)
        onNavigationItemSelected(menuItem)
        menuItem.isChecked = true
    }
}
