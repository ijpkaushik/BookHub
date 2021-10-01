package com.ijpkaushik.bookhub.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ijpkaushik.bookhub.R
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.ijpkaushik.bookhub.database.BookDatabase
import com.ijpkaushik.bookhub.database.BookEntity
import com.ijpkaushik.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*

class DescriptionActivity : AppCompatActivity() {

    lateinit var imgBookImage: ImageView
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookDescription: TextView
    lateinit var btnAddToFavourites: Button
    lateinit var progressBarLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookDescription = findViewById(R.id.txtBookDescription)
        btnAddToFavourites = findViewById(R.id.btnAddToFavourites)
        progressBarLayout = findViewById(R.id.progressBarLayout)
        progressBarLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")

        } else {
            finish()
            Toast.makeText(this@DescriptionActivity,
                "Some unexpected Error Occurred!!",
                Toast.LENGTH_SHORT).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(this@DescriptionActivity,
                "Some unexpected Error Occurred!!",
                Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonObject = object : JsonObjectRequest(Method.POST,
                url,
                jsonParams,
                Response.Listener {
                    try {

                        val success = it.getBoolean("success")

                        if (success) {
                            progressBarLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE

                            val bookJsonObject = it.getJSONObject("book_data")
                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookDescription.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDescription.text.toString(),
                                bookImageUrl
                            )
                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                btnAddToFavourites.text = getString(R.string.remove_from_favorites)
                                val favColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.purple_500
                                )
                                btnAddToFavourites.setBackgroundColor(favColor)
                            } else {
                                btnAddToFavourites.text = getString(R.string.add_to_favourites)
                                val noFavColor =
                                    ContextCompat.getColor(applicationContext, R.color.purple_700)
                                btnAddToFavourites.setBackgroundColor(noFavColor)
                            }

                            btnAddToFavourites.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Book Added To Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFavourites.text = getString(R.string.remove_from_favorites)
                                        val favColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.purple_500
                                        )
                                        btnAddToFavourites.setBackgroundColor(favColor)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Some Error Occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Book Removed From Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFavourites.text = getString(R.string.add_to_favourites)
                                        val nofavColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.purple_700
                                        )
                                        btnAddToFavourites.setBackgroundColor(nofavColor)
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Some Error Occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this@DescriptionActivity,
                                "Error Occurred!!!",
                                Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this@DescriptionActivity,
                            "Some unexpected Error Occurred!!",
                            Toast.LENGTH_SHORT).show()
                    }



                },
                Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity,
                        "Volley Error Occurred!!",
                        Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "082e90c08954f3"
                    return headers
                }

            }
            queue.add(jsonObject)
        } else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }

                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}





