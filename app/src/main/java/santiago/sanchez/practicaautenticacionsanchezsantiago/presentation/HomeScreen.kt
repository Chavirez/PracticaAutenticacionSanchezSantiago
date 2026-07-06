package santiago.sanchez.practicaautenticacionsanchezsantiago.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import santiago.sanchez.practicaautenticacionsanchezsantiago.data.User

@Composable
fun HomeScreen(auth: FirebaseAuth, onLogout: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch users when screen loads
    LaunchedEffect(Unit) {
        db.collection("users").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            if (snapshot != null) {
                users = snapshot.toObjects(User::class.java)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Firestore CRUD", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val user = User(id = db.collection("users").document().id, name = name, phone = phone)
                db.collection("users").document(user.id).set(user)
                name = ""
                phone = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = user.phone, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = {
                            db.collection("users").document(user.id).delete()
                        }) {
                            Text("❌")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            auth.signOut()
            onLogout()
        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
            Text("Logout")
        }
    }
}
