package com.example.profilecardlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.Icon
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.profilecardlayout.ui.theme.ProfileCardLayoutTheme
import com.example.profilecardlayout.ui.theme.lightGreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfileCardLayoutTheme {
                UsersApplication()
            }
        }
    }
}

@Composable
fun UsersApplication() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = UserListScreen) {
        composable<UserListScreen> {
            UsersListScreen(userProfileList, navController)
        }
        composable<UserProfileDetailsScreen> {
            val args = it.toRoute<UserProfileDetailsScreen>()
            UserProfileDetailsScreen(args.name, args.imageUrl, args.status, navController)
        }
    }
}

@Composable
fun UsersListScreen(userProfileList: ArrayList<UserProfile>, navController: NavHostController?) {
    Scaffold(
        topBar = {
            AppBar(title = "List of Users", icon = Icons.Default.Home) {

            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.background(Color.LightGray)) {
                items(userProfileList) { userProfile ->
                    ProfileCard(userProfile = userProfile) {
                        navController?.navigate(
                            UserProfileDetailsScreen(
                                userProfile.name, userProfile.imageUrl, userProfile.status
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppBar(title: String, icon: ImageVector, imageClickAction: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            Icon(icon,
                contentDescription = "Content description",
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable { imageClickAction.invoke() })
        },
    )
}

@Composable
fun ProfileCard(userProfile: UserProfile, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        onClick = { clickAction.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(userProfile.imageUrl, userProfile.status, 72.dp)
            ProfileContent(userProfile.name, userProfile.status, Alignment.Start)
        }
    }
}

@Composable
fun ProfilePicture(imageUrl: String, onlineStatus: Boolean, imageSize: Dp) {
    Card(
        shape = CircleShape, border = BorderStroke(
            width = 2.dp, color = if (onlineStatus) MaterialTheme.colorScheme.lightGreen
            else Color.Red
        ), modifier = Modifier.padding(16.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.profile_picture),
            contentDescription = "Content description",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(imageSize)
        )

    }
}

@Composable
fun ProfileContent(name: String, onlineStatus: Boolean, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier.padding(8.dp), horizontalAlignment = alignment
    ) {
        if (onlineStatus) Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.alpha(ContentAlpha.high)
        )
        else Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.alpha(ContentAlpha.disabled),
        )
        Text(
            text = if (onlineStatus) "Is Active" else "Offline",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.alpha(ContentAlpha.disabled),
        )

    }
}

@Composable
fun UserProfileDetailsScreen(
    name: String, imageUrl: String, status: Boolean, navController: NavHostController?
) {
    Scaffold(
        topBar = {
            AppBar(title = name, icon = Icons.AutoMirrored.Filled.ArrowBack) {
                navController?.navigateUp()
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(imageUrl, status, 240.dp)
                ProfileContent(name, status, Alignment.CenterHorizontally)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileDetailsPreview() {
    ProfileCardLayoutTheme {
        UserProfileDetailsScreen(
            name = "Michaela Runnings",
            imageUrl = "https://images.unsplash.com/photo-1485290334039-a3c69043e517?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
            status = true,
            null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    ProfileCardLayoutTheme {
        UsersListScreen(userProfileList, null)
    }
}

@Serializable
object UserListScreen

@Serializable
data class UserProfileDetailsScreen(
    val name: String, val imageUrl: String, val status: Boolean
)