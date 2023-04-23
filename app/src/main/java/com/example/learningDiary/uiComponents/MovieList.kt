package com.example.learningDiary.uiComponents

import android.os.Handler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import com.example.learningDiary.R
import com.example.learningDiary.Navigation
import com.example.learningDiary.models.Movie
import com.example.learningDiary.viewModel.MoviesViewModel
import coil.compose.rememberImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

fun getFavouredIcon(favoured: Boolean): ImageVector {
    if (favoured) {
        return Icons.Filled.Favorite
    }
    return Icons.Outlined.FavoriteBorder
}

@ExperimentalCoilApi
@Composable
fun MovieList(navController: NavController, moviesViewModel: MoviesViewModel, movies: State<List<Movie>>) {

    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        items(movies.value) { movie ->
            MovieRow(
                movie = movie,
                onMovieClicked = { movieID ->
                    navController.navigate(
                        route = Navigation.DetailScreen.setID(movieID)
                    )
                },
                onFavourIconClicked = { clickedMovie ->
                    coroutineScope.launch {
                        moviesViewModel.toggleFavourite(movie = clickedMovie)
                    }
                }
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
fun MovieRow(movie: Movie, onMovieClicked: (String) -> Unit = {}, onFavourIconClicked: (Movie) -> Unit = {}) {

    // For movie description
    var expanded by remember {
        mutableStateOf(false)
    }
    // For arrow icon
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )
    // For current image
    var imageIndex by remember {
        mutableStateOf(0)
    }
    // Allow to change the image
    var changeImage by remember {
        mutableStateOf(false)
    }
    // For delay between images
    val handler = Handler()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onMovieClicked(movie.id) },
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {

                val painter = rememberImagePainter(
                        data = movie.images[imageIndex],
                        builder = {
                            error(R.drawable.avatar2)
                            crossfade(500)
                        }
                    )
                if (painter.state is ImagePainter.State.Loading) {
                    CircularProgressIndicator()
                }

                Image(
                    painter = painter,      //painterResource(id = R.drawable.avatar2),
                    contentDescription = "${movie.title} Image",
                    contentScale = ContentScale.Crop
                )

                if (expanded && changeImage) {

                    changeImage = false

                    handler.postDelayed(Runnable {

                        imageIndex = if (expanded) {
                            Random.nextInt(0, movie.images.size)
                        } else {
                            0
                        }
                        changeImage = true

                    }, 5000)
                }
                IconButton(
                    onClick = {
                        onFavourIconClicked(movie)
                    }, modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = getFavouredIcon(movie.favoured),
                        contentDescription = "Favour",
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(Color.Black)
            ) {

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = LinearOutSlowInEasing
                            )
                        )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = !expanded
                                changeImage = expanded
                            }
                    ) {
                        Text(
                            text = movie.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.h6.fontSize,
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color.Gray,
                                    offset = Offset(5.0f, 10.0f),
                                    blurRadius = 3f
                                )
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                        )
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "DropDownArrow",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    Column {
                        if (expanded) {
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = Color.Gray)) {
                                        append("Year: ")
                                    }
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(movie.year)
                                    }
                                    withStyle(style = SpanStyle(color = Color.Gray)) {
                                        append("Actors: ")
                                    }
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(movie.actors)
                                    }
                                    withStyle(style = SpanStyle(color = Color.Gray)) {
                                        append("Director: ")
                                    }
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(movie.director)
                                    }
                                    withStyle(style = SpanStyle(color = Color.Gray)) {
                                        append("Rating: ")
                                    }
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(movie.rating.toString())
                                    }
                                }
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(movie.plot)
                                    }
                                }
                            )
                            /*
                            Text(
                                modifier = Modifier.padding(top = 10.dp),
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = Color.Gray)) {
                                        append("Genre: ")
                                    }
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(movie.genres.toString())
                                    }
                                }
                            )

                             */
                        }
                    }
                }
            }
        }
    }
}