package es.iesnervion.avazquez.askus.ui.details.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import kotlinx.android.synthetic.main.activity_details_post.*

class DetailsPostActivity : AppCompatActivity() {
    lateinit var currentPost: PostCompletoParaMostrarDTO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_post)
        currentPost = intent.getSerializableExtra("post") as PostCompletoParaMostrarDTO
        //ctlLayout.title = currentPost.tituloPost
        setSupportActionBar(appbar);
        //this line shows back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        appbar.title = currentPost.tituloPost
    }
}
