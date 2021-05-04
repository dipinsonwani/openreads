package com.example.mybooks.ui.bookslist.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mybooks.R
import com.example.mybooks.data.db.BooksDatabase
import com.example.mybooks.data.db.entities.Book
import com.example.mybooks.data.repositories.BooksRepository
import com.example.mybooks.ui.bookslist.viewmodel.BooksViewModel
import com.example.mybooks.ui.bookslist.viewmodel.BooksViewModelProviderFactory
import com.example.mybooks.ui.bookslist.ListActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_edit_book.*


class EditBookFragment : Fragment(R.layout.fragment_edit_book) {

    lateinit var viewModel: BooksViewModel
    private val args: EditBookFragmentArgs by navArgs()
    lateinit var book: Book
    lateinit var listActivity: ListActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ListActivity).booksViewModel
        listActivity = activity as ListActivity

        var whatIsClicked = "nothing"

        val database = BooksDatabase(view.context)
        val repository = BooksRepository(database)
        val factory = BooksViewModelProviderFactory(repository)
        var book = args.book

        var viewModel = ViewModelProviders.of(this, factory).get(BooksViewModel::class.java)

            etEditedBookTitle.setText(book.bookTitle)
            etEditedBookAuthor.setText(book.bookAuthor)
            rbEditedRating.rating = book.bookRating

            when (book.bookStatus) {
                "read" -> {
                    ivEditorBookStatusRead.setColorFilter(ContextCompat.getColor(view.context, R.color.orange_300), android.graphics.PorterDuff.Mode.SRC_IN)
                    ivEditorBookStatusInProgress.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
                    ivEditorBookStatusToRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
                    whatIsClicked = "read"
                    rbEditedRating.visibility = View.VISIBLE
                }
                "in_progress" -> {
                    ivEditorBookStatusRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
                    ivEditorBookStatusInProgress.setColorFilter(ContextCompat.getColor(view.context, R.color.orange_300), android.graphics.PorterDuff.Mode.SRC_IN)
                    ivEditorBookStatusToRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
                    whatIsClicked = "in_progress"
                    rbEditedRating.visibility = View.GONE
                }
                "to_read" -> {
                    ivEditorBookStatusRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
                    ivEditorBookStatusInProgress.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
                    ivEditorBookStatusToRead.setColorFilter(ContextCompat.getColor(view.context, R.color.orange_300), android.graphics.PorterDuff.Mode.SRC_IN)
                    whatIsClicked = "to_read"
                    rbEditedRating.visibility = View.GONE
                }
            }

        ivEditorBookStatusRead.setOnClickListener {
            ivEditorBookStatusRead.setColorFilter(ContextCompat.getColor(view.context, R.color.orange_300), android.graphics.PorterDuff.Mode.SRC_IN)
            ivEditorBookStatusInProgress.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
            ivEditorBookStatusToRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
            whatIsClicked = "read"
            rbEditedRating.visibility = View.VISIBLE
        }

        ivEditorBookStatusInProgress.setOnClickListener {
            ivEditorBookStatusRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
            ivEditorBookStatusInProgress.setColorFilter(ContextCompat.getColor(view.context, R.color.orange_300), android.graphics.PorterDuff.Mode.SRC_IN)
            ivEditorBookStatusToRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
            whatIsClicked = "in_progress"
            rbEditedRating.visibility = View.GONE
        }

        ivEditorBookStatusToRead.setOnClickListener {
            ivEditorBookStatusRead.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
            ivEditorBookStatusInProgress.setColorFilter(ContextCompat.getColor(view.context, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
            ivEditorBookStatusToRead.setColorFilter(ContextCompat.getColor(view.context, R.color.orange_300), android.graphics.PorterDuff.Mode.SRC_IN)
            whatIsClicked = "to_read"
            rbEditedRating.visibility = View.GONE
        }

        fabSaveEditedBook.setOnClickListener {
            var bookTitle = etEditedBookTitle.text.toString()
            var bookAuthor = etEditedBookAuthor.text.toString()
            var bookRating = 0.0F

            if (bookTitle.isNotEmpty()) {
                if (bookAuthor.isNotEmpty()){
                    if (whatIsClicked != "nothing") {
                        when(whatIsClicked){
                            "read" -> bookRating = rbEditedRating.rating
                            "in_progress" -> bookRating = 0.0F
                            "to_read" -> bookRating = 0.0F
                        }

                        var bookStatus = whatIsClicked
                        viewModel.updateBook(book.id, bookTitle, bookAuthor, bookRating, bookStatus)

                        it.hideKeyboard()
                        findNavController().popBackStack()
                        findNavController().popBackStack()
                    } else {
                        Snackbar.make(it, "Select book's state", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(it, "Fill in the author", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(it, "Fill in the title", Snackbar.LENGTH_SHORT).show()
            }
        }

        class UndoBookDeletion : View.OnClickListener {
            override fun onClick(view: View) {
                viewModel.upsert(book)
            }
        }

        fabDeleteBook.setOnClickListener{
            viewModel.delete(book)
            it.hideKeyboard()
            findNavController().popBackStack()
            findNavController().popBackStack()

            Snackbar.make(it, "Book Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", UndoBookDeletion())
                .show()
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}