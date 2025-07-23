package com.alvindizon.linkedmapissue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.linkedmapissue.ui.theme.LinkedMapIssueTheme

enum class GroupingTabsType { All, Location, Department, Role }
data class TeamMember(
    val name: String,
    val location: String,
    val department: String,
    val role: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkedMapIssueTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Show the LinkedMapScreen instead of Greeting
                    LinkedMapScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun LinkedMapScreen(modifier: Modifier = Modifier) {
    val allTeamMembersList = remember {
        listOf(
            TeamMember("Alice", "NY", "Engineering", "Developer"),
            TeamMember("Bob", "SF", "Design", "Designer"),
            TeamMember("Charlie", "NY", "Engineering", "Tester"),
            TeamMember("Diana", "London", "HR", "HR Manager"),
            TeamMember("Evelyn", "SF", "Engineering", "Developer"),
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    val groupedTeamMembersByType by remember(allTeamMembersList, searchQuery) {
        derivedStateOf {
            val filtered = allTeamMembersList.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
            GroupingTabsType.entries.associateWith { type ->
                when (type) {
                    GroupingTabsType.All -> {
                        val allList = if (searchQuery.isNotEmpty()) filtered else allTeamMembersList
                        if (allList.isNotEmpty()) linkedMapOf("All" to allList.toMutableList()) else linkedMapOf()
                    }
                    GroupingTabsType.Location -> filtered.groupByTo(LinkedHashMap()) { it.location }
                        .toSortedMap()

                    GroupingTabsType.Department -> filtered.groupByTo(LinkedHashMap()) { it.department }
                        .toSortedMap()

                    GroupingTabsType.Role -> filtered.groupByTo(LinkedHashMap()) { it.role }
                        .toSortedMap()
                }
            }
        }
    }
    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name") }
        )
        Text("Grouped Results:", style = MaterialTheme.typography.titleMedium)
        groupedTeamMembersByType.forEach { (tab, map) ->
            Text(tab.name, style = MaterialTheme.typography.titleSmall)
            map.forEach { (key: String, members: List<TeamMember>) ->
                Card(
                    modifier = Modifier.padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("$key: (${members.size})", style = MaterialTheme.typography.bodyLarge)
                        members.forEach { member ->
                            Text("- ${member.name}")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LinkedMapScreenPreview() {
    LinkedMapIssueTheme {
        LinkedMapScreen()
    }
}